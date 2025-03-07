package ch.chaos.library;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import ch.chaos.library.Input.Event;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class Menus {

    private static Menus instance;


    private Menus() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Menus instance() {
        if (instance == null)
            new Menus(); // will set 'instance'
        return instance;
    }

    // CONST


    public static final int mPARENT = Memory.tagUser + 0;
    public static final int mBEFORE = Memory.tagUser + 1;
    public static final int mDIALOG = Memory.tagUser + 2;
    public static final int mNAME = Memory.tagUser + 3;
    public static final int mCHECK = Memory.tagUser + 4;
    public static final int mCHECKED = Memory.tagUser + 5;
    public static final int mCOMM = Memory.tagUser + 6;
    public static final int mENABLE = Memory.tagUser + 7;
    public static final int mCOLOR = Memory.tagUser + 8;

    // TYPE


    public static interface MenuPtr { // Opaque type
    }

    // VAR


    public MenuPtr noMenu;


    public MenuPtr getNoMenu() {
        return this.noMenu;
    }

    public void setNoMenu(MenuPtr noMenu) {
        this.noMenu = noMenu;
    }

    // IMPL


    public static class Menu implements MenuPtr {

        private String text;
        private boolean enabled;
        private Character shortcut;
        private Menu parent;
        private List<Menu> items = new ArrayList<>();


        public Menu(String text, boolean enabled, Character shortcut) {
            this.text = text;
            this.enabled = enabled;
            this.shortcut = shortcut;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Character getShortcut() {
            return shortcut;
        }

        public void setShortcut(Character shortcut) {
            this.shortcut = shortcut;
        }

        public Menu getParent() {
            return parent;
        }

        public void setParent(Menu parent) {
            this.parent = parent;
        }

        public List<Menu> getItems() {
            return items;
        }

    }


    private final List<Menu> menuBar = new ArrayList<>();


    public MenuPtr AddNewMenu(Memory.TagItem tags) {
        String text = Memory.tagString(tags, mNAME, "<Missing>");
        Menu parent = (Menu) Memory.tagObject(tags, mPARENT, null);
        int enabledVal = Memory.tagInt(tags, mENABLE, Memory.YES);
        boolean enabled = (enabledVal == Memory.YES);
        Integer shortcutInt = Memory.tagInteger(tags, mCOMM);
        Character shortCut = (shortcutInt == null ? null : (char) (int) shortcutInt);
        Menu menu = new Menu(text, enabled, shortCut);
        if (parent != null) {
            parent.getItems().add(menu);
            menu.setParent(parent);
        } else {
            menuBar.add(menu);
        }
        installMenu();
        return menu;
    }

    public void ModifyMenu(MenuPtr menu0, Memory.TagItem tags) {
        Menu menu = (Menu) menu0;
        String text = Memory.tagString(tags, mNAME, menu.getText());
        menu.setText(text);
        int enabledVal = Memory.tagInt(tags, mENABLE, menu.isEnabled() ? Memory.YES : Memory.NO);
        boolean enabled = (enabledVal == Memory.YES);
        menu.setEnabled(enabled);
        installMenu();
    }

    public void FreeMenu(IRef<MenuPtr> menuRef) {
        Menu menu = (Menu) menuRef.get();
        if (menu != null) {
            if (menu.getParent() != null) {
                menu.getParent().getItems().remove(menu);
                menu.setParent(null);
            } else {
                menuBar.remove(menu);
            }
        }
        menuRef.set(null);
        installMenu();
    }

    public void clear() {
        menuBar.clear();
    }

    public Object MenuToAddress(MenuPtr menu) {
        return menu;
    }

    public MenuPtr AddressToMenu(Object addr) {
        return (Menu) addr;
    }


    private JFrame mainFrame;
    private JPopupMenu popupMenu;


    public void registerMainFrame(JFrame frame) {
        this.mainFrame = frame;
        installMenu();
        this.mainFrame.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                if (Input.instance().isReadyStat() && popupMenu != null) {
                    popupMenu.show(mainFrame, e.getX(), e.getY());
                }
            }

        });
    }

    private void installMenu() {
        if (SwingUtilities.isEventDispatchThread()) {
            installMenu0();
        } else {
            try {
                SwingUtilities.invokeAndWait(this::installMenu0);
            } catch (InvocationTargetException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void installMenu0() {
        if (mainFrame == null)
            return;
        if (menuBar.isEmpty()) {
            popupMenu = null;
            return;
        }
        JPopupMenu popupMenu = new JPopupMenu();
        for (Menu menu : menuBar) {
            JMenu target = new JMenu(menu.getText());
            target.setEnabled(menu.isEnabled());
            popupMenu.add(target);
            addMenuItems(menu, target);
        }
        this.popupMenu = popupMenu;
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (mainFrame != null)
                    mainFrame.repaint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
    }

    private void addMenuItems(Menu menu, JMenu target) {
        for (Menu item : menu.getItems()) {
            if (isSeparator(item)) {
                target.addSeparator();
            } else {
                JMenuItem menuItem;
                if (item.getItems().isEmpty()) {
                    menuItem = new JMenuItem(item.getText());
                } else {
                    menuItem = new JMenu(item.getText());
                }
                menuItem.setEnabled(item.isEnabled());
                if (item.getShortcut() != null) {
                    KeyStroke keyStroke = KeyStroke.getKeyStroke(item.getShortcut());
                    menuItem.setAccelerator(keyStroke);
                }
                target.add(menuItem);
                if (item.isEnabled()) {
                    menuItem.addActionListener((e) -> menuSelected(item));
                }

                if (!item.getItems().isEmpty()) {
                    addMenuItems(item, (JMenu) menuItem);
                }
            }
        }
    }

    private void menuSelected(Menu menu) {
        Event event = new Event();
        event.type = Input.eMENU;
        event.menu = menu;
        Input.instance().queueEvent(event);
    }

    private static boolean isSeparator(Menu menu) {
        if (menu.isEnabled())
            return false;
        for (int i = 0; i < menu.getText().length(); i++) {
            char ch = menu.getText().charAt(i);
            if (ch != '-')
                return false;
        }
        return true;
    }

    public void begin() {

    }

    public void close() {

    }
}
