package ch.chaos.library.utils;

public class Platform {
    
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    
    public static boolean isMacOsX() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
    
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
    
    public static boolean is32bit() {
        return "32".equals(System.getProperty("sun.arch.data.model"));
    }
    
    public static String getPlatformName() {
        if (isWindows())
            return "Windows";
        else if (isMacOsX())
            return "MacOS";
        else if (isLinux())
            return "Linux";
        return "Other";
    }

}
