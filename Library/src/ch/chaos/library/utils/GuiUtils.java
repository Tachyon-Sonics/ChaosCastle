package ch.chaos.library.utils;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class GuiUtils {

    public static void setupLookAndFeel() {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Thou shalt call me from EDT");
        if (System.getProperty("swing.defaultlaf") != null) {
            return; // Laf manually specified
        }
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }
    
}
