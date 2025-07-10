package ch.chaos.library.utils.gui;

import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * {@link JPanel} whose preferred/minimum/maximum with and/or height are locked to a multiple
 * of another panel's.
 * <p>
 * If one of the ratio is {@link Double#isNaN()}, this dimension is not locked.
 */
public class LockedJPanel extends JPanel {
    
    private final JPanel other;
    private final double widthRatio;
    private final double heightRatio;
    
    
    public LockedJPanel(JPanel other, double widthRatio, double heightRatio) {
        this.other = other;
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension mySize = super.getPreferredSize();
        Dimension otherSize = other.getPreferredSize();
        return applyDimensions(mySize, otherSize);
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension mySize = super.getMinimumSize();
        Dimension otherSize = other.getMinimumSize();
        return applyDimensions(mySize, otherSize);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension mySize = super.getMaximumSize();
        Dimension otherSize = other.getMaximumSize();
        return applyDimensions(mySize, otherSize);
    }

    private Dimension applyDimensions(Dimension mySize, Dimension otherSize) {
        int width = mySize.width;
        int height = mySize.height;
        if (!Double.isNaN(widthRatio)) {
            width = (int) (otherSize.width * widthRatio + 0.5);
        }
        if (!Double.isNaN(heightRatio)) {
            height = (int) (otherSize.height * heightRatio + 0.5);
        }
        return new Dimension(width, height);
    }

}
