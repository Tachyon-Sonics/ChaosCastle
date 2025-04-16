package ch.chaos.library.dialogs;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Memory;
import ch.chaos.library.Memory.TagItem;

public class GroupGadget extends Gadget {

    private final JPanel target;
    private List<Gadget> children = new ArrayList<>();
    private boolean vertical;
    private int span = 10;


    public GroupGadget() {
        this.target = new JPanel();
    }

    @Override
    public JComponent getTarget() {
        return target;
    }

    @Override
    public void apply(TagItem tags) {
        Integer flags = Memory.tagInteger(tags, Dialogs.dFLAGS);
        if (flags != null) {
            vertical = ((flags & Dialogs.dfVDIR) != 0);
            boolean border = ((flags & Dialogs.dfBORDER) != 0);
            if (border) {
                target.setBorder(new EtchedBorder());
            } else {
                target.setBorder(null);
            }
        }
        Integer span = Memory.tagInteger(tags, Dialogs.dSPAN);
        if (span != null) {
            this.span = span;
        }

        rebuild();
    }

    @Override
    public void addChild(Gadget child) {
        children.add(child);
        child.setParent(this);
        rebuild();
    }

    private List<Gadget> getChildrenSorted() {
        if (vertical) {
            int nbChildren = children.size();
            int columns = Math.max(1, nbChildren / span);
            int lines = (nbChildren + columns - 1) / columns;
            List<Gadget> result = new ArrayList<>();
            for (int i = 0; i < nbChildren; i++) {
                int k = (i % columns) * lines + (i / columns);
                result.add(children.get(k));
            }
            return result;
        } else {
            return children;
        }
    }

    private void rebuild() {
        target.removeAll();
        int nbChildren = children.size();
        if (vertical) {
            int columns = Math.max(1, nbChildren / span);
            target.setLayout(new TableLayout(columns));
        } else {
            target.setLayout(new TableLayout(span));
        }

        for (Gadget child : getChildrenSorted()) {
            GridBagConstraints gbc = child.getConstraints();
            target.add(child.getTarget(), gbc);
        }
    }

}
