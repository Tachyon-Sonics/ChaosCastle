package ch.chaos.library.settings;

import ch.chaos.library.graphics.xbrz.XbrzHelper;

public class AppMode {
    
    private boolean fullScreen;
    private GfxModeType gfxMode;
    private GfxDisplayMode displayMode;
    private int innerScale;
    private int outerScale;
    
    
    public boolean isFullScreen() {
        return fullScreen;
    }
    
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }
    
    public GfxModeType getGfxMode() {
        return gfxMode;
    }
    
    public void setGfxMode(GfxModeType gfxMode) {
        this.gfxMode = gfxMode;
    }
    
    public GfxDisplayMode getDisplayMode() {
        return displayMode;
    }
    
    public void setDisplayMode(GfxDisplayMode displayMode) {
        this.displayMode = displayMode;
    }
    
    public int getInnerScale() {
        return innerScale;
    }
    
    public void setInnerScale(int innerScale) {
        this.innerScale = innerScale;
    }
    
    public int getOuterScale() {
        return outerScale;
    }
    
    public void setOuterScale(int outerScale) {
        this.outerScale = outerScale;
    }
    
    public static AppMode createDefault(GfxDisplayMode mode) {
        AppMode appMode = new AppMode();
        appMode.fullScreen = true;
        appMode.gfxMode = GfxModeType.INDEXED;
        
        // Scaling factors
        appMode.innerScale = 1;
        appMode.outerScale = 1;
        int innerScale = 1;
        int outerScale = 1;
        int width = 320;
        int height = 240;
        while (width * innerScale * outerScale <= mode.width() 
                && height * innerScale * outerScale <= mode.height()) {
            appMode.innerScale = XbrzHelper.getNearestScale(innerScale);
            appMode.outerScale = outerScale;
            if (innerScale < 4) {
                innerScale++;
            } else {
                outerScale++;
            }
        }
        return appMode;
    }

}
