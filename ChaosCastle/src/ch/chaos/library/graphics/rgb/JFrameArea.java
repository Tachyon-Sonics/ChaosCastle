package ch.chaos.library.graphics.rgb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.JFrame;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.AreaPtr;
import ch.chaos.library.Input;
import ch.chaos.library.Menus;
import ch.chaos.library.graphics.AreaBase;

/**
 * {@link AreaPtr} implementation when displaying on screen (type {@link Graphics0#atDISPLAY})
 */
class JFrameArea extends AreaBase implements AreaPtr {

    private static int NB_BUFFERS = 2; // Single / Double / Triple Buffering

    private final JFrame frame;
    private final int width;
    private final int height;
    private final AreaPanel panel;
    private Graphics2D g;
    private BufferArea bufferArea;
    private BufferStrategy bufferStrategy;

    int panelOffsetX;
    int panelOffsetY;

    final static double scaleX;
    final static double scaleY;

    static {
        AffineTransform screenTransform = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
                .getDefaultTransform();
        scaleX = screenTransform.getScaleX();
        scaleY = screenTransform.getScaleY();
    }


    public JFrameArea(int width, int height) {
        this.frame = new JFrame();
        this.panel = new AreaPanel();
        this.width = Graphics.scale(width) * Graphics.FRAME_SCALE;
        this.height = Graphics.scale(height) * Graphics.FRAME_SCALE;
        panel.setOpaque(true);
        panel.setBackground(Color.black);
        int frameWidth = (int) (Graphics.scale(width) * Graphics.FRAME_SCALE / JFrameArea.scaleX + 0.5);
        int frameHeight = (int) (Graphics.scale(height) * Graphics.FRAME_SCALE / JFrameArea.scaleY + 0.5);
        panel.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.getContentPane().add(panel);
        Input.instance().registerMainFrame(frame);
        Menus.instance().registerMainFrame(frame);
        Dialogs.instance().setMainFrame(frame);
    }

    @Override
    public void bringToFront() {
        if (!frame.isVisible()) {
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO review
            if (bufferArea == null) {
                GraphicsConfiguration gc = frame.getGraphicsConfiguration();
                BufferedImage image = gc.createCompatibleImage(width, height, Transparency.OPAQUE);
                panel.setImage(image);
                g = panel.getImage().createGraphics();
                g.scale(Graphics.SCALE / JFrameArea.scaleX, Graphics.SCALE / JFrameArea.scaleY);
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);
                GraphicsRgbColorImpl.setupHighQuality(g);
            } else if (NB_BUFFERS > 1) {
                frame.createBufferStrategy(NB_BUFFERS);
                bufferStrategy = frame.getBufferStrategy();
                Point panelLocation = panel.getLocationOnScreen();
                Point frameLocation = frame.getLocationOnScreen();
                panelOffsetX = (int) ((panelLocation.x - frameLocation.x) * scaleX + 0.5);
                panelOffsetY = (int) ((panelLocation.y - frameLocation.y) * scaleY + 0.5);
            }
        } else {
            if (bufferArea != null) {
                panel.setImage(bufferArea.getExternalImage());
            }
            panel.repaint();
        }
    }

    public boolean isVisible() {
        return frame.isVisible();
    }

    public void repaint() {
        panel.repaint();
    }

    public void switchArea() {
        if (bufferStrategy != null) {
            // Render single frame
            do {
                // The following loop ensures that the contents of the drawing buffer
                // are consistent in case the underlying surface was recreated
                do {
                    // Get a new graphics context every time through the loop
                    // to make sure the strategy is validated
                    Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();

                    // Render to graphics
                    panel.paint(graphics); // TODO review, the idea is to paint directly using 'graphics' instead of inside the image, then this methods only
                                           // make show() and create a new gfx

                    // Dispose the graphics
                    graphics.dispose();

                    // Repeat the rendering if the drawing buffer contents
                    // were restored
                } while (bufferStrategy.contentsRestored());

                // Display the buffer
                bufferStrategy.show();

                // Repeat the rendering if the drawing buffer was lost
            } while (bufferStrategy.contentsLost());
        } else {
            java.awt.Graphics g = panel.getGraphics();
            panel.paint(g);
        }
    }

    public void setupBuffer(BufferArea bufferArea) {
        this.bufferArea = bufferArea;
        this.panel.setFrameArea(this);
    }

    @Override
    public void setPalette(int color, int red, int green, int blue) {
        if (bufferArea != null) {
            bufferArea.setPalette(color, red, green, blue);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Color getColor(int pen) {
        if (bufferArea != null) {
            return bufferArea.getColor(pen);
        } else {
            // Assume true color
            return new Color(pen);
        }
    }

    @Override
    public BufferedImage getExternalImage() {
        if (bufferArea != null) {
            return bufferArea.getExternalImage();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public BufferedImage getInternalImage() {
        if (bufferArea != null) {
            return bufferArea.getInternalImage();
        } else {
            return panel.getImage();
        }
    }

    @Override
    public void draw(Consumer<Graphics2D> operation) {
        if (bufferArea != null) {
            bufferArea.draw(operation);
        } else {
            operation.accept(g);
            panel.repaint();
        }
    }

    @Override
    public void close() {
        if (g != null)
            g.dispose();
        frame.dispose();
    }

}