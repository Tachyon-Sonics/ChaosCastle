package ch.chaos.library.dialogs;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class CycleGadget extends Gadget {

    private final JButton target;


    public CycleGadget() {
        target = new JButton();
        target.addActionListener(super::action);
    }

    @Override
    public JComponent getTarget() {
        return target;
    }

    @Override
    public void apply(TagItem tags) {
        String text = Memory.tagString(tags, Dialogs.dTEXT, null);
        if (text != null)
            target.setText(text);
        Integer flags = Memory.tagInteger(tags, Dialogs.dFLAGS);
        if (flags != null) {
            boolean active = ((flags & Dialogs.dfACTIVE) != 0);
            target.setEnabled(active);
        }
    }

}
