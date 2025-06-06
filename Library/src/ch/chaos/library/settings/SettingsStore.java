package ch.chaos.library.settings;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ch.chaos.library.Files;

public class SettingsStore {
    
    public static AppSettings loadSettings() throws IOException {
        Path appDir = Files.appDataDirectory().toPath();
        Path settingsFile = appDir.resolve(".settings");
        if (java.nio.file.Files.isRegularFile(settingsFile)) {
            String content = java.nio.file.Files.readString(settingsFile);
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.readValue(content, AppSettings.class);
        } else {
            return AppSettings.createDefault();
        }
    }
    
    public static void saveSettings(AppSettings settings) throws IOException {
        Path appDir = Files.appDataDirectory().toPath();
        Path settingsFile = appDir.resolve(".settings");
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String content = mapper.writeValueAsString(settings);
        java.nio.file.Files.writeString(settingsFile, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
