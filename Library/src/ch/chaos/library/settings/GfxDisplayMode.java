package ch.chaos.library.settings;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public record GfxDisplayMode(int width, int height, int depth, int refreshRate) {
    
    public static GfxDisplayMode from(DisplayMode displayMode) {
        return new GfxDisplayMode(displayMode.getWidth(), displayMode.getHeight(), displayMode.getBitDepth(), displayMode.getRefreshRate());
    }
    
    public static GfxDisplayMode current() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        return from(displayMode);
    }
    
    public DisplayMode toDisplayMode() {
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode result = null;
        for (DisplayMode displayMode : graphicsDevice.getDisplayModes()) {
            if (from(displayMode).equals(this)) {
                result = displayMode;
            }
        }
        return result;
    }
    
    /**
     * Used by JSON deserialization. Must be compatible with {@link #toString()}
     */
    public GfxDisplayMode(String fromString) {
        this(num(fromString, 0), num(fromString, 1), num(fromString, 2), num(fromString, 3));
    }
    
    private static int num(String fromString, int index) {
        String[] parts = fromString.split("x");
        return Integer.parseInt(parts[index]);
    }
    
    /**
     * Used by JSON serialization. Must be compatible with {@link GfxDisplayMode#GfxDisplayMode(String)}.
     */
    @Override
    public String toString() {
        return width + "x" + height + "x" + depth + "x" + refreshRate;
    }
    
    public String toDisplayString() {
        return width + "x" + height + ", " + depth + " bits, " + refreshRate + " Hz";
    }
}
