package ch.chaos.library;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import ch.chaos.library.Input.Event;
import ch.pitchtech.modula.runtime.Runtime;
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
    private SystemTray systemTray;
    private TrayIcon trayIcon;


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
        if (menuBar.isEmpty()) {
            popupMenu = null;
            return;
        }

        // Install tray icon
//        installTrayIconAwt();
        installTrayIconSwing();

        // Install context menu on main frame
        if (mainFrame == null)
            return;
        {
            JPopupMenu popupMenu = new JPopupMenu();
            for (Menu menu : menuBar) {
                JMenu target = new JMenu(menu.getText());
                target.setEnabled(menu.isEnabled());
                popupMenu.add(target);
                addMenuItems(menu, target, null);
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
    }

    void installTrayIconAwt() {
        if (Dialogs.instance().getAppImage() != null && SystemTray.isSupported()) {
            PopupMenu popupMenu = new PopupMenu(Runtime.getAppName());
            for (Menu menu : menuBar) {
                java.awt.Menu target = new java.awt.Menu(menu.getText());
                target.setEnabled(menu.isEnabled());
                popupMenu.add(target);
                addMenuItems(menu, target);
            }

            if (systemTray == null) {
                systemTray = SystemTray.getSystemTray();
                trayIcon = new TrayIcon(Dialogs.instance().getAppImage(), Runtime.getAppName());
                trayIcon.setImageAutoSize(true);
                try {
                    systemTray.add(trayIcon);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
            trayIcon.setPopupMenu(popupMenu);
        }
    }


    private JPopupMenu trayPopupMenu;
    private MouseListener trayMouseListener;
    private Timer trayPopupMenuHider;
    private int nbInsideTrayIconMenuItems;


    /**
     * Although it is possible to show a {@link JPopupMenu} (instead of a {@link PopupMenu}) when right-clicking on the
     * {@link TrayIcon}, the menu will not automatically disappear when clicking outside of it.
     * <p>
     * Hence, we use this {@link MouseAdapter}, that listen to mouse-enter and mouse-exit events on every menu items,
     * in order to check whether the mouse is over the popup menu or not. If the mouse is not over the popup menu
     * for more than 1 second, the popup menu is hidden.
     */
    private class EnterExitAdapter extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            nbInsideTrayIconMenuItems++;
            if (nbInsideTrayIconMenuItems > 0) {
                disablePopupHider();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            nbInsideTrayIconMenuItems--;
            if (nbInsideTrayIconMenuItems <= 0) {
                enablePopupHider();
            }
        }

        private void enablePopupHider() {
            disablePopupHider();
            trayPopupMenuHider = new Timer((int) TimeUnit.SECONDS.toMillis(1), (ActionEvent et) -> {
                if (trayPopupMenuHider != et.getSource())
                    return;
                trayPopupMenu.setVisible(false);
                trayPopupMenuHider = null;
            });
            trayPopupMenuHider.setRepeats(false);
            trayPopupMenuHider.start();
        }

        private void disablePopupHider() {
            if (trayPopupMenuHider != null) {
                trayPopupMenuHider.stop();
                trayPopupMenuHider = null;
            }
        }

    }


    /**
     * Install a {@link TrayIcon} with a swing popup menu ({@link JPopupMenu} instead of AWT's {@link PopupMenu}).
     * <p>
     * We need to listen to mouse events and to show the popup menu manually.
     * <p>
     * For some reason, AWT's {@link PopupMenu} sucks as it does not use the correct look&feel, it even seems
     * to use CDE Look&feel on Linux (!). Hopyfully, using {@link JPopupMenu} is better, despite of a few
     * caveats (see {@link EnterExitAdapter}).
     * TODO (3) in full screen mode, menu is too small because uiScale is set to 1
     */
    void installTrayIconSwing() {
        if (Dialogs.instance().getAppImage() != null && SystemTray.isSupported()) {
            trayPopupMenu = new JPopupMenu(Runtime.getAppName());
            for (Menu menu : menuBar) {
                JMenu target = new JMenu(menu.getText());
                EnterExitAdapter activityAdapter = new EnterExitAdapter();
                target.addMouseListener(activityAdapter);
                target.setEnabled(menu.isEnabled());
                trayPopupMenu.add(target);
                addMenuItems(menu, target, (item) -> {
                    item.addMouseListener(activityAdapter);
                });
            }

            if (systemTray == null) {
                systemTray = SystemTray.getSystemTray();
                trayIcon = new TrayIcon(Dialogs.instance().getAppImage(), Runtime.getAppName());
                trayIcon.setImageAutoSize(true);
                try {
                    systemTray.add(trayIcon);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }

                if (trayMouseListener != null) {
                    trayIcon.removeMouseListener(trayMouseListener);
                    trayMouseListener = null;
                }

                trayMouseListener = new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        maybeShowPopup(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        maybeShowPopup(e);
                    }

                    private void maybeShowPopup(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            nbInsideTrayIconMenuItems = 0;
                            GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                                    .getDefaultConfiguration();
                            AffineTransform screenTransform = graphicsConfiguration.getDefaultTransform();
                            double scaleX = screenTransform.getScaleX();
                            double scaleY = screenTransform.getScaleY();
                            trayPopupMenu.setLocation((int) (e.getX() / scaleX) - 10, (int) (e.getY() / scaleY) - 10);
                            trayPopupMenu.setInvoker(trayPopupMenu);
                            trayPopupMenu.setVisible(true);
                        }
                    }
                };
                trayIcon.addMouseListener(trayMouseListener);
            }
        }
    }

    private void addMenuItems(Menu menu, JMenu target, Consumer<JComponent> onMenuItemCreated) {
        for (Menu item : menu.getItems()) {
            if (isSeparator(item)) {
                JComponent separator = new JPopupMenu.Separator();
                target.add(separator);
                if (onMenuItemCreated != null) {
                    onMenuItemCreated.accept(separator);
                }
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
                if (onMenuItemCreated != null) {
                    onMenuItemCreated.accept(menuItem);
                }

                if (!item.getItems().isEmpty()) {
                    addMenuItems(item, (JMenu) menuItem, onMenuItemCreated);
                }
            }
        }
    }

    private void addMenuItems(Menu menu, java.awt.Menu target) {
        for (Menu item : menu.getItems()) {
            if (isSeparator(item)) {
                target.addSeparator();
            } else {
                MenuItem menuItem;
                if (item.getItems().isEmpty()) {
                    menuItem = new MenuItem(item.getText());
                } else {
                    menuItem = new java.awt.Menu(item.getText());
                }
                menuItem.setEnabled(item.isEnabled());
                if (item.getShortcut() != null) {
                    int key = KeyEvent.getExtendedKeyCodeForChar(item.getShortcut());
                    menuItem.setShortcut(new MenuShortcut(key));
                }
                target.add(menuItem);
                if (item.isEnabled()) {
                    menuItem.addActionListener((e) -> menuSelected(item));
                }

                if (!item.getItems().isEmpty()) {
                    addMenuItems(item, (java.awt.Menu) menuItem);
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
