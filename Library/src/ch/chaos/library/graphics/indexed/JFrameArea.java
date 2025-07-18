package ch.chaos.library.graphics.indexed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.chaos.library.Dialogs;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.AreaPtr;
import ch.chaos.library.Input;
import ch.chaos.library.Input.Event;
import ch.chaos.library.Menus;
import ch.chaos.library.graphics.AreaBase;
import ch.chaos.library.graphics.GraphicsBase;
import ch.chaos.library.settings.GfxDisplayMode;
import ch.chaos.library.settings.Settings;
import ch.chaos.library.utils.FpsStats;
import ch.pitchtech.modula.runtime.Runtime;

/**
 * {@link AreaPtr} implementation when displaying on screen (type {@link Graphics0#atDISPLAY})
 */
class JFrameArea extends AreaBase implements AreaPtr {

    private final static boolean SKIP_MODE = Graphics.SEPARATE_GAME_LOOP;
    private final static int NB_BUFFERS = 2; // Single / Double / Triple Buffering

    private final JFrame frame;
    private final int width;
    private final int height;
    private final AreaPanel panel;
    private Graphics2D g;
    private DoubleBufferArea bufferArea;
    private BufferStrategy bufferStrategy;
    private BufferedImage intermediateImage;
    private Thread repaintThread;
    private BlockingQueue<MemoryArea> repaintExchanger = new SynchronousQueue<>();

    private int panelOffsetX;
    private int panelOffsetY;

    private final static double scaleX;
    private final static double scaleY;

    static {
        AffineTransform screenTransform = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
                .getDefaultTransform();
        scaleX = screenTransform.getScaleX();
        scaleY = screenTransform.getScaleY();
    }


    public JFrameArea(int width, int height) {
        int frameScale = Settings.appMode().getOuterScale();
        this.width = Graphics.scale(width) * frameScale;
        this.height = Graphics.scale(height) * frameScale;
        this.frame = new JFrame();
        frame.setIconImages(Dialogs.instance().getAppImageList());
        this.panel = new AreaPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        frame.setBackground(Color.BLACK);
        double corrX = (Settings.appMode().isFullScreen() && Settings.appMode().getDisplayMode() != null) ? 1.0 : scaleX;
        double corrY = (Settings.appMode().isFullScreen() && Settings.appMode().getDisplayMode() != null) ? 1.0 : scaleY;
        int frameWidth = (int) (this.width / corrX + 0.5);
        int frameHeight = (int) (this.height / corrY + 0.5);
        panel.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel);
        Input.instance().registerMainFrame(frame);
        Menus.instance().registerMainFrame(frame);
        Dialogs.instance().setMainFrame(frame);
        Dialogs.instance().setHideArea(this::hide);
        Dialogs.instance().setShowArea(this::show);
    }

    private void hide() {
        frame.getContentPane().remove(panel);
    }

    private void show() {
        frame.getContentPane().add(panel);
        frame.requestFocus(); // Or else keyboard does not work
    }

    public JFrame getFrame() {
        return frame;
    }

    public int getPanelOffsetX() {
        return panelOffsetX;
    }

    public int getPanelOffsetY() {
        return panelOffsetY;
    }

    public void setupBuffer(DoubleBufferArea bufferArea) {
        this.bufferArea = bufferArea;
        this.panel.setFrameArea(this);
    }

    @Override
    public void bringToFront() {
        if (!frame.isVisible()) {
            if (Settings.appMode().isFullScreen()) {
                frame.setUndecorated(true);
            } else {
                frame.setTitle(Runtime.getAppNameOrDefault());
                frame.setResizable(false);
                frame.setIconImages(Dialogs.instance().getAppImageList());
            }
            frame.pack();
            if (Settings.appMode().isFullScreen()) {
                frame.getContentPane().setBackground(Color.BLACK);
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                device.setFullScreenWindow(frame);
                GfxDisplayMode gfxDisplayMode = Settings.appMode().getDisplayMode();
                if (gfxDisplayMode != null) {
                    Dimension screenSize = new Dimension(gfxDisplayMode.width(), gfxDisplayMode.height());
                    DisplayMode switchTo = null;
                    DisplayMode current = device.getDisplayMode();
                    if (!current.equals(gfxDisplayMode.toDisplayMode())) {
                        switchTo = gfxDisplayMode.toDisplayMode();
                    }
                    if (switchTo != null && device.isDisplayChangeSupported()) {
                        System.out.println("Switching to " + switchTo);
                        device.setDisplayMode(switchTo);
                        frame.setSize(screenSize);
                        frame.paint(frame.getGraphics());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    frame.setSize(screenSize);
                    frame.validate();
                }
                frame.addWindowFocusListener(new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        /*
                         * This occurs when the full-screen window looses focus,
                         * by using [ALT]-[TAB] for instance
                         * 
                         * Send a [p] key to pause the game
                         */
                        Event event = new Event();
                        event.type = Input.eKEYBOARD;
                        event.ch = 'p';
                        Input.instance().SendEvent(event);
                    }

                    @Override
                    public void windowGainedFocus(WindowEvent e) {
                        SwingUtilities.invokeLater(() -> updateArea());
                    }

                });
            } else {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO review
            if (bufferArea == null) {
                BufferedImage image = frame.getGraphicsConfiguration().createCompatibleImage(width, height);
                panel.setImage(image);
                g = panel.getImage().createGraphics();
                g.scale(GraphicsBase.scale() / scaleX, GraphicsBase.scale() / scaleY);
                GraphicsIndexedColorImpl.setupHighSpeed(g);
            } else if (NB_BUFFERS > 1) {
                frame.createBufferStrategy(NB_BUFFERS);
                bufferStrategy = frame.getBufferStrategy();
                if (!Settings.appMode().isFullScreen()) {
                    // Offset according to frame's title and borders
                    Point panelLocation = panel.getLocationOnScreen();
                    Point frameLocation = frame.getLocationOnScreen();
                    panelOffsetX = (int) ((panelLocation.x - frameLocation.x) * scaleX);
                    panelOffsetY = (int) ((panelLocation.y - frameLocation.y) * scaleY);
                } else {
                    // Center
                    double corrX = (Settings.appMode().getDisplayMode() == null ? scaleX : 1.0);
                    double corrY = (Settings.appMode().getDisplayMode() == null ? scaleY : 1.0);
                    Dimension preferredSize = panel.getPreferredSize();
                    Dimension actualSize = panel.getSize();
                    panelOffsetX = (int) ((actualSize.width - preferredSize.width) * corrX / 2 + 0.5);
                    panelOffsetY = (int) ((actualSize.height - preferredSize.height) * corrY / 2 + 0.5);
                }
                int frameScale = Settings.appMode().getOuterScale();
                if (frameScale > 1) {
                    /*
                     * Scaling an indexed image to an RGB one seems slower than converting to an RGB image
                     * first, and scaling then. At least for scale > 2. For 2 both ways seems similar in speed...
                     */
                    intermediateImage = frame.getGraphicsConfiguration()
                            .createCompatibleImage(width / frameScale, height / frameScale);
                }
            }
            repaintThread = new Thread(this::repaintLoop, "Paint Loop");
            repaintThread.setDaemon(true);
            repaintThread.start();
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

    private void repaintLoop() {
        long lastDisplay = System.nanoTime();
        long displayPeriod = TimeUnit.SECONDS.toNanos(5);
        long previous = System.nanoTime();
        while (true) {
            MemoryArea area = null;
            try {
                area = repaintExchanger.take();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (bufferStrategy != null) {
                if (intermediateImage != null) {
                    Graphics2D g2 = intermediateImage.createGraphics();
                    GraphicsIndexedColorImpl.setupHighSpeed(g2);
                    g2.drawImage(area.getExternalImage(), 0, 0, null);
                    g2.dispose();
                }
                BufferedImage toRender = (intermediateImage != null ? intermediateImage : area.getExternalImage());
                // Render single frame
                do {
                    // The following loop ensures that the contents of the drawing buffer
                    // are consistent in case the underlying surface was recreated
                    do {
                        // Get a new graphics context every time through the loop
                        // to make sure the strategy is validated
                        Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();

                        // Render to graphics
                        panel.paint(graphics, toRender, true);

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

            // Update FPS stats
            long now = System.nanoTime();
            long elapsed = now - previous;
            double fps = 1000000000.0 / (double) elapsed;
            previous = now;
            FpsStats.instance(FpsStats.EXTERNAL).accumulate(fps);

            if (now >= lastDisplay + displayPeriod) {
                displayFps();
                lastDisplay = now;
            }
        }
    }

    private void displayFps() {
        double iFps = FpsStats.instance(FpsStats.INTERNAL).pick();
        double eFps = FpsStats.instance(FpsStats.EXTERNAL).pick();
        DecimalFormat formatter = new DecimalFormat("#0.0");
        String message;
        if (SKIP_MODE) {
            message = "GameLoop: " + formatter.format(iFps) + " / Rendering: " + formatter.format(eFps) + " FPS";
        } else {
            message = formatter.format(eFps) + " FPS";
        }
        if (Settings.appMode().isFullScreen()) {
//            System.out.println(message);
        } else {
            SwingUtilities.invokeLater(() -> {
                String title = Runtime.getAppNameOrDefault() + " (" + message + ")";
                frame.setTitle(title);
            });
        }
    }

    public void switchArea() {
        if (bufferStrategy != null) {
            MemoryArea memoryArea = bufferArea.switchArea();
            if (SKIP_MODE) {
                repaintExchanger.offer(memoryArea); // If the repaint loop is not ready, this will skip it
            } else {
                try {
                    repaintExchanger.put(memoryArea); // This will wait for the repaint loop to take it
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (isVisible()) {
            java.awt.Graphics g = panel.getGraphics();
            panel.paint(g);
        }
    }

    public void updateArea() {
        if (bufferStrategy != null) {
            MemoryArea memoryArea = bufferArea.updateArea();
            try {
                repaintExchanger.put(memoryArea); // This will wait for the repaint loop to take it
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } else if (isVisible()) {
            java.awt.Graphics g = panel.getGraphics();
            panel.paint(g);
        }
    }

    public boolean isFirstBuffer() {
        if (bufferArea != null) {
            return bufferArea.isFirstBuffer();
        }
        return true;
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
        Dialogs.instance().setMainFrame(null);
        Dialogs.instance().setHideArea(null);
        Dialogs.instance().setShowArea(null);

        if (g != null)
            g.dispose();
        frame.dispose();
        bufferStrategy = null;
    }

}