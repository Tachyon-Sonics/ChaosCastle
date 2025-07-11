package ch.chaos.library.graphics.indexed;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import ch.chaos.library.Graphics;
import ch.chaos.library.settings.Settings;

class AreaPanel extends JPanel {

    private JFrameArea frameArea;
    private BufferedImage image;


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

    public void paint(java.awt.Graphics g, BufferedImage image, boolean fromFrame) {
        boolean fullScreen = Settings.appMode().isFullScreen();
        Graphics2D g2 = (Graphics2D) g.create();
        if (frameArea != null) {
            // Buffered
            if (frameArea.isVisible()) {
                if (fullScreen) {
                    if (!fromFrame) {
                        g2.setColor(Color.BLACK);
                        g2.fillRect(0, 0, frameArea.getFrame().getWidth(), frameArea.getFrame().getHeight());
                    }
                }
                Graphics.resetScale(g2);
                if (fromFrame || fullScreen)
                    g2.translate(frameArea.getPanelOffsetX(), frameArea.getPanelOffsetY());
                g2.scale(Graphics.FRAME_SCALE, Graphics.FRAME_SCALE);
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
            g2.scale(Graphics.FRAME_SCALE, Graphics.FRAME_SCALE);
            g2.drawImage(this.image, 0, 0, this);
        }
        g2.dispose();
    }

}