package ch.chaos.library.settings;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;

public record GfxDisplayMode(int width, int height, int depth, int refreshRate) {
    
    public static GfxDisplayMode from(DisplayMode displayMode) {
        return new GfxDisplayMode(displayMode.getWidth(), displayMode.getHeight(), displayMode.getBitDepth(), displayMode.getRefreshRate());
    }
    
    public static GfxDisplayMode current() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        return from(displayMode);
    }
    
    @Override
    public String toString() {
        return width + "x" + height + ", " + depth + " bits, " + refreshRate + " Hz";
    }
}
