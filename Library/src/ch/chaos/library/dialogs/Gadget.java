package ch.chaos.library.dialogs;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Dialogs.GadgetPtr;
import ch.chaos.library.Input;
import ch.chaos.library.Input.Event;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public abstract class Gadget implements GadgetPtr {

    protected Gadget parent;
    protected int gbcFill = GridBagConstraints.HORIZONTAL;
    protected int gbcAnchor = GridBagConstraints.CENTER;


    public abstract JComponent getTarget();

    public Gadget getParent() {
        return this.parent;
    }

    public void setParent(Gadget parent) {
        this.parent = parent;
    }

    public Gadget getDialog() {
        Gadget current = this;
        while (!(current instanceof DialogGadgetWindowed) && !(current instanceof DialogGadgetFullScreen)) {
            current = current.getParent();
        }
        return current;
    }

    public void handlePlacement(TagItem tags) {
        Integer flags = Memory.tagInteger(tags, Dialogs.dFLAGS);
        if (flags != null) {
            boolean justify = ((flags & Dialogs.dfJUSTIFY) != 0);
            gbcFill = (justify ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE);
            if (this instanceof GroupGadget)
                gbcFill = GridBagConstraints.HORIZONTAL;
        }
        flags = Memory.tagInteger(tags, Dialogs.dRFLAGS);
        if (flags != null) {
            boolean left = ((flags & Dialogs.dfAUTOLEFT) != 0);
            boolean right = ((flags & Dialogs.dfAUTORIGHT) != 0);
            gbcAnchor = (left ? GridBagConstraints.LINE_START : (right ? GridBagConstraints.LINE_END : GridBagConstraints.CENTER));
        }
    }

    protected GridBagConstraints getConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = gbcFill;
        gbc.anchor = gbcAnchor;
        gbc.insets = new Insets(3, 3, 3, 3);
        return gbc;
    }

    public abstract void apply(TagItem tags);

    public void getAttr(TagItem what) {
        throw new UnsupportedOperationException("This gadget does not support getting attributes");
    }

    public void addChild(Gadget child) {
        throw new UnsupportedOperationException("This gadget type does not support adding childs");
    }

    protected void action(ActionEvent e) {
        Event event = new Event();
        event.type = Input.eGADGET;
        event.gadget = this;
        Input.instance().SendEvent(event);
    }

    public void refresh() {
        throw new UnsupportedOperationException("This gadget type does not support refresh");
    }

    public void dispose() {

    }

}