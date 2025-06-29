package ch.chaos.library.utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create a {@link ProcessBuilder} that can be used to re-launch the current application.
 * <p>
 * This class copies JVM and app arguments, and allows you to specify additional JVM / app arguments
 * usign {@link #addAdditionalJvmArg(String)} and {@link #addAdditionalAppArg(String)}.
 */
public class RelauncherBuilder {
    
    /** 
     * Sun property pointing the main class and its arguments. 
     * Might not be defined on non Hotspot VM implementations.
     */
    private static final String SUN_JAVA_COMMAND = "sun.java.command";

    
    private final List<String> additionalJvmArgs = new ArrayList<>();
    private final List<String> additionalAppArgs = new ArrayList<>();
    
    
    public void addAdditionalJvmArg(String arg) {
        additionalJvmArgs.add(arg);
    }
    
    public void addAdditionalAppArg(String arg) {
        additionalAppArgs.add(arg);
    }
    
    public ProcessBuilder build() {
        List<String> command = new ArrayList<>();
        command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        
        // Append JVM arguments
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String jvmArg : jvmArgs) {
            if (!jvmArg.startsWith("-agentlib")) { // Skip to prevent conflict on port num
                command.add(jvmArg);
            }
        }
        
        // Append additional JVM arguments
        command.addAll(additionalJvmArgs);

        // Main
        String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
        if (mainCommand[0].endsWith(".jar")) {
            // Main is a jar
            command.add("-jar");
            command.add(new File(mainCommand[0]).getPath());
        } else {
            // Main is a class
            command.add("-cp");
            command.add(System.getProperty("java.class.path"));
            command.add(mainCommand[0]);
        }

        // Add other arguments
        List<String> argList = Arrays.asList(mainCommand);
        command.addAll(argList.subList(1, argList.size()));
        
        // Add additional arguments
        command.addAll(additionalAppArgs);
        
        // Create process builder
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        return processBuilder;
    }

}
