package ch.chaos.library.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

import ch.chaos.library.graphics.indexed.GraphicsIndexedColorImpl;

/**
 * Once upon a time, some guy from the Java for macOS team, decided that all text on macOS
 * must be antialiased. Not even a default, always. Meaning that {@link RenderingHints#VALUE_TEXT_ANTIALIAS_OFF}
 * is ignored.
 * <p>
 * After all, what could possibly go wrong? antialiased text is surely better that aliased text, and
 * the macOS's graphics are fast enough, right?
 * <p>
 * WRONG. When using indexed images, antialiasing sucks. Especially when the color palette dynamically changes.
 * <p>
 * This class draws text without antialiasing. Thanks to the fact text antialiasing just <i>cannot</i> be disabled,
 * we have to draw text in a temporary black & white image...
 */
public class AliasedTextDrawer {
    
    private final static int GAP = 2;
    
    private BufferedImage binaryImage;
    
    
    public void drawText(Graphics2D g, String text) {
        Rectangle2D textSize = g.getFontMetrics().getStringBounds(text, g);
        int width = (int) (textSize.getWidth() + 0.5);
        int height = (int) (textSize.getHeight() + 0.5);
        width += GAP * 2;
        height += GAP * 2;
        if (binaryImage == null || binaryImage.getWidth() < width || binaryImage.getHeight() < height) {
            binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        }
        
        LineMetrics lineMetrics = g.getFontMetrics().getLineMetrics(text, g);
        int y = (int) (lineMetrics.getAscent());
        
        Graphics2D g2 = binaryImage.createGraphics();
        GraphicsIndexedColorImpl.setupHighSpeed(g2);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height); // Clear background
        g2.setColor(Color.WHITE);
        g2.setFont(g.getFont());
        g2.drawString(text, GAP, GAP + y); // Draw string
        g2.dispose();
        
        // Create transparent image with target color
        Color targetColor = g.getColor();
        byte[] reds = new byte[] {0, (byte) targetColor.getRed()};
        byte[] greens = new byte[] {0, (byte) targetColor.getGreen()};
        byte[] blues = new byte[] {0, (byte) targetColor.getBlue()};
        IndexColorModel colorModel = new IndexColorModel(1, 2, reds, greens, blues, 0);
        BufferedImage image = new BufferedImage(colorModel, binaryImage.getRaster(), false, null);
        
        // Draw in target
        g.drawImage(image, -GAP, -GAP, width, height, null);
    }

}
