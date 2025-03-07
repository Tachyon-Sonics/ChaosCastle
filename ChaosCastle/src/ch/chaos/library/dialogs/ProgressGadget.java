package ch.chaos.library.dialogs;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class ProgressGadget extends Gadget {

    private final JProgressBar target;


    public ProgressGadget() {
        target = new JProgressBar();
    }

    @Override
    public JComponent getTarget() {
        return target;
    }

    @Override
    public void apply(TagItem tags) {
        Integer fill = Memory.tagInteger(tags, Dialogs.dFILL);
        if (fill == null)
            return;
        int percent = fill * 100 / 65535;
        if (percent < 0)
            percent = 0;
        else if (percent > 100)
            percent = 100;
        target.setValue(percent);
        target.repaint();
        if (getParent() != null)
            getParent().refresh();
    }

}
