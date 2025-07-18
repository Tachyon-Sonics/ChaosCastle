package ch.chaos.library.settings;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ch.chaos.library.utils.Platform;

public class AppSettings {
    
    private Map<GfxDisplayMode, AppMode> appModes = new HashMap<>();
    private GarbageCollectorType garbageCollectorType;
    private AudioResamplingType audioResamplingType = AudioResamplingType.LINEAR;

    
    public Map<GfxDisplayMode, AppMode> getAppModes() {
        return appModes;
    }
    
    public void setAppModes(Map<GfxDisplayMode, AppMode> appModes) {
        this.appModes = appModes;
    }
    
    public AudioResamplingType getAudioResamplingType() {
        return audioResamplingType;
    }

    public void setAudioResamplingType(AudioResamplingType audioResamplingType) {
        this.audioResamplingType = audioResamplingType;
    }
    
    public GarbageCollectorType getGarbageCollectorType() {
        return garbageCollectorType;
    }

    public void setGarbageCollectorType(GarbageCollectorType garbageCollectorType) {
        this.garbageCollectorType = garbageCollectorType;
    }
    
    public void cleanup() {
        if (garbageCollectorType == null)
            garbageCollectorType = GarbageCollectorType.DEFAULT;
    }

    public static AppSettings createDefault() {
        AppSettings settings = new AppSettings();
        settings.audioResamplingType = AudioResamplingType.LINEAR;
        if ((Platform.isWindows() || Platform.isMacOsX() || Platform.isLinux()) && !Platform.is32bit()) {
            settings.garbageCollectorType = GarbageCollectorType.ZGC;
        } else {
            settings.garbageCollectorType = GarbageCollectorType.DEFAULT;
        }
        GfxDisplayMode gfxMode = GfxDisplayMode.current();
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
