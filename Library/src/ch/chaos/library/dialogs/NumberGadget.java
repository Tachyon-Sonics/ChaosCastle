package ch.chaos.library.dialogs;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class NumberGadget extends Gadget {

    private final JSpinner spinner;


    public NumberGadget() {
        spinner = new JSpinner();
    }

    @Override
    public JComponent getTarget() {
        return spinner;
    }

    @Override
    public void apply(TagItem tags) {
        Integer flags = Memory.tagInteger(tags, Dialogs.dFLAGS);
        if (flags != null) {
            boolean active = ((flags & Dialogs.dfACTIVE) != 0);
            spinner.setEnabled(active);
        }

        Integer value = Memory.tagInteger(tags, Dialogs.dINTVAL);
        Integer txtLen = Memory.tagInteger(tags, Dialogs.dTXTLEN);
        if (value == null || txtLen == null)
            return;
        int maxValue = 10;
        while (txtLen > 2) {
            maxValue *= 10;
            txtLen--;
        }
        spinner.setModel(new SpinnerNumberModel((int) value, 0, (int) maxValue - 1, 1));
    }

    @Override
    public void getAttr(TagItem what) {
        if (what.tag == Dialogs.dINTVAL) {
            what.data = ((Number) spinner.getValue()).intValue();
        } else {
            throw new UnsupportedOperationException("getAttr unsupported: " + what.tag);
        }
    }

}
