package ch.chaos.library.dialogs;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class ToggleGadget extends Gadget {

    private final JToggleButton toggle;


    public ToggleGadget() {
        this.toggle = new JToggleButton("Loading...");
        toggle.addActionListener(super::action);
    }

    @Override
    public JComponent getTarget() {
        return toggle;
    }

    @Override
    public void apply(TagItem tags) {
        String text = Memory.tagString(tags, Dialogs.dTEXT, null);
        if (text != null)
            toggle.setText(text);
        Integer flags = Memory.tagInteger(tags, Dialogs.dFLAGS);
        if (flags != null) {
            boolean selected = ((flags & Dialogs.dfSELECT) != 0);
            toggle.setSelected(selected);
        }
    }

}
