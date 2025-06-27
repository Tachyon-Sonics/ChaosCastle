package ch.chaos.library.settings;

import ch.chaos.library.graphics.xbrz.XbrzHelper;

/**
 * Graphics settings for a given screen configuration.
 * <p>
 * If multiple screens exists, a separate {@link AppMode} is stored for each.
 */
public class AppMode {
    
    private boolean fullScreen;
    private GfxModeType gfxMode;
    private GfxPipelineType gfxPipeline;
    private GfxDisplayMode displayMode; // null => no change
    private int innerScale;
    private int outerScale;
    private VsyncType vsyncType;
    private boolean doNotAskAgain;
    
    
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
    
    public GfxPipelineType getGfxPipeline() {
        return gfxPipeline;
    }

    public void setGfxPipeline(GfxPipelineType gfxPipeline) {
        this.gfxPipeline = gfxPipeline;
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
    
    public VsyncType getVsyncType() {
        return vsyncType;
    }

    public void setVsyncType(VsyncType vsyncType) {
        this.vsyncType = vsyncType;
    }
    
    public boolean isDoNotAskAgain() {
        return doNotAskAgain;
    }
    
    public void setDoNotAskAgain(boolean doNotAskAgain) {
        this.doNotAskAgain = doNotAskAgain;
    }

    public static AppMode createDefault(GfxDisplayMode mode) {
        AppMode appMode = new AppMode();
        appMode.fullScreen = true;
        appMode.gfxMode = GfxModeType.INDEXED;
        appMode.gfxPipeline = GfxPipelineType.DEFAULT;
        appMode.displayMode = null;
        appMode.vsyncType = VsyncType.BALANCED_LOW;
        appMode.doNotAskAgain = false;
        
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
