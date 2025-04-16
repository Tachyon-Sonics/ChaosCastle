package ch.chaos.library.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public abstract class AreaBase {

    // State

    protected List<int[]> currentPoly; // List of int[2] -> x, y
    protected Color currentBackground = Color.BLACK;
    protected Color currentColor = Color.WHITE;
    protected int currentPattern = 4;


    public List<int[]> getCurrentPoly() {
        return currentPoly;
    }

    public void setCurrentPoly(List<int[]> currentPoly) {
        this.currentPoly = currentPoly;
    }

    public Color getCurrentBackground() {
        return currentBackground;
    }

    public void setCurrentBackground(Color currentBackground) {
        this.currentBackground = currentBackground;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    public int getCurrentPattern() {
        return currentPattern;
    }

    public void setCurrentPattern(int currentPattern) {
        this.currentPattern = currentPattern;
    }

    public void bringToFront() {
        throw new UnsupportedOperationException("toFront() not allowed for this area type");
    }

    // Operations

    public abstract void draw(Consumer<Graphics2D> operation);

    public abstract void setPalette(int color, int red, int green, int blue);

    public abstract BufferedImage getInternalImage(); // Image for off-screen drawing

    public abstract BufferedImage getExternalImage(); // Image to draw on screen

    public abstract Color getColor(int pen);

    public void writePixel(int x, int y, int pen) {
        throw new UnsupportedOperationException();
    }

    public abstract void close();

}