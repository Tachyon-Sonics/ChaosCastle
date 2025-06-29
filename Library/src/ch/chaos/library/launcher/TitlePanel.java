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
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.drawImage(icon, GAP, GAP * 2 + 3, GAP + SIZE, GAP * 2 + 3 + SIZE, 0, 0, SIZE * 2, SIZE * 2, this);
        
        g2.setFont(font);
        g2.setColor(new Color(15 * 15, 0, 0));
        g2.drawString(TITLE, SIZE + GAP * 2, SIZE + GAP + 3);
        g2.setColor(new Color(15 * 15, 15 * 15, 0));
        g2.drawString(TITLE, SIZE + GAP * 2 - 3, SIZE + GAP);
        g2.setColor(new Color(13 * 15, 11 * 15, 3 * 15));
        g2.drawString(TITLE, SIZE + GAP * 2, SIZE + GAP);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Graphics2D g = (Graphics2D) getGraphics();
        Rectangle2D rect = g.getFontMetrics(font).getStringBounds(TITLE, g);
        g.dispose();
        return new Dimension((int) (rect.getWidth() + GAP * 3 + SIZE + 0.5), (int) (rect.getHeight() + GAP * 2 + 0.5));
    }

}
