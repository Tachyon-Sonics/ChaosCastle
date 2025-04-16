package ch.chaos.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import ch.pitchtech.modula.runtime.Runtime;

public class Languages {

    private static Languages instance;


    private Languages() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Languages instance() {
        if (instance == null)
            new Languages(); // will set 'instance'
        return instance;
    }

    // VAR


    public short language;


    public short getLanguage() {
        return this.language;
    }

    public void setLanguage(short language) {
        this.language = language;
    }

    // IMPL


    private final Map<String, String> frenchMap = new HashMap<>();
    private final Map<String, String> germanMap = new HashMap<>();


    public Runtime.IRef<String> GetLanguageName(short num) {
        String result = switch (num) {
            case 0 -> "English";
            case 1 -> "Français";
            case 2 -> "Deutsch";
            default -> null;
        };
        if (result == null)
            return null;
        return new Runtime.Ref<>(result);
    }

    public void SetLanguage(short num) {
        this.language = num;
        Menus.instance().clear(); // This sucks... but the original code does not clear the menu bar...
    }

    public Object ADL(String s) {
        if (language == 0) {
            return new Runtime.Ref<>(s);
        } else if (language == 1) {
            String result = frenchMap.getOrDefault(s, s);
            return new Runtime.Ref<>(result);
        } else if (language == 2) {
            String result = germanMap.getOrDefault(s, s);
            return new Runtime.Ref<>(result);
        }
        return new Runtime.Ref<>(s);
    }

    public void begin() {
        // Load translations
        try {
            InputStream input = Languages.class.getResourceAsStream("/Strings");
            if (input == null)
                return;
            try (input) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.ISO_8859_1));
                String line = reader.readLine();
                while (line != null) {
                    if (!line.isBlank()) {
                        String[] items = line.split("\\|");
                        if (items.length == 3) {
                            frenchMap.put(items[0], items[1]);
                            germanMap.put(items[0], items[2]);
                        }
                    }
                    line = reader.readLine();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void close() {

    }
}
