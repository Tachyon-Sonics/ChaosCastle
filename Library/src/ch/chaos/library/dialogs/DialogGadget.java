package ch.chaos.library.dialogs;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;
import ch.chaos.library.settings.Settings;

public class DialogGadget extends Gadget {

    private final JPanel panel;
    private String title;
    private Gadget target;
    private List<Gadget> children = new ArrayList<>();


    public DialogGadget() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    @Override
    public JComponent getTarget() {
        return panel;
    }

    @Override
    public void apply(TagItem tags) {
        String title = Memory.tagString(tags, Dialogs.dTEXT, null);
        if (title != null)
            this.title = title;
    }

    @Override
    public void addChild(Gadget child) {
        panel.add(child.getTarget());
        child.setParent(this);
        children.add(child);
    }

    @Override
    public void refresh() {
        if (target == null) {
            if (Settings.appMode().isFullScreen()) {
                target = new DialogGadgetFullScreen(panel, title);
            } else {
                target = new DialogGadgetWindowed(panel, title);
            }
        }
        target.refresh();
    }

    @Override
    public void dispose() {
        if (target != null)
            target.dispose();
    }

}
