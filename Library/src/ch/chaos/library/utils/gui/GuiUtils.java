package ch.chaos.library.utils.gui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
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
    
    public static boolean isWindowsLaf() {
        return UIManager.getLookAndFeel().getName().contains("Windows");
    }

    public static boolean isAppleLaf() {
        return UIManager.getLookAndFeel().getName().contains("Mac OS");
    }

    public static boolean isFlatLaf() {
        return UIManager.getLookAndFeel().getClass().getName().startsWith("com.formdev.flatlaf");
    }

    public static <E> void installModelWithTooltips(JComboBox<E> comboBox, E[] values) {
        comboBox.setModel(new DefaultComboBoxModel<>(values));
        comboBox.setRenderer(new ComboBoxTooltipRenderer<>(values));
    }

}
