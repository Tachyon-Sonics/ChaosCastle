package ch.chaos.library.utils.gui;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class SelectAllOnFocus {

    private final static Map<Component, Long> timeMap = new WeakHashMap<>();


    public static void installSelectAllOnFocus(JTextComponent textField) {
        textField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (GuiUtils.isAppleLaf()) {
                    SwingUtilities.invokeLater(textField::selectAll);
                    /*
                     * If the focus is gained by a click on the editor, the OS X l&f will respond to the click by clearing the selection
                     * and setting the caret position. To make the click select all, we have to notify the click handler below that focus
                     * has just been gained, and the click handler will select all again.
                     *
                     * We do not want subsequent clicks to select all, but to set caret position, hence the use of a timeout (in case focus
                     * is gained through the TAB key).
                     *
                     * We still select all here for the case focus is gained through the TAB key.
                     */
                    long time = System.currentTimeMillis();
                    timeMap.put(e.getComponent(), time);
                } else {
                    SwingUtilities.invokeLater(textField::selectAll);
                }
            }

        });
        if (GuiUtils.isAppleLaf()) {
            textField.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    Long time = timeMap.get(e.getComponent());
                    if (time == null)
                        return;
                    if (Math.abs(time - System.currentTimeMillis()) < 200) {
                        timeMap.remove(e.getComponent());
                        SwingUtilities.invokeLater(textField::selectAll);
                    }
                }

            });
        }

    }

}
