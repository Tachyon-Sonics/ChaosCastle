package ch.chaos.library.graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.GraphicsErr;
import ch.chaos.library.Graphics.TextModes;
import ch.chaos.library.Memory;
import ch.chaos.library.utils.AccurateSleeper;
import ch.chaos.library.utils.FpsStats;
import ch.pitchtech.modula.runtime.Runtime;

public abstract class GraphicsBase implements IGraphics {

    private final static double TEXT_FONT_WIDEN = 1.0;

    protected AreaBase currentArea;
    protected Point textPosition = new Point(0, 0);
    protected float textSize = 8.0f;

    private long refreshPeriod;
    private long lastRefresh = System.nanoTime();
    private AccurateSleeper mrSandman = new AccurateSleeper();


    public GraphicsBase() {
        GraphicsDevice displayDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int refreshRate = displayDevice.getDisplayMode().getRefreshRate();
        if (refreshRate < 10)
            refreshRate = 60;
        else if (refreshRate > 120)
            refreshRate = 120;
        this.refreshPeriod = 1000000000L / refreshRate;
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
        float x = textPosition.x + (Graphics.SCALE > 1 ? 0.5f : 0.0f);
        float y = textPosition.y + textSize - 1;
        currentArea.draw((g) -> {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.scale(TEXT_FONT_WIDEN, 1.0);
            g2.drawString(t.get(), 0, 0);
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
        long prev = lastRefresh;
        long nextRefresh = lastRefresh + refreshPeriod;
        long now = System.nanoTime();
        if (now < nextRefresh) {
            long sleepTime = nextRefresh - now;
            mrSandman.sleep(sleepTime);
            lastRefresh = nextRefresh;
//            System.out.println("Missed: " + ((System.nanoTime() - nextRefresh) / 1000) + " us");
        } else if (now < nextRefresh + refreshPeriod) {
            // Try to recover up to two missed frames // TODO check vsync in ExtendedBufferCapabilities
            lastRefresh = nextRefresh;
        } else {
            lastRefresh = now;
        }

        // Update FPS stats
        long elapsed = System.nanoTime() - prev;
        double fps = 1000000000.0 / (double) elapsed;
        FpsStats.instance(FpsStats.INTERNAL).accumulate(fps);
    }

}
