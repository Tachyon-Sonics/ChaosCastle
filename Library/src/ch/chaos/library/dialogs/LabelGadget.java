package ch.chaos.library.dialogs;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class LabelGadget extends Gadget {

    private final JLabel label;


    public LabelGadget() {
        this.label = new JLabel();
    }

    @Override
    public JComponent getTarget() {
        return label;
    }

    @Override
    public void apply(TagItem tags) {
        String text = Memory.tagString(tags, Dialogs.dTEXT, null);
        if (text != null)
            label.setText(text);
    }

}
