package ch.chaos.library.launcher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import ch.chaos.library.Dialogs;


public class TitlePanel extends JComponent {
    
    private final static String TITLE = "ChaosCastle";
    private final static String SUB_TITLE = "Launcher";
    private final static int GAP = 5;
    private final static int SIZE = 64;
    
    private final Font font = new Font("Verdana", Font.PLAIN, SIZE);
    private final Image icon = pickImage(SIZE * 2);
    
    
    private static Image pickImage(int size) {
        Image result = null;
        int bestResult = Integer.MAX_VALUE;
        for (Image image : Dialogs.instance().getAppImageList()) {
            int s = image.getWidth(null);
            int diff = Math.abs(size - s);
            if (diff < bestResult) {
                bestResult = diff;
                result = image;
            }
         }
        return result;
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        Dimension myDim = getPreferredSize();
        Dimension curDim = getSize();
        g2.translate(
                Math.max(0, (curDim.width - myDim.width) / 2),
                Math.max(0, (curDim.height - myDim.height) / 2));
        
        g2.drawImage(icon, GAP, GAP * 2 + 3, GAP + SIZE, GAP * 2 + 3 + SIZE, 0, 0, SIZE * 2, SIZE * 2, this);
        
        Rectangle2D rect1 = g.getFontMetrics(font).getStringBounds(TITLE, g);
        Rectangle2D rect2 = g.getFontMetrics(font).getStringBounds(SUB_TITLE, g);
        int subtitleOffset = (int) (rect1.getWidth() - rect2.getWidth() + 0.5);

        g2.setFont(font);
        g2.setColor(new Color(15 * 15, 0, 0));
        g2.drawString(TITLE, SIZE + GAP * 2, SIZE + GAP + 3);
        g2.setColor(new Color(15 * 15, 15 * 15, 0));
        g2.drawString(TITLE, SIZE + GAP * 2 - 3, SIZE + GAP);
        g2.setColor(new Color(13 * 15, 11 * 15, 3 * 15));
        g2.drawString(TITLE, SIZE + GAP * 2, SIZE + GAP);
        
        g2.setColor(Color.GRAY);
        g2.drawString(SUB_TITLE, SIZE * 2 + GAP * 2 + subtitleOffset, SIZE * 2 + GAP + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(SUB_TITLE, SIZE * 2 + GAP * 2 + subtitleOffset - 2, SIZE * 2 + GAP);
        g2.setColor(Color.BLUE);
        g2.drawString(SUB_TITLE, SIZE * 2 + GAP * 2 + subtitleOffset, SIZE * 2 + GAP);
        
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Graphics2D g = (Graphics2D) getGraphics();
        Rectangle2D rect1 = g.getFontMetrics(font).getStringBounds(TITLE, g);
        Rectangle2D rect2 = g.getFontMetrics(font).getStringBounds(SUB_TITLE, g);
        g.dispose();
        return new Dimension(
                (int) (rect1.getWidth() + GAP * 3 + SIZE * 2 + 0.5),
                (int) (rect1.getHeight() + rect2.getHeight() + GAP * 2 + 0.5));
    }

}
