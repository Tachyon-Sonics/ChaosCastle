package ch.chaos.library.settings;

import java.awt.DisplayMode;

public record GfxDisplayMode(int width, int height, int depth, int refreshRate) {
    
    public static GfxDisplayMode from(DisplayMode displayMode) {
        return new GfxDisplayMode(displayMode.getWidth(), displayMode.getHeight(), displayMode.getBitDepth(), displayMode.getRefreshRate());
    }
}
