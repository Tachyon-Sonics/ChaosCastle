package ch.chaos.test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import ch.chaos.castle.app.ChaosCastleApp;
import io.github.stanio.xbrz.Xbrz;
import io.github.stanio.xbrz.Xbrz.ScalerCfg;

public class TestScalingXbrz {
    
    public static void main(String[] args) throws IOException {
        URL url = ChaosCastleApp.class.getResource("App16.png");
        ImageIcon image = new ImageIcon(url);

        BufferedImage source = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = source.createGraphics();
        g.drawImage(image.getImage(), 0, 0, null);
        g.dispose();
        
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        int[] srcPixels = source.getRGB(0, 0, srcWidth, srcHeight, null, 0, srcWidth);

        int factor = 4;
        int destWidth = srcWidth * factor;
        int destHeight = srcHeight * factor;
        boolean hasAlpha = false; //source.getColorModel().hasAlpha();
        ScalerCfg cfg = new ScalerCfg();
        cfg.equalColorTolerance = 1.0;
        int[] destPixels = new Xbrz(factor, hasAlpha, cfg).scaleImage(srcPixels, null, srcWidth, srcHeight);

        BufferedImage scaled = new BufferedImage(destWidth, destHeight,
                                                 hasAlpha ? BufferedImage.TYPE_INT_ARGB
                                                          : BufferedImage.TYPE_INT_RGB);
        scaled.setRGB(0, 0, destWidth, destHeight, destPixels, 0, destWidth);
        
        ImageIO.write(scaled, "PNG", new File("C:\\Users\\Nicolas Juillerat\\Desktop\\Result.png"));
    }

}
