package ch.chaos.castle.app;

import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import ch.chaos.castle.ChaosCastle;
import ch.chaos.library.Dialogs;
import ch.pitchtech.modula.runtime.Runtime;

/**
 * Launcher for {@link ChaosCastle}. Setup stuff that does not exist in the Modula-2 code,
 * and hence not in the generated Java code. This includes stuff like application icons
 * and application name.
 * <p>
 * This class setup the mentionned stuff and then runs {@link ChaosCastle}'s main method.
 */
public class ChaosCastleApp {
    
    private static void init() {
        Runtime.setAppName("ChaosCastle");
        
        List<Image> images = new ArrayList<>();
        images.add(loadIcon("App16.png").getImage());
        images.add(loadIcon("App24.png").getImage());
        images.add(loadIcon("App32.png").getImage());
        images.add(loadIcon("App48.png").getImage());
        images.add(loadIcon("App64.png").getImage());
        images.add(loadIcon("App128.png").getImage());
        Image appImage = new BaseMultiResolutionImage(2, images.toArray(Image[]::new));
        Dialogs.instance().setAppImage(appImage);
        Dialogs.instance().setAppImageList(images);
    }
    
    private static ImageIcon loadIcon(String name) {
        URL url = ChaosCastleApp.class.getResource(name);
        return new ImageIcon(url);
    }
    
    public static void main(String[] args) {
        init();
//        Launcher.showLauncherIfNeeded(args, () -> {
//            ChaosCastle.main(args);
//        });
        ChaosCastle.main(args);
    }

}
