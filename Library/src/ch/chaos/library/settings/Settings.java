package ch.chaos.library.settings;

import java.io.IOException;

/**
 * Read-only cached access to app settings
 */
public class Settings {
    
    private static AppSettings appSettings;
    private static AppMode appMode;
    
    
    public static synchronized AppSettings appSettings() {
        if (appSettings == null) {
            try {
                appSettings = SettingsStore.loadSettings();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.print("Failed to load settings. Using defaults.");
                appSettings = AppSettings.createDefault();
            }
        }
        return appSettings;
    }
    
    public static synchronized AppMode appMode() {
        if (appMode == null) {
            appMode = appSettings().getAppModes().get(GfxDisplayMode.current());
        }
        return appMode;
    }
    
    public static synchronized void reload() {
        appSettings = null;
        appMode = null;
    }

}
