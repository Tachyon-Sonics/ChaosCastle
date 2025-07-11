package ch.chaos.library.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;
import ch.chaos.library.settings.Settings;

public class DialogGadgetWindowed extends Gadget {

    private final JDialog target;
    private final JPanel panel;


    public DialogGadgetWindowed(JPanel panel, String title) {
        target = new JDialog(owner());
        target.setTitle(title);
        target.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        target.setMinimumSize(new Dimension(300, 60));
        target.getContentPane().setLayout(new BorderLayout());
        this.panel = panel;
        target.getContentPane().add(panel);
    }

    private static JFrame owner() {
        return (Settings.appMode().isFullScreen() ? null : Dialogs.instance().getMainFrame());
    }

    @Override
    public JComponent getTarget() {
        return panel;
    }

    @Override
    public void apply(TagItem tags) {
        String title = Memory.tagString(tags, Dialogs.dTEXT, null);
        if (title != null)
            target.setTitle(title);
    }

    @Override
    public void addChild(Gadget child) {
        panel.add(child.getTarget());
        child.setParent(this);
    }

    @Override
    public void refresh() {
        if (!target.isVisible()) {
            target.pack();
            target.setLocationRelativeTo(owner());
            target.setVisible(true);
            if (owner() == null)
                target.requestFocus();
        }
    }

    @Override
    public void dispose() {
        try {
            SwingUtilities.invokeAndWait(target::dispose);
        } catch (InvocationTargetException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
