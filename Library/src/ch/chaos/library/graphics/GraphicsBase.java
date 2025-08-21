package ch.chaos.library.graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import ch.chaos.library.Clock;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.GraphicsErr;
import ch.chaos.library.Graphics.TextModes;
import ch.chaos.library.Memory;
import ch.chaos.library.settings.Settings;
import ch.chaos.library.settings.VsyncType;
import ch.chaos.library.utils.AccurateSleeper;
import ch.chaos.library.utils.FpsStats;
import ch.chaos.library.utils.Platform;
import ch.pitchtech.modula.runtime.Runtime;

public abstract class GraphicsBase implements IGraphics {

    private final static double TEXT_FONT_WIDEN = 1.0;

    protected AreaBase currentArea;
    protected Point textPosition = new Point(0, 0);
    protected float textSize = 8.0f;

    private long refreshPeriod;
    private long lastRefresh = System.nanoTime();
    private AccurateSleeper mrSandman;
    
    private VsyncType vsyncType;
    private AliasedTextDrawer aliasedTextDrawer;


    public GraphicsBase() {
        GraphicsDevice displayDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int refreshRate = displayDevice.getDisplayMode().getRefreshRate();
        if (refreshRate < 10) // -1 if unknown: force 60 Hz
            refreshRate = 60;
        else if (refreshRate > 120)
            refreshRate = 120;
        this.refreshPeriod = 1000000000L / refreshRate;
        
        if (Platform.isMacOsX()) {
            // Drawing text without antialiasing on macOS is challenging...
            aliasedTextDrawer = new AliasedTextDrawer();
        }

        vsyncType = Settings.appMode().getVsyncType();
        if (vsyncType == VsyncType.BALANCED_LOW || vsyncType == VsyncType.BALANCED_HIGH) {
            double security = switch (vsyncType) {
                case BALANCED_LOW -> 1.2;
                case BALANCED_HIGH -> 1.5;
                default -> throw new IllegalArgumentException("Unexpected value: " + vsyncType);
            };
            mrSandman = new AccurateSleeper(security);
        }
    }
    
    public static int scale() {
        return Settings.appMode().getInnerScale();
    }

    @Override
    public void GetGraphicsSysAttr(/* VAR */ Memory.TagItem what) {
        int result = switch (what.tag) {
            case Graphics.aSIZEX -> 320;
            case Graphics.aSIZEY -> 240;
            case Graphics.aCOLOR -> 16;
            default -> throw new IllegalArgumentException("Unexpected value: " + what.tag);
        };
        what.data = result;
    }

    @Override
    public GraphicsErr GetGraphicsErr() {
        return GraphicsErr.gOk;
    }

    @Override
    public void SetBuffer(boolean first, boolean off) {
        // Ignore. Subclasses may want to repaint
    }

    @Override
    public void GetBuffer(/* VAR */ Runtime.IRef<Boolean> first, /* VAR */ Runtime.IRef<Boolean> off) {
        first.set(true);
        off.set(false);
    }

    @Override
    public void SetTextPos(short x, short y) {
        textPosition = new Point(x, y);
    }

    @Override
    public short TextWidth(Runtime.IRef<String> t) {
        AtomicInteger result = new AtomicInteger();
        currentArea.draw((g) -> {
            Rectangle2D rect = g.getFontMetrics().getStringBounds(t.get(), g);
            result.set((int) (rect.getWidth() * TEXT_FONT_WIDEN + 0.5));
        });
        return (short) result.get();
    }

    @Override
    public void DrawText(Runtime.IRef<String> t) {
        float x = textPosition.x + (scale() > 1 ? 0.5f : 0.0f);
        float y = textPosition.y;
        currentArea.draw((g) -> {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.scale(TEXT_FONT_WIDEN, 1.0);
            if (aliasedTextDrawer != null) {
                aliasedTextDrawer.drawText(g2, t.get());
            } else {
                g2.drawString(t.get(), 0, textSize - 1);
            }
            g2.dispose();
        });
        int width = TextWidth(t);
        textPosition.translate(width, 0);
    }

    @Override
    public void SetTextMode(EnumSet<TextModes> tm) {
        currentArea.draw((g) -> {
            int style = 0;
            if (tm.contains(TextModes.bold))
                style |= Font.BOLD;
            if (tm.contains(TextModes.italic))
                style |= Font.ITALIC;
            // outline and shadow not used (and not supported)
            g.setFont(g.getFont().deriveFont(style));

        });
    }

    @Override
    public void SetTextSize(short s) {
        textSize = s;
        currentArea.draw((g) -> {
            g.setFont(g.getFont().deriveFont(textSize));
        });
    }

    @Override
    public void WaitTOF() {
        vsync(false);
    }
    
    public void vsync(boolean accurateSleep) {
        long prev = lastRefresh;
        long nextRefresh = lastRefresh + refreshPeriod;
        long now = System.nanoTime();
        
        if (now < nextRefresh) {
            long sleepTime = nextRefresh - now;
            if (accurateSleep) {
                accurateSleep(sleepTime);
                
                /*
                 * This fixes a design bug in the original ChaosCastle's code. In order to calculate how many
                 * frames were missed, it queries the clock not after vsync, but while moving the player. This could
                 * occur quite at any moment because there is no guarantee that the player is the first or last
                 * sprite. Furthermore, it queries the value in 300 FPS units, potentially resulting in fractional
                 * missed frames.
                 * 
                 * The result is that, on a 60 FPS system, even when no frame is missed, it will randomly get values
                 * among {4, 5, 6} instead of constantly getting 5 (300 / 60, or the duration of a single frame).
                 * Together with various rounding issues, this makes scrolling less smooth than it should be.
                 * 
                 * To fix this bug, we "lock" the clock at vsync, so it will not change until the next
                 * vsync (or until 3 missed vsync, for instance if no vsync occur - like level finished and
                 * displaying the Shop).
                 */
                Clock.instance().setVsyncTime(nextRefresh, nextRefresh + refreshPeriod, refreshPeriod);
            } else {
                AccurateSleeper.threadSleep(sleepTime);
            }
            lastRefresh = nextRefresh;
        } else if (now < nextRefresh + refreshPeriod) {
            // Try to recover up to two missed frames
            lastRefresh = nextRefresh;
        } else {
            lastRefresh = now;
        }

        // Update FPS stats
        if (accurateSleep) {
            long elapsed = System.nanoTime() - prev;
            double fps = 1000000000.0 / (double) elapsed;
            FpsStats.instance(FpsStats.INTERNAL).accumulate(fps);
        }
    }

    // Accurate sleep, used for soft V-Sync
    private void accurateSleep(long sleepTime) {
        switch (vsyncType) {
            case SLEEP -> {
                AccurateSleeper.threadSleep(sleepTime);
            }
            case BALANCED_LOW, BALANCED_HIGH -> {
                mrSandman.sleep(sleepTime);
            }
            case ACTIVE -> {
                long start = System.nanoTime();
                // Busy wait
                while (System.nanoTime() < start + sleepTime) {
                    
                }
            }
        }
    }

}
