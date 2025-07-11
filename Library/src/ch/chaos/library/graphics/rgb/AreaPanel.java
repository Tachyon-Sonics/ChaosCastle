package ch.chaos.library.graphics.rgb;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

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
        paint(g, false);
    }

    public void paint(java.awt.Graphics g, boolean fromFrame) {
        Graphics2D g2 = (Graphics2D) g.create();
        int frameScale = Settings.appMode().getOuterScale();
        g2.scale(frameScale, frameScale);
        if (frameArea != null) {
            if (frameArea.isVisible()) {
                g2.scale(1.0 / JFrameArea.scaleX, 1.0 / JFrameArea.scaleY);
                if (fromFrame)
                    g2.translate(frameArea.panelOffsetX, frameArea.panelOffsetY);
                g2.drawImage(frameArea.getExternalImage(), 0, 0, this);
            }
        } else {
            g2.drawImage(image, 0, 0, this);
        }
        g2.dispose();
    }

}