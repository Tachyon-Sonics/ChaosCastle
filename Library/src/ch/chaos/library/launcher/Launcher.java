package ch.chaos.library.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import ch.chaos.library.settings.AppMode;
import ch.chaos.library.settings.AppSettings;
import ch.chaos.library.settings.GfxDisplayMode;
import ch.chaos.library.settings.SettingsStore;
import ch.chaos.library.utils.Platform;
import ch.chaos.library.utils.RelauncherBuilder;
import ch.chaos.library.utils.gui.GuiUtils;

public class Launcher {

    private static final String LAUNCH_ARG = "--launch";
    private static final String SETTINGS_ARG = "--settings"; // Force show launcher dialog


    public static void showLauncherIfNeeded(String[] args, Runnable onComplete) {
        AppSettings appSettings;
        try {
            appSettings = SettingsStore.loadSettings();
        } catch (IOException ex) {
            ex.printStackTrace();
            appSettings = AppSettings.createDefault();
        }
        GfxDisplayMode currentDisplayMode = GfxDisplayMode.current();
        AppMode appMode = appSettings.getAppModes().get(currentDisplayMode);
        if (appMode == null) {
            appMode = AppMode.createDefault(currentDisplayMode);
            appSettings.getAppModes().put(currentDisplayMode, appMode);
        }

        // Check if we must start the app now
        if (isLaunchNow(args)) {
            // Start app now
            onComplete.run();
        } else if (appMode != null && appMode.isDoNotAskAgain() && !isOpenSettings(args)) {
            // Apply settings immediately
            // Some settings require adding JVM options (like Java2D pipeline, etc), hence relaunch
            relaunchApp(appSettings);
        } else {
            // Open settings dialog
            AppSettings appSettings0 = appSettings;
            if (appMode == null)
                appMode = AppMode.createDefault(currentDisplayMode);
            AppMode appMode0 = appMode;
            SwingUtilities.invokeLater(() -> openSettingsDialog(appSettings0, appMode0));
        }
    }
    
    private static boolean isLaunchNow(String[] args) {
        for (String arg : args) {
            if (LAUNCH_ARG.equals(arg))
                return true;
        }
        return false;
    }
    
    private static boolean isOpenSettings(String[] args) {
        for (String arg : args) {
            if (SETTINGS_ARG.equals(arg))
                return true;
        }
        return false;
    }

    private static void openSettingsDialog(AppSettings appSettings, AppMode appMode) {
        GuiUtils.setupLookAndFeel();
        LauncherFrame frame = new LauncherFrame(appSettings, appMode, Launcher::relaunchApp, null);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void relaunchApp(AppSettings appSettings) {
        RelauncherBuilder relauncher = new RelauncherBuilder();
        
        // Add launch argument so that we won't open settings again but just start the app
        relauncher.addAdditionalAppArg(LAUNCH_ARG);
        relauncher.addAdditionalJvmArg("-XX:+UseZGC"); // TODO add as an option to settings
        
        // Add jvm options related to the specified java2d pipeline
        AppMode appMode = appSettings.getAppModes().get(GfxDisplayMode.current());
        if (appMode != null) {
            for (String jvmArg : appMode.getGfxPipeline().getJvmArgs()) {
                relauncher.addAdditionalJvmArg(jvmArg);
            }
        }
        
        ProcessBuilder processBuilder = relauncher.build(true);
        // Add any environment variable related to the specified java2d pipeline
        if (appMode != null) {
            Map<String, String> additionalEnv = appMode.getGfxPipeline().getAdditionalEnv();
            if (additionalEnv != null) {
                System.out.println("Adding the following environment variables: " + additionalEnv);
                processBuilder.environment().putAll(additionalEnv);
            }
        }

        // Launch
        processBuilder.inheritIO();
        System.out.println("Launching app with the following command line:");
        System.out.println(command2string(processBuilder.command()));
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.exit(exitCode);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static String command2string(List<String> command) {
        StringBuilder result = new StringBuilder();
        for (String item : command) {
            if (result.length() > 0)
                result.append(" ");
            boolean isFile = false;
            try {
                Path path = Paths.get(item);
                isFile = Files.isRegularFile(path) || Files.isDirectory(path);
            } catch (Exception ex) {
                // Ignore, just consider it's not a file
            }
            if (Platform.isWindows() || !isFile) {
                if (item.contains(" "))
                    result.append("\"" + item + "\"");
                else
                    result.append(item);
            } else {
                result.append(item.replace(" ", "\\ "));
            }
        }
        return result.toString();
    }

}
