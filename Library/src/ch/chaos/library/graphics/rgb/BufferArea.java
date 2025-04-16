package ch.chaos.library.graphics.rgb;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import ch.chaos.library.Graphics.AreaPtr;
import ch.chaos.library.graphics.AreaBase;

/**
 * {@link AreaPtr} implementation for off-screen buffers (type {@link Graphics0#atDISPLAY})
 */
class BufferArea extends AreaBase implements AreaPtr {

    private final GraphicsRgbColorImpl graphicsRgbColorImpl;
    private final int width;
    private final int height;
    final int nbColors;
    private final int scale;
    private final boolean mask;
    private final boolean offBuffer;

    final byte[] rPalette;
    final byte[] gPalette;
    final byte[] bPalette;
    private BufferedImage image;
    private Graphics2D g;
    private Runnable screenPaletteListener;


    public BufferArea(GraphicsRgbColorImpl graphicsRgbColorImpl, int width, int height, int nbColors, int scale,
            boolean mask, boolean offBuffer) {
        this.graphicsRgbColorImpl = graphicsRgbColorImpl;
        this.width = width;
        this.height = height;
        this.nbColors = nbColors;
        this.scale = scale;
        this.mask = mask;
        this.offBuffer = offBuffer;

        if (nbColors == this.graphicsRgbColorImpl.nbScreenColor) {
            rPalette = this.graphicsRgbColorImpl.rScreenPalette;
            gPalette = this.graphicsRgbColorImpl.gScreenPalette;
            bPalette = this.graphicsRgbColorImpl.bScreenPalette;
            screenPaletteListener = this::paletteChanged;
            this.graphicsRgbColorImpl.screenPaletteChangeListeners.add(screenPaletteListener);
        } else {
            rPalette = new byte[nbColors];
            gPalette = new byte[nbColors];
            bPalette = new byte[nbColors];
            rPalette[nbColors - 1] = (byte) 255;
            gPalette[nbColors - 1] = (byte) 255;
            bPalette[nbColors - 1] = (byte) 255;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void paletteChanged() {
//        image = null; // Force reconstruction with new IndexColorModel
//        if (g != null)
//            g.dispose();
//        g = null;
    }

    @Override
    public BufferedImage getInternalImage() {
        return getExternalImage();
    }

    @Override
    public BufferedImage getExternalImage() {
        if (image == null) {
            int transparency;
            if (offBuffer) {
                transparency = Transparency.OPAQUE;
            } else if (GraphicsRgbColorImpl.USE_ALPHA_TRANSPARENCY) {
                transparency = Transparency.TRANSLUCENT;
            } else if (mask) {
                transparency = Transparency.BITMASK;
            } else {
                transparency = Transparency.OPAQUE;
            }
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            image = gc.createCompatibleImage(width * scale, height * scale, transparency);
        }
        return image;
    }

    @Override
    public void setPalette(int color, int red, int green, int blue) {
        rPalette[color] = (byte) red;
        gPalette[color] = (byte) green;
        bPalette[color] = (byte) blue;
        paletteChanged();
    }

    @Override
    public Color getColor(int pen) {
        int red = rPalette[pen] & 0xff;
        int green = gPalette[pen] & 0xff;
        int blue = bPalette[pen] & 0xff;
        return new Color(red, green, blue);
    }

    private void ensureGraphics() {
        if (image == null)
            getExternalImage();
        if (g == null) {
            g = image.createGraphics();
            g.scale(scale, scale);
            if (GraphicsRgbColorImpl.USE_ALPHA_TRANSPARENCY) {
                // Make initially transparent
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 0));
                g2.setComposite(AlphaComposite.Src);
                g2.fillRect(0, 0, width, height);
                g2.dispose();
            }
            g.setColor(new Color(0, 0, 0, 255));
            if (mask)
                g.setComposite(AlphaComposite.Src);
            GraphicsRgbColorImpl.setupHighQuality(g);
        }
    }

    @Override
    public void writePixel(int x, int y, int pen) {
        ensureGraphics();
        g.setColor(getColor(pen));
        g.fillRect(x, y, 1, 1);
    }

    @Override
    public void draw(Consumer<Graphics2D> operation) {
        ensureGraphics();
        operation.accept(g);
    }

    @Override
    public void close() {
        if (g != null)
            g.dispose();
        if (screenPaletteListener != null) {
            this.graphicsRgbColorImpl.screenPaletteChangeListeners.remove(screenPaletteListener);
        }
    }

}