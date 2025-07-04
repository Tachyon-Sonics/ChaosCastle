package ch.chaos.library.utils.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TableLayout extends GridBagLayout {

    private static final long serialVersionUID = 5511537663228706394L;

    private final int nbColumns;
    private GridBagConstraints dfltConstraint = new GridBagConstraints();
    private int x = 0;
    private int y = 0;
    private List<BitSet> reservedColumns = new ArrayList<>();


    /**
     * @param nbColumns number of columns, or <tt>-1</tt> to display all elements in a single row
     */
    public TableLayout(int nbColumns) {
        super();
        this.nbColumns = nbColumns;
    }

    public TableLayout(int nbColumns, GridBagConstraints dfltConstraint) {
        this(nbColumns);
        this.dfltConstraint = (GridBagConstraints) dfltConstraint.clone();
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        GridBagConstraints gbc = (GridBagConstraints) constraints;
        if (gbc == null)
            gbc = (GridBagConstraints) dfltConstraint;
        gbc = (GridBagConstraints) gbc.clone();
        gbc.gridx = x;
        gbc.gridy = y;
        if (gbc.gridheight > 1) {
            for (int i = 1; i < gbc.gridheight; i++) {
                for (int j = 0; j < gbc.gridwidth; j++)
                    reserveColumn(i, x + j);
            }
        }
        if (x + gbc.gridwidth > nbColumns && nbColumns > 0)
            throw new IllegalArgumentException("Element at column " + x + " has width " + gbc.gridwidth + " which is above the number of columns " + nbColumns);
        if (gbc.gridwidth < 1)
            throw new IllegalArgumentException("GridBagConstraint.gridwidth must be > 0");
        super.addLayoutComponent(comp, gbc);
        skipCells(gbc.gridwidth);
    }

    public void skipCells(int nbCells) {
        BitSet reserved = getReservedColumns();
        for (int i = 0; i < nbCells; i++) {
            do {
                x++;
                if (nbColumns > 0 && x >= nbColumns) {
                    x = 0;
                    y++;
                    nextLine();
                    reserved = getReservedColumns();
                }
            } while (reserved.get(x));
        }
    }

    // reserved columns handling

    private void nextLine() {
        if (reservedColumns.isEmpty())
            return;
        reservedColumns.remove(0);
    }

    private BitSet getReservedColumns() {
        if (reservedColumns.isEmpty())
            return new BitSet();
        return reservedColumns.get(0);
    }

    private void reserveColumn(int yDelta, int x) {
        while (reservedColumns.size() <= yDelta)
            reservedColumns.add(new BitSet());
        BitSet colSet = reservedColumns.get(yDelta);
        colSet.set(x);
    }

}
