package ch.chaos.library.dialogs;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class CheckBoxGadget extends Gadget {

    private final JCheckBox target;


    public CheckBoxGadget() {
        this.target = new JCheckBox();
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
            boolean selected = ((flags & Dialogs.dfSELECT) != 0);
            target.setSelected(selected);
            boolean active = ((flags & Dialogs.dfACTIVE) != 0);
            target.setEnabled(active);
        }
    }

}
