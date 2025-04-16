package ch.chaos.library.dialogs;

import javax.swing.JButton;
import javax.swing.JComponent;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class ButtonGadget extends Gadget {

    private final JButton button;


    public ButtonGadget() {
        this.button = new JButton();
        button.addActionListener(super::action);
    }

    @Override
    public JComponent getTarget() {
        return button;
    }

    @Override
    public void apply(TagItem tags) {
        String text = Memory.tagString(tags, Dialogs.dTEXT, null);
        if (text != null)
            button.setText(text);
    }

}
