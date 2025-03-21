package ch.chaos.castle.app;

import ch.chaos.castle.Grotte;
import ch.pitchtech.modula.runtime.Runtime;

/**
 * Launcher for {@link Grotte}. Setup stuff that does not exist in the Modula-2 code,
 * and hence not in the generated Java code. This includes stuff like application icons
 * and application name.
 * <p>
 * This class setup the mentionned stuff and then runs {@link Grotte}'s main method.
 */
public class GrotteApp {
    
    private static void init() {
        Runtime.setAppName("Grotte");
    }
    
    public static void main(String[] args) {
        init();
        Grotte.main(args);
    }

}
