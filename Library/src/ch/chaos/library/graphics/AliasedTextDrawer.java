package ch.chaos.library.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

import ch.chaos.library.graphics.indexed.GraphicsIndexedColorImpl;
import ch.chaos.library.settings.Settings;

/**
 * Once upon a time, some guy from the Java macOS team, decided that all texts on macOS
 * must be antialiased. Not even by default, <i>always</i>. Meaning that {@link RenderingHints#VALUE_TEXT_ANTIALIAS_OFF}
 * is ignored.
 * <p>
 * After all, what could possibly go wrong? antialiased text is surely better than aliased text, and
 * the macOS's graphics are fast enough, right?
 * <p>
 * WRONG. When using indexed images, antialiasing sucks. Especially when the color palette dynamically changes.
 * <p>
 * This class draws text without antialiasing. Thanks to the fact text antialiasing just <i>cannot</i> be disabled
 * on macOS, we have to draw the text in a temporary black & white image, and then blit it on the target.
 * <p>
 * The size of the temporary black and white image automatically grows as needed.
 */
public class AliasedTextDrawer {
    
    // Damns I hate that... but it seems that getting text bounds correctly is just some obscure vodoo...
    private final static double SECURITY = 1.05;
    private final static int GAP = 3;
    
    
    private BufferedImage binaryImage;
    
    
    public void drawText(Graphics2D g, String text) {
        final int Scale = Settings.appMode().getInnerScale();
        
        Rectangle2D textSize = g.getFontMetrics().getStringBounds(text, g);
        int width = (int) (textSize.getWidth() * SECURITY + 0.5);
        int height = (int) (textSize.getHeight() * SECURITY + 0.5);
        width += GAP * 2;
        height += GAP * 2;
        width *= Scale;
        height *= Scale;
        
        if (binaryImage == null || binaryImage.getWidth() < width || binaryImage.getHeight() < height) {
            binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        }
        
        int y = g.getFont().getSize() - 1;
        
        Graphics2D g2 = binaryImage.createGraphics();
        GraphicsIndexedColorImpl.setupHighSpeed(g2);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height); // Clear background
        g2.setColor(Color.WHITE);
        g2.setFont(g.getFont());
        g2.scale(Scale, Scale);
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
        g = (Graphics2D) g.create();
        g.scale(1.0 / Scale, 1.0 / Scale);
        g.drawImage(image, -GAP * Scale, -GAP * Scale,
                width - GAP * Scale, height - GAP * Scale,
                0, 0, width, height, null);
        g.dispose();
    }

}
