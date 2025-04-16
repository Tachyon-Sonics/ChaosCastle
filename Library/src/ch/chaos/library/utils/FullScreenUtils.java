package ch.chaos.library.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import ch.chaos.library.Dialogs;
import ch.chaos.library.dialogs.TableLayout;

public class FullScreenUtils {

    private final static Map<JComponent, JPanel> cmp2panels = new HashMap<>();


    public static void addFullScreenDialog(JComponent component, String title) {
        assert SwingUtilities.isEventDispatchThread();
        Dialogs.instance().hideArea();
        JFrame mainFrame = Dialogs.instance().getMainFrame();
        JPanel screenPanel = new JPanel(new TableLayout(1));
        screenPanel.setBackground(Color.BLACK);
        mainFrame.getContentPane().add(screenPanel);
        JPanel panel = new JPanel();
        screenPanel.add(panel);
        panel.setBorder(new TitledBorder(title));
        panel.add(component);
        mainFrame.validate();
        cmp2panels.put(component, screenPanel);
    }

    public static void removeFullScreenDialog(JComponent component) {
        assert SwingUtilities.isEventDispatchThread();
        JPanel panel = cmp2panels.remove(component);
        assert panel != null : "Component was not added as a full screen dialog";
        JFrame mainFrame = Dialogs.instance().getMainFrame();
        mainFrame.getContentPane().remove(panel);
        Dialogs.instance().showArea();
        mainFrame.validate();
        mainFrame.repaint();
        mainFrame.requestFocus(); // or else keyboard does not longer work...
    }

}
