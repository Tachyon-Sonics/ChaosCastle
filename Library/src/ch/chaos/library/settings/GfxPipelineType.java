package ch.chaos.library.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ch.chaos.library.utils.Platform;

/**
 * Java2D pipeline
 */
public enum GfxPipelineType {
    DEFAULT("Default"),
    SOFT("Software Loops", getArgsForSoftwarePipeline()),
    DIRECT_DRAW("DirectDraw", "-Dsun.java2d.d3d=false", "-Dsun.java2d.noddraw=false"),
    DIRECT_3D("Direct3D", "-Dsun.java2d.d3d=True"),
    OPENGL("OpenGL", "-Dsun.java2d.opengl=True"),
    XRENDER("XRender", "-Dsun.java2d.xrender=True"),
    METAL("Metal", "-Dsun.java2d.metal=True"),
    WAYLAND("Wayland", "-Dawt.toolkit.name=WLToolkit", "-Dsun.java2d.vulkan=True"); // Beta, based on https://wiki.openjdk.org/display/wakefield/Pure+Wayland+toolkit+prototype
    
    
    private final String name;
    private final List<String> jvmArgs = new ArrayList<>();
    
    private GfxPipelineType(String name, String... jvmArgs) {
        this.name = name;
        this.jvmArgs.addAll(Arrays.asList(jvmArgs));
    }
    
    private static String[] getArgsForSoftwarePipeline() {
        if (Platform.isWindows()) {
            // Disable d3d and direct draw to use plain GDI
            return new String[] { "-Dsun.java2d.d3d=false", "-Dsun.java2d.noddraw=true" };
        } else if (Platform.isMacOsX()) {
            return new String[0]; // Not supported
        } else {
            // Disable opengl and xrender so that plain X11 is used
            return new String[] { "-Dsun.java2d.opengl=false", "-Dsun.java2d.xrender=false" };
        }
    }
    
    public List<String> getJvmArgs() {
        return jvmArgs;
    }
    
    public Map<String, String> getAdditionalEnv() {
        if (this == DIRECT_3D) {
            /*
             * Here we force d3d if chosen, by disabling hw compatibility check.
             * - Our graphics are quite simple, may work even if not 100% compatible
             * - The user can still select an other pipeline if this fails
             */
            return Map.of("J2D_D3D_NO_HWCHECK", "true");
        } else {
            return null;
        }
    }
    
    public static List<GfxPipelineType> getPlatformSupportedTypes() {
        if (Platform.isWindows()) {
            return List.of(DEFAULT, SOFT, DIRECT_DRAW, DIRECT_3D, OPENGL);
        } else if (Platform.isMacOsX()) {
            return List.of(DEFAULT, OPENGL, METAL);
        } else if (Platform.isLinux()) {
            return List.of(DEFAULT, SOFT, XRENDER, OPENGL, WAYLAND);
        } else {
            // Not clear what is available. Just assume the user is a geek and expose all
            return Arrays.asList(values());
        }
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
