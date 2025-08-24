package ch.chaos.library.graphics.indexed;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import ch.chaos.library.Graphics;
import ch.chaos.library.settings.Settings;

class AreaPanel extends JPanel {

    private final int frameScale = Settings.appMode().getOuterScale();
    private JFrameArea frameArea;
    private BufferedImage image;
    private int borderCd = 0;


    public JFrameArea getFrameArea() {
        return frameArea;
    }

    public void setFrameArea(JFrameArea frameArea) {
        this.frameArea = frameArea;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void paint(java.awt.Graphics g) {
        paint(g, this.image, false);
    }

    /**
     * @param fromGameLoop true if called from repaint loop, false if called by Swing's repaint
     */
    public void paint(java.awt.Graphics g, BufferedImage image, boolean fromGameLoop) {
        boolean fullScreen = Settings.appMode().isFullScreen();
        Graphics2D g2 = (Graphics2D) g.create();
        if (frameArea != null) {
            // Buffered
            if (frameArea.isVisible()) {
                if (fullScreen) {
                    if (!fromGameLoop) {
                        g2.setColor(Color.BLACK);
                        g2.fillRect(0, 0, frameArea.getFrame().getWidth(), frameArea.getFrame().getHeight());
                        borderCd = 2; // Allow both buffers (in case of page flipping) to be fully erased as well
                    } else if (borderCd > 0) {
                        g2.setColor(Color.BLACK);
                        g2.fillRect(0, 0, frameArea.getFrame().getWidth(), frameArea.getFrame().getHeight());
                        borderCd--;
                    }
                }
                Graphics.resetScale(g2);
                if (fromGameLoop || fullScreen)
                    g2.translate(frameArea.getPanelOffsetX(), frameArea.getPanelOffsetY());
                g2.scale(frameScale, frameScale);
                if (image != null) {
                    g2.drawImage(image, 0, 0, this);
                    this.image = image;
                } else {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, Graphics.scale(getWidth()), Graphics.scale(getHeight()));
                }
            }
        } else {
            // Not buffered
            g2.scale(frameScale, frameScale);
            g2.drawImage(this.image, 0, 0, this);
        }
        g2.dispose();
    }

}