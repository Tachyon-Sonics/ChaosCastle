package ch.chaos.library.graphics.indexed;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import ch.chaos.library.Graphics;
import ch.chaos.library.graphics.AreaBase;

public class DoubleBufferArea extends AreaBase {

    private final MemoryArea area1;
    private final MemoryArea area2;
    private boolean first;


    public DoubleBufferArea(GraphicsIndexedColorImpl graphicsIndexedColorImpl, int width, int height, int nbColors, int scale) {
        this.area1 = new MemoryArea(graphicsIndexedColorImpl, width, height, nbColors, scale);
        this.area2 = new MemoryArea(graphicsIndexedColorImpl, width, height, nbColors, scale);
    }

    private MemoryArea area() {
        return first ? area1 : area2;
    }

    public byte[] getrExtPalette() {
        return area().getrExtPalette();
    }

    public byte[] getgExtPalette() {
        return area().getgExtPalette();
    }

    public byte[] getbExtPalette() {
        return area().getbExtPalette();
    }

    public MemoryArea switchArea() {
        MemoryArea result = area();
        first = !first;
        return result;
    }

    public MemoryArea updateArea() {
        MemoryArea current = area();
        MemoryArea offscreen = (current == area1 ? area2 : area1);
        offscreen.draw((g) -> {
            g = (Graphics2D) g.create();
            Graphics.resetScale(g);
            g.drawImage(current.getInternalImage(), 0, 0, null);
            g.dispose();
        });
        return offscreen;
    }

    public boolean isFirstBuffer() {
        return first;
    }

    @Override
    public void draw(Consumer<Graphics2D> operation) {
        area().draw(operation);
    }

    @Override
    public void setPalette(int color, int red, int green, int blue) {
        area1.setPalette(color, red, green, blue);
        area2.setPalette(color, red, green, blue);
    }

    @Override
    public BufferedImage getInternalImage() {
        return area().getInternalImage();
    }

    @Override
    public BufferedImage getExternalImage() {
        return area().getExternalImage();
    }

    @Override
    public Color getColor(int pen) {
        return area().getColor(pen);
    }

    @Override
    public void close() {
        area1.close();
        area2.close();
    }

}
