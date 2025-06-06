package ch.chaos.library.settings;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AppSettings {
    
    private Map<GfxDisplayMode, AppMode> appModes = new HashMap<>();
    private boolean enableSounds;

    
    public Map<GfxDisplayMode, AppMode> getAppModes() {
        return appModes;
    }
    
    public void setAppModes(Map<GfxDisplayMode, AppMode> appModes) {
        this.appModes = appModes;
    }

    public boolean isEnableSounds() {
        return enableSounds;
    }

    public void setEnableSounds(boolean enableSounds) {
        this.enableSounds = enableSounds;
    }
    
    public static AppSettings createDefault() {
        AppSettings settings = new AppSettings();
        settings.enableSounds = true;
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        GfxDisplayMode gfxMode = GfxDisplayMode.from(displayMode);
        AppMode appMode = AppMode.createDefault(gfxMode);
        settings.appModes.put(gfxMode, appMode);
        return settings;
    }
    
    public static void main(String[] args) throws JsonProcessingException {
        AppSettings settings = AppSettings.createDefault();
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String result = mapper.writeValueAsString(settings);
        System.out.println(result);
    }

}
