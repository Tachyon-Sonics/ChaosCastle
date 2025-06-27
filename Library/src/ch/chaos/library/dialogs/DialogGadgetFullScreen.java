package ch.chaos.library.dialogs;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;
import ch.chaos.library.utils.TableLayout;

public class DialogGadgetFullScreen extends Gadget {

    private final JPanel container;
    private final JLabel label;
    private final JPanel target;
    private boolean visible;


    public DialogGadgetFullScreen(JPanel panel, String title) {
        container = new JPanel();
        container.setLayout(new TableLayout(1));
        container.setBackground(Color.BLACK);
        label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setText(title);
        container.add(label);
        target = panel;
        panel.setBackground(Color.BLACK);
        target.setMinimumSize(new Dimension(300, 60));
        container.add(target);
    }

    @Override
    public JComponent getTarget() {
        return target;
    }

    @Override
    public void apply(TagItem tags) {
        String title = Memory.tagString(tags, Dialogs.dTEXT, null);
        if (title != null) {
            label.setText(title);
        }
    }

    @Override
    public void addChild(Gadget child) {
        target.add(child.getTarget());
        child.setParent(this);
    }

    @Override
    public void refresh() {
        if (!visible && Dialogs.instance().getMainFrame() != null) {
            Dialogs.instance().hideArea();
            target.validate();
            JFrame mainFrame = Dialogs.instance().getMainFrame();
            mainFrame.getContentPane().add(container);
            mainFrame.validate();
            mainFrame.repaint();
            label.repaint();
            visible = true;
        } else if (visible) {
            container.repaint();
        }
    }

    @Override
    public void dispose() {
        if (visible) {
            JFrame mainFrame = Dialogs.instance().getMainFrame();
            if (mainFrame != null)
                mainFrame.getContentPane().remove(container);
            visible = false;
            Dialogs.instance().showArea();
        }
    }

}
