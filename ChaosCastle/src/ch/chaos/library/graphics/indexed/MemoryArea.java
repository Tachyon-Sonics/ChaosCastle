package ch.chaos.library.graphics.indexed;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.BitSet;
import java.util.function.Consumer;

import ch.chaos.library.Graphics.AreaPtr;
import ch.chaos.library.graphics.AreaBase;

/**
 * {@link AreaPtr} implementation for off-screen buffers (type {@link Graphics0#atDISPLAY})
 */
class MemoryArea extends AreaBase implements AreaPtr {

    private final int width;
    private final int height;
    private final int nbColors;

    private final WritableRaster raster;

    /*
     * We need to separate ExtPalette, that contains the actual palette, and
     * IntPalette, that is a grayscale palette.
     * 
     * IntPalette is used for all off-screen rendering, because we need a palette with unique
     * colors, as Graphics2D does not allow us to choose a color index, and may fail on a palette
     * with two or more identical colors.
     * 
     * ExtPalette is only used when drawing on screen
     */
    private final byte[] rExtPalette;
    private final byte[] gExtPalette;
    private final byte[] bExtPalette;
    final byte[] rIntPalette;
    final byte[] gIntPalette;
    final byte[] bIntPalette;
    private final BufferedImage image;
    private final Graphics2D g;

    private BitSet usedPens = new BitSet();
    private Rectangle modified = null;


    public MemoryArea(GraphicsIndexedColorImpl graphicsIndexedColorImpl, int width, int height, int nbColors, int scale) {
        this.width = width;
        this.height = height;
        this.nbColors = nbColors;

        int nbBits = GraphicsIndexedColorImpl.bitsForColors(nbColors);
        SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width * scale, height * scale, 1, width * scale, new int[] { 0 });
        DataBuffer dataBuffer = sampleModel.createDataBuffer();
        raster = Raster.createPackedRaster(dataBuffer, width * scale, height * scale, nbBits, null);
        if (nbColors == graphicsIndexedColorImpl.nbScreenColor) {
            rExtPalette = graphicsIndexedColorImpl.rScreenPalette;
            gExtPalette = graphicsIndexedColorImpl.gScreenPalette;
            bExtPalette = graphicsIndexedColorImpl.bScreenPalette;
        } else {
            rExtPalette = new byte[nbColors];
            gExtPalette = new byte[nbColors];
            bExtPalette = new byte[nbColors];
            rExtPalette[nbColors - 1] = (byte) 255;
            gExtPalette[nbColors - 1] = (byte) 255;
            bExtPalette[nbColors - 1] = (byte) 255;
        }
        rIntPalette = new byte[nbColors];
        gIntPalette = new byte[nbColors];
        bIntPalette = new byte[nbColors];
        for (int i = 0; i < nbColors; i++) {
            int grey = i * 255 / (nbColors - 1);
            rIntPalette[i] = (byte) grey;
            gIntPalette[i] = (byte) grey;
            bIntPalette[i] = (byte) grey;
        }
        IndexColorModel colorModel = new IndexColorModel(nbBits, nbColors, rIntPalette, gIntPalette, bIntPalette);
        image = new BufferedImage(colorModel, raster, false, null);
        g = image.createGraphics();
        g.scale(scale, scale);
        GraphicsIndexedColorImpl.setupHighSpeed(g);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3.0f));
        g.setFont(new Font("Verdana", Font.PLAIN, 12)); // Seems to be the best one at SCALE 1
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNbColors() {
        return nbColors;
    }

    public BitSet getUsedPens() {
        return usedPens;
    }

    public byte[] getrExtPalette() {
        return rExtPalette;
    }

    public byte[] getgExtPalette() {
        return gExtPalette;
    }

    public byte[] getbExtPalette() {
        return bExtPalette;
    }

    public boolean isModified(int x, int y, int width, int height) {
        if (modified == null)
            return false;
        return modified.intersects(x, y, width, height);
    }

    void modify(int x, int y, int width, int height) {
        if (modified == null) {
            modified = new Rectangle(x, y, width, height);
        } else {
            modified.add(new Rectangle(x, y, width, height));
        }
    }

    void clearModified() {
        modified = null;
    }

    @Override
    public BufferedImage getInternalImage() {
        return image;
    }

    @Override
    public BufferedImage getExternalImage() {
        int nbBits = GraphicsIndexedColorImpl.bitsForColors(nbColors);
        IndexColorModel colorModel = new IndexColorModel(nbBits, nbColors, rExtPalette, gExtPalette, bExtPalette);
        return new BufferedImage(colorModel, raster, false, null);
    }

    @Override
    public void setPalette(int color, int red, int green, int blue) {
        rExtPalette[color] = (byte) red;
        gExtPalette[color] = (byte) green;
        bExtPalette[color] = (byte) blue;
    }

    @Override
    public Color getColor(int pen) {
        if (pen >= nbColors)
            pen = nbColors - 1;
        this.usedPens.set(pen);
        int red = rIntPalette[pen] & 0xff;
        int green = gIntPalette[pen] & 0xff;
        int blue = bIntPalette[pen] & 0xff;
        return new Color(red, green, blue);
    }

    @Override
    public void writePixel(int x, int y, int pen) {
        g.setColor(getColor(pen));
        g.fillRect(x, y, 1, 1);
    }

    public void writeUnscaledPixel(int x, int y, int pen) {
        raster.setPixel(x, y, new int[] { pen });
    }

    @Override
    public void draw(Consumer<Graphics2D> operation) {
        operation.accept(g);
    }

    @Override
    public void close() {
        if (g != null)
            g.dispose();
    }

}