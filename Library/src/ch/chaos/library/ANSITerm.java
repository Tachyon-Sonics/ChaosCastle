package ch.chaos.library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ch.chaos.library.topaz.TopazFont;
import ch.pitchtech.modula.runtime.Runtime;

public class ANSITerm { // TODO (0) Delete key in load

    private final static boolean FULL_SCREEN; // TODO allow switching
    private final static boolean USE_NATIVE_FONT = false;
    private final static boolean USE_NEW_TOPAZ = true;
    // TODO try using Lucida Console, replace "*" by "Ω"

    private static boolean OPTIMIZED_REPAINT = true;
    private static int NB_BUFFERS = 2; // Single / Double / Triple Buffering
    private BufferStrategy bufferStrategy;
    private int panelOffsetX;
    private int panelOffsetY;

    static {
        FULL_SCREEN = System.getProperty("FullScreen") != null;
    }

    private final static int TERM_WIDTH = 78;
    private final static int TERM_HEIGHT = 22;
    private final static int PAD_X = 10;
    private final static int PAD_Y = 10;

    private final static double SCREEN_FRACTION = 1.0;
    private final static int SCALE_X = 1;
    private final static int SCALE_Y = 2;
    @SuppressWarnings("unused")
    private final static int FONT_SCALE_X = USE_NEW_TOPAZ || USE_NATIVE_FONT ? 2 : 1;
    private final static int FONT_SCALE_Y = 1;

    private final static Color[] ANSI_COLORS = new Color[] {
            Color.black,
            Color.red,
            Color.green,
            Color.yellow,
            Color.blue,
            Color.magenta,
            Color.cyan,
            Color.white
    };

    private final Map<Character, Character> numpadMap = Map.of(
            't', '7', 'z', '8', 'u', '9',
            'g', '4', 'h', '5', 'j', '6',
            'b', '1', 'n', '2', 'm', '3');


    record ColorChar(char ch, int color) {
    }


    private int fontSize = 16;
    private int fontDescent = 2;
    private Font topazFont;
    private Font altTopazFont;
    private JFrame frame;
    private TermPanel panel;
    private volatile boolean fullRepaintRequested = true;
    private double screenScaleX;
    private double screenScaleY;
    private int refreshRate;
    private int imageWidth;
    private int imageHeight;
    private Cursor blankCursor;
    private Timer cursorTimer;
    private BufferedImage image;
    private Graphics2D gi;
    private Map<ColorChar, BufferedImage> charImages = new HashMap<>();

    private Thread repaintThread;
    private boolean repaintRequested;
    private int repaintMinX;
    private int repaintMinY;
    private int repaintMaxX;
    private int repaintMaxY;
    private final Object repaintLock = new String("RepaintLock");

    private char[][] reportContent;
    private char[][] realContent;
    private byte[][] realColors;
    private int color = 7;
    private int x, y;
    private BlockingQueue<Character> typedChars = new LinkedBlockingQueue<>();
    private char lastCh;
    private boolean readAgain;
    private boolean useNumPadEmulation = false;
    private boolean useKeyTyped = false;
    private boolean closeRequested = false;


    class TermPanel extends JPanel {

        @Override
        public void paint(Graphics g) {
            paint(g, false, 0, 0, 0, 0);
        }

        public void paint(Graphics g, boolean fromFrame, int minX, int minY, int maxX, int maxY) {
            Dimension dimension = getSize();
            int px = (int) ((dimension.width * screenScaleX - imageWidth) / 2 + 0.5);
            int py = (int) ((dimension.height * screenScaleY - imageHeight) / 2 + 0.5);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.scale(1.0 / screenScaleX, 1.0 / screenScaleY);
            if (fromFrame) {
                g2.translate(panelOffsetX, panelOffsetY);
            }
            if (fullRepaintRequested) {
                g2.setColor(Color.black);
                g2.fillRect(0, 0,
                        (int) (dimension.width * screenScaleX + 0.5),
                        (int) (dimension.height * screenScaleY + 0.5));
                fullRepaintRequested = false;
            }
            if (fromFrame && OPTIMIZED_REPAINT) {
                g2.drawImage(image, minX + px, minY + py, maxX + px, maxY + py,
                        minX, minY, maxX, maxY, this);
            } else {
                g2.drawImage(image, px, py, this);
            }
            g2.dispose();
        }
    }


    private static ANSITerm instance;


    private ANSITerm() {
        instance = this; // Set early to handle circular dependencies
    }

    public static ANSITerm instance() {
        if (instance == null)
            new ANSITerm(); // will set 'instance'
        return instance;
    }

    public void begin() {
        try {
            SwingUtilities.invokeAndWait(this::init);
        } catch (InvocationTargetException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void init() {
        // Load Topaz font
        if (USE_NATIVE_FONT) {
            topazFont = new Font("Consolas", Font.BOLD, 16);
        } else {
            InputStream topazStream;
            if (USE_NEW_TOPAZ) {
                topazStream = TopazFont.class.getResourceAsStream("Topaznew.ttf");
            } else {
                topazStream = TopazFont.class.getResourceAsStream("amiga-topaz-8.ttf");
            }
            try (topazStream) {
                topazFont = Font.createFont(Font.TRUETYPE_FONT, topazStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(topazFont);
            } catch (FontFormatException | IOException ex) {
                throw new RuntimeException(ex);
            }

            InputStream altTopazStream;
            if (USE_NEW_TOPAZ) {
                altTopazStream = TopazFont.class.getResourceAsStream("amiga-topaz-8.ttf");
            } else {
                altTopazStream = TopazFont.class.getResourceAsStream("Topaznew.ttf");
            }
            try (altTopazStream) {
                altTopazFont = Font.createFont(Font.TRUETYPE_FONT, altTopazStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(altTopazFont);
            } catch (FontFormatException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        // Determine default frame insets
        JFrame testFrame = new JFrame();
        testFrame.pack();
        Insets frameInsets = testFrame.getInsets();
        testFrame.dispose();

        // Determine font size based on screen size
        GraphicsDevice displayDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = displayDevice.getDefaultConfiguration();
        AffineTransform screenTransform = graphicsConfiguration.getDefaultTransform();
        screenScaleX = screenTransform.getScaleX();
        screenScaleY = screenTransform.getScaleY();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
        if (FULL_SCREEN)
            screenInsets = new Insets(0, 0, 0, 0);
        int availWidth = screenSize.width - screenInsets.left - screenInsets.right - (FULL_SCREEN ? 0 : frameInsets.left + frameInsets.right);
        int availHeight = screenSize.height - screenInsets.top - screenInsets.bottom - (FULL_SCREEN ? 0 : frameInsets.top + frameInsets.bottom);
        int screenWidth = (int) (availWidth * screenScaleX * SCREEN_FRACTION);
        int screenHeight = (int) (availHeight * screenScaleY * SCREEN_FRACTION);
        double fontWidth = (screenWidth - PAD_X * 2) / ((double) TERM_WIDTH * SCALE_X);
        double fontHeight = (screenHeight - PAD_Y * 2) / ((double) TERM_HEIGHT * SCALE_Y);
        fontSize = (int) Math.floor(Math.min(fontWidth, fontHeight));

        refreshRate = displayDevice.getDisplayMode().getRefreshRate();
        if (refreshRate < 30)
            refreshRate = 30;
        else if (refreshRate > 240)
            refreshRate = 240;

        // Create frame
        String title = Runtime.getAppNameOrDefault();
        if (title == null || title.isBlank()) {
            title = "Main Window";
        }
        frame = new JFrame(title);
        panel = new TermPanel();
        imageWidth = (int) Math.ceil(fontSize * TERM_WIDTH * SCALE_X);
        imageHeight = (int) Math.ceil(fontSize * TERM_HEIGHT * SCALE_Y);
        Dimension dimension = new Dimension(
                (int) Math.ceil(imageWidth / screenScaleX) + PAD_X * 2,
                (int) Math.ceil(imageHeight / screenScaleY) + PAD_Y * 2);
        panel.setPreferredSize(dimension);
        panel.setOpaque(true);
        panel.setBackground(Color.black);
        panel.setDoubleBuffered(false); // We already use an off-screen buffer
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel);
        frame.setBackground(Color.black);
        if (FULL_SCREEN)
            frame.setUndecorated(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (FULL_SCREEN) {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            device.setFullScreenWindow(frame);
        } else {
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
        panel.setFont(topazFont.deriveFont((float) fontSize));

        if (NB_BUFFERS > 1) {
            frame.createBufferStrategy(NB_BUFFERS);
            bufferStrategy = frame.getBufferStrategy();
            Point panelLocation = panel.getLocationOnScreen();
            Point frameLocation = frame.getLocationOnScreen();
            panelOffsetX = (int) ((panelLocation.x - frameLocation.x) * screenScaleX + 0.5);
            panelOffsetY = (int) ((panelLocation.y - frameLocation.y) * screenScaleY + 0.5);
        }

        // Clear cursor
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        if (FULL_SCREEN)
            frame.setCursor(blankCursor);

        // Create content
        reportContent = new char[TERM_HEIGHT][TERM_WIDTH];
        realContent = new char[TERM_HEIGHT][TERM_WIDTH];
        realColors = new byte[TERM_HEIGHT][TERM_WIDTH];
        for (int y = 0; y < TERM_HEIGHT; y++) {
            for (int x = 0; x < TERM_WIDTH; x++) {
                reportContent[y][x] = ' ';
                realContent[y][x] = ' ';
            }
        }

        // Create off-screen image
        image = panel.getGraphicsConfiguration().createCompatibleImage(imageWidth, imageHeight);
        gi = image.createGraphics();
        setupHighSpeed(gi);
        gi.setFont(topazFont.deriveFont((float) fontSize));
        gi.scale(SCALE_X, SCALE_Y);
        this.fontDescent = gi.getFontMetrics().getDescent();

        // Listen to keyboard
        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (useKeyTyped)
                    return;
                closeRequested = false;
                char ch = e.getKeyChar();
                if (ch != KeyEvent.CHAR_UNDEFINED) {
                    if (useNumPadEmulation && numpadMap.containsKey(ch)) {
                        ch = numpadMap.get(ch);
                    }
                    typedChars.add(ch);
                } else {
                    int keyCode = e.getKeyCode();
                    if (keyCode == KeyEvent.VK_UP) {
                        if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                            typedChars.add('8');
                        } else {
                            typedChars.add((char) 034);
                        }
                    } else if (keyCode == KeyEvent.VK_DOWN) {
                        if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                            typedChars.add('2');
                        } else {
                            typedChars.add((char) 035);
                        }
                    } else if (keyCode == KeyEvent.VK_LEFT) {
                        if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                            typedChars.add('7');
                        } else if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                            typedChars.add('1');
                        } else {
                            typedChars.add((char) 037);
                        }
                    } else if (keyCode == KeyEvent.VK_RIGHT) {
                        if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                            typedChars.add('9');
                        } else if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                            typedChars.add('3');
                        } else {
                            typedChars.add((char) 036);
                        }
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!useKeyTyped)
                    return;
                char ch = e.getKeyChar();
                if (ch != KeyEvent.CHAR_UNDEFINED) {
                    typedChars.add(ch);
                }
            }
            
        });

        // Listen to window closing. Send 'q', then CTRL-C
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (closeRequested) {
                    typedChars.add((char) 03);
                } else {
                    if (useKeyTyped) {
                        typedChars.add('\n');
                    } else {
                        typedChars.add('q');
                        closeRequested = true;
                    }
                }
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                repaintFull();
            }

        });
        
        if (!FULL_SCREEN) {
            planifyHideCursor();
            frame.addMouseMotionListener(new MouseMotionAdapter() {

                @Override
                public void mouseMoved(MouseEvent e) {
                    frame.setCursor(Cursor.getDefaultCursor());
                    planifyHideCursor();
                }
                
            });
        }

        // Listen to size changes
        panel.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                repaintFull();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                repaintFull();
            }

        });

        // Repaint when idle
        repaintThread = new Thread(this::repaintLoop, "Repaint Thread");
        repaintThread.setDaemon(true);
        repaintThread.start();
    }
    
    private void planifyHideCursor() {
        if (cursorTimer != null) {
            cursorTimer.restart();
        } else {
            cursorTimer = new Timer(2500, (e) -> {
                if (frame != null && frame.isVisible()) {
                    frame.setCursor(blankCursor);
                }
            });
            cursorTimer.start();
        }
    }

    private void repaintFull() {
        fullRepaintRequested = true;
        requestRepaint();
    }

    private void requestRepaint() {
        requestRepaint(0, 0, imageWidth, imageHeight);
    }

    private void requestRepaint(int minX, int minY, int maxX, int maxY) {
        synchronized (repaintLock) {
            repaintRequested = true;
            repaintMinX = Math.min(repaintMinX, minX);
            repaintMinY = Math.min(repaintMinY, minY);
            repaintMaxX = Math.max(repaintMaxX, maxX);
            repaintMaxY = Math.max(repaintMaxY, maxY);
        }
    }

    private void repaintLoop() {
        long repaintPeriod = 1000000000 / refreshRate;
        long lastRepaintTime = System.nanoTime();
        while (true) {
            long now = System.nanoTime();
            if (now < lastRepaintTime + repaintPeriod) {
                long toSleep = lastRepaintTime + repaintPeriod - now;
                try {
                    Thread.sleep(toSleep / 1000000, (int) (toSleep % 1000000));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                lastRepaintTime += repaintPeriod;
            } else {
                lastRepaintTime = now;
            }
            int minX, minY, maxX, maxY;
            boolean repaint;
            synchronized (repaintLock) {
                repaint = repaintRequested;
                minX = repaintMinX;
                minY = repaintMinY;
                maxX = repaintMaxX;
                maxY = repaintMaxY;
                repaintRequested = false;
                repaintMinX = Integer.MAX_VALUE;
                repaintMinY = Integer.MAX_VALUE;
                repaintMaxX = 0;
                repaintMaxY = 0;
            }
            if (repaint && (!OPTIMIZED_REPAINT || ((maxX > minX) && (maxY > minY)))) {
                if (bufferStrategy != null) {
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            do {
                                do {
                                    Graphics2D g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
                                    panel.paint(g2, true, minX, minY, maxX, maxY);
                                    g2.dispose();
                                } while (bufferStrategy.contentsRestored());
                                bufferStrategy.show();
                            } while (bufferStrategy.contentsLost());
                        });
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    panel.repaint();
//                    panel.repaint( // TODO fix when 1 BUFFERS and resizing window
//                            (int) (minX / screenScaleX) + PAD_X,
//                            (int) (minY / screenScaleY) + PAD_Y,
//                            (int) ((maxX + 1 - minX) / screenScaleX + 1.0),
//                            (int) ((maxY + 1 - minY) / screenScaleY + 1.0));
                }
            }
        }
    }

    private void setupHighQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    private void setupHighSpeed(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private void update(int x, int y) {
        Graphics2D g2 = (Graphics2D) gi.create();
        BufferedImage charImage = getCharImage(realContent[y][x], color, g2);
        g2.drawImage(charImage, x * fontSize, y * fontSize, null);
        g2.dispose();
        requestRepaint(x * fontSize * SCALE_X,
                y * fontSize * SCALE_Y,
                (x + 1) * fontSize * SCALE_X,
                (y + 1) * fontSize * SCALE_Y);
    }

    private BufferedImage getCharImage(char ch, int color, Graphics2D g) {
        if (USE_NATIVE_FONT) {
            if (ch == '*')
                ch = 'Ω';
        }
        ColorChar key = new ColorChar(ch, color);
        BufferedImage image = charImages.get(key);
        if (image == null) {
            image = g.getDeviceConfiguration().createCompatibleImage(fontSize, fontSize);
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.black);
            g2.fillRect(0, 0, fontSize, fontSize);
            setupHighQuality(g2);

            g2.setFont(topazFont.deriveFont((float) fontSize));
            g2.setColor(ANSI_COLORS[color]);
            int fontVertOffset = fontSize - fontDescent;
            if (ch == '£') { // "±"
                // This character is not present in topaz font. Draw manually
                if (USE_NEW_TOPAZ) {
                    g2.fillRect(fontSize / 6, fontVertOffset - fontSize / 7,
                            fontSize - fontSize / 3 + 1, fontSize / 7);
                } else {
                    g2.fillRect(0, fontVertOffset - fontSize * 2 / 7,
                            fontSize - fontSize / 4, fontSize / 7);
                }
                g2.scale(FONT_SCALE_X, FONT_SCALE_Y);
                g2.drawString("+", 0, fontVertOffset / FONT_SCALE_Y);
            } else {
                if (ch == '%') {
                    g2.setFont(altTopazFont.deriveFont((float) fontSize));
                    g2.scale(3 - FONT_SCALE_X, FONT_SCALE_Y);
                    g2.drawString(String.valueOf(ch), 0, fontVertOffset / FONT_SCALE_Y);
                } else {
                    g2.scale(FONT_SCALE_X, FONT_SCALE_Y);
                    g2.drawString(String.valueOf(ch), 0, fontVertOffset / FONT_SCALE_Y);
                }
            }
            g2.dispose();

            charImages.put(key, image);
        }
        return image;
    }

    public void Read(/* VAR */ Runtime.IRef<Character> ch) {
        if (readAgain) {
            ch.set(lastCh);
            readAgain = false;
            return;
        }
        if (typedChars.isEmpty()) {
            ch.set((char) 0);
            return;
        }
        Character next = typedChars.remove();
        ch.set(next);
        lastCh = next;
    }

    public void ReadAgain() {
        this.readAgain = true;
    }

    public void WaitChar(/* VAR */ Runtime.IRef<Character> ch) {
        requestRepaint();
        if (readAgain) {
            ch.set(lastCh);
            readAgain = false;
            return;
        }
        try {
            SwingUtilities.invokeLater(() -> frame.setCursor(Cursor.getDefaultCursor()));
            Character next = typedChars.take();
            ch.set(next);
            lastCh = next;
            if (FULL_SCREEN)
                SwingUtilities.invokeLater(() -> frame.setCursor(blankCursor));
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void ReadString(/* VAR */ Runtime.IRef<String> st) {
        boolean previousNpe = useNumPadEmulation;
        useNumPadEmulation = false;
        boolean previousKt = useKeyTyped;
        useKeyTyped = true;
        try {
            StringBuilder result = new StringBuilder();
            Runtime.IRef<Character> ch = new Runtime.Ref<>(' ');
            while (true) {
                WaitChar(ch);
                if (ch.get().equals('\n'))
                    break;
                if (ch.get() == (char) 127 || ch.get() == (char) 8) {
                    // Delete
                    if (result.length() > 0) {
                        Goto((byte) (this.x - 1), (byte) this.y);
                        Write(' ');
                        Goto((byte) (this.x - 1), (byte) this.y);
                        result.deleteCharAt(result.length() - 1);
                    }
                } else {
                    result.append(ch.get());
                    Write(ch.get());
                }
            }
            st.set(result.toString());
        } finally {
            useNumPadEmulation = previousNpe;
            useKeyTyped = previousKt;
        }
    }

    public char Report(byte x, byte y) {
        return reportContent[y][x];
    }

    public void Write(char ch) {
        char prevCh = realContent[y][x];
        byte prevColor = realColors[y][x];

        reportContent[y][x] = ch;
        realContent[y][x] = ch;
        realColors[y][x] = (byte) color;

        if (ch != prevCh || (byte) color != prevColor)
            update(x, y);
        x++;
        if (x >= TERM_WIDTH) {
            x = 0;
            y++;
            if (y >= TERM_HEIGHT)
                y = 0;
        }
    }

    public void WriteString(String st) {
        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            Write(ch);
        }
    }

    public void WriteLn() {
        x = 0;
        y++;
    }

    public void WriteAt(byte x, byte y, char ch) {
        Goto(x, y);
        Write(ch);
    }

    public void MoveChar(byte sx, byte sy, char sch, byte dx, byte dy, char dch) {
        WriteAt(sx, sy, sch);
        WriteAt(dx, dy, dch);
    }

    public void Ghost(byte x, byte y, char ch) {
        char previous = reportContent[y][x];
        WriteAt(x, y, ch);
        reportContent[y][x] = previous;
    }

    public void Goto(byte x, byte y) {
        this.x = x;
        this.y = y;
    }

    public void ClearScreen() {
        for (int y = 0; y < TERM_HEIGHT; y++) {
            for (int x = 0; x < TERM_WIDTH; x++) {
                WriteAt((byte) x, (byte) y, ' ');
            }
        }
    }

    public void ClearLine(byte y) {
        for (int x = 0; x < TERM_WIDTH; x++) {
            WriteAt((byte) x, (byte) y, ' ');
        }
    }

    public void Color(short c) {
        this.color = c;
    }

    public void close() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        }
    }
}
