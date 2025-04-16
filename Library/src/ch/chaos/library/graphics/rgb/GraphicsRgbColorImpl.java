package ch.chaos.library.graphics.rgb;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.AreaPtr;
import ch.chaos.library.Memory;
import ch.chaos.library.graphics.AreaBase;
import ch.chaos.library.graphics.GraphicsBase;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class GraphicsRgbColorImpl extends GraphicsBase {

    // Whether to use alpha transparency, notable for SetPat()
    public final static boolean USE_ALPHA_TRANSPARENCY = true;

    private final static int SCALE = Graphics.SCALE;

    int nbScreenColor;
    byte[] rScreenPalette;
    byte[] gScreenPalette;
    byte[] bScreenPalette;
    final List<Runnable> screenPaletteChangeListeners = new ArrayList<>();

    private List<int[]> currentPoly;
    private Color currentColor = Color.WHITE;
    private Color currentBackground = Color.BLACK;
    private int currentBPen = 0;
    private int currentPattern = 4;


    private static int scale(int value) {
        return Graphics.scale(value);
    }

    public static void setupHighQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static void setupHighSpeed(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    }

    @Override
    public AreaPtr CreateArea(Memory.TagItem tags) {
        int type = Memory.tagInt(tags, Graphics.aTYPE, Graphics.atMEMORY);
        int width = Memory.tagInt(tags, Graphics.aSIZEX, 320);
        int height = Memory.tagInt(tags, Graphics.aSIZEY, 240);
        if (type == Graphics.atDISPLAY) {
            return new JFrameArea(width, height);
        } else if (type == Graphics.atBUFFER) {
            JFrameArea frameArea = new JFrameArea(width, height);
            int nbColors = Memory.tagInt(tags, Graphics.aCOLOR, 16);
            BufferArea area = new BufferArea(this, width, height, nbColors, SCALE, false, true);
            frameArea.setupBuffer(area);
            this.nbScreenColor = nbColors;
            this.rScreenPalette = area.rPalette;
            this.gScreenPalette = area.gPalette;
            this.bScreenPalette = area.bPalette;
            return frameArea;
        } else if (type == Graphics.atMEMORY || type == Graphics.atMASK) {
            int nbColors = Memory.tagInt(tags, Graphics.aCOLOR, 16);
            BufferArea area = new BufferArea(this, width, height, nbColors, SCALE, type == Graphics.atMASK, false);
            return area;
        } else {
            throw new UnsupportedOperationException("Unsupported display type: " + type);
        }
    }

    @Override
    public void DeleteArea(IRef<AreaPtr> a) {
        AreaBase area = (AreaBase) a.get();
        if (area != null) {
            area.close();
        }
        a.set(null);
    }

    @Override
    public void AreaToFront() {
        currentArea.bringToFront();
    }

    @Override
    public void SwitchArea() {
        if (currentArea instanceof JFrameArea frameArea) {
            try {
                SwingUtilities.invokeAndWait(frameArea::switchArea);
            } catch (InvocationTargetException | InterruptedException ex) {
                ex.printStackTrace();
            }
//            frameArea.switchArea();
        }
    }

    @Override
    public void UpdateArea() {
        if (currentArea instanceof JFrameArea frameArea) {
            frameArea.repaint();
        }
    }

    @Override
    public void SetArea(AreaPtr a) {
        currentArea = (AreaBase) a;
        if (currentArea instanceof JFrameArea frameArea) {
            frameArea.repaint();
        }
    }

    @Override
    public void SetPalette(short color, short red, short green, short blue) {
        currentArea.setPalette(color, red, green, blue);
    }

    @Override
    public void SetCopyMode(EnumSet<Graphics.Modes> dm) {
        if (dm.equals(Graphics.cmCopy)) {
            currentArea.draw((g) -> {
                g.setComposite(AlphaComposite.Src);
            });
        } else if (dm.equals(Graphics.cmTrans)) {
            currentArea.draw((g) -> {
                g.setComposite(AlphaComposite.SrcOver);
            });
        } else if (dm.equals(Graphics.cmXor)) {
            currentArea.draw((g) -> {
                g.setComposite(AlphaComposite.Xor);
            });
        } else {
            throw new UnsupportedOperationException("Not implemented: SetCopyMode " + dm.toString());
        }
    }

    @Override
    public void SetPlanes(long planes, boolean clear) {
        // todo implement SetPlanes
        throw new UnsupportedOperationException("Not implemented: SetPlanes");
    }

    @Override
    public void SetPen(long pen) {
        Color color = currentArea.getColor((int) pen);
        currentColor = color;
        applyColorAndPattern();
    }

    @Override
    public void SetBPen(long bpen) {
        currentBPen = (int) bpen;
        Color color = currentArea.getColor((int) bpen);
        currentBackground = color;
        applyColorAndPattern();
    }

    @Override
    public void SetPat(short v) {
        currentPattern = v;
        applyColorAndPattern();
    }

    private void applyColorAndPattern() {
        if (USE_ALPHA_TRANSPARENCY) {
            // Mix between foreground and background
            float cr = currentColor.getRed() / 255.0f;
            float cg = currentColor.getGreen() / 255.0f;
            float cb = currentColor.getBlue() / 255.0f;
            float br = currentBackground.getRed() / 255.0f;
            float bg = currentBackground.getGreen() / 255.0f;
            float bb = currentBackground.getBlue() / 255.0f;
            float red = (cr * currentPattern + br * (4 - currentPattern)) / 4.0f;
            float green = (cg * currentPattern + bg * (4 - currentPattern)) / 4.0f;
            float blue = (cb * currentPattern + bb * (4 - currentPattern)) / 4.0f;
            Color color = new Color(red, green, blue);
            currentArea.draw((g) -> {
                g.setComposite(AlphaComposite.SrcOver);
                g.setColor(color);
            });
        } else {
            Color color = new Color(currentColor.getRGB());
            Color back = new Color(currentBackground.getRGB());
            int pat = currentPattern;
            int bpen = currentBPen;
            currentArea.draw((g) -> {
                if (pat == 4) {
                    g.setPaint(color);
                } else if (pat == 0) {
                    g.setPaint(back);
                } else {
                    BufferedImage texture = g.getDeviceConfiguration().createCompatibleImage(2, 2);
                    Graphics2D gt = texture.createGraphics();
                    if (bpen != 0) {
                        gt.setColor(back);
                        gt.fillRect(0, 0, 2, 2);
                    }
                    gt.setColor(color);
                    if (pat == 1) // Half-tone pattern, 1 .. 4: 4 = plain, 2 = 1/2 pixels, 0 = fully transparent, etc
                        gt.fillRect(1, 1, 1, 1);
                    else if (pat >= 1) {
                        gt.fillRect(0, 0, 1, 1);
                        if (pat >= 2)
                            gt.fillRect(1, 1, 1, 1);
                        if (pat >= 3)
                            gt.fillRect(1, 0, 1, 1);
                    }
                    // Note: incorrect when drawing directly on-screen (and not in a buffer)
                    TexturePaint texturePaint = new TexturePaint(texture,
                            new Rectangle2D.Float(0.0f, 0.0f, 2.0f / (float) SCALE, 2.0f / (float) SCALE));
                    g.setPaint(texturePaint);
                }
            });
        }
    }

    @Override
    public void SetPattern(/* VAR */ byte[] pattern) {
        // Not used
        throw new UnsupportedOperationException("Not implemented: SetPattern");
    }

    @Override
    public void DrawPixel(short x, short y) {
        currentArea.draw((g) -> {
            g = (Graphics2D) g.create();
            GraphicsRgbColorImpl.setupHighSpeed(g);
            g.fillRect(x, y, 1, 1);
            g.dispose();
        });
    }

    @Override
    public void DrawLine(short x1, short y1, short x2, short y2) {
        currentArea.draw((g) -> {
            Line2D shape = new Line2D.Double(x1, y1, x2, y2);
            g.draw(shape);
        });
    }

    @Override
    public void OpenPoly(short x, short y) {
        currentPoly = new ArrayList<>();
        AddLine(x, y);
    }

    @Override
    public void AddLine(short x, short y) {
        currentPoly.add(new int[] { x, y });
    }

    @Override
    public void FillPoly() {
        List<int[]> poly0 = new ArrayList<>(currentPoly);
        currentPoly = null;
        currentArea.draw((g) -> {
            Path2D polygon = new Path2D.Double();
            int[] last = poly0.get(poly0.size() - 1);
            polygon.moveTo(last[0], last[1]);
            for (int[] points : poly0) {
                polygon.lineTo(points[0], points[1]);
            }
            g.fill(polygon);
        });
    }

    @Override
    public void FillRect(short x1, short y1, short x2, short y2) {
        currentArea.draw((g) -> {
            g = (Graphics2D) g.create();
            GraphicsRgbColorImpl.setupHighSpeed(g);
            g.fillRect(x1, y1, x2 - x1, y2 - y1);
            g.dispose();
        });
    }

    @Override
    public void FillEllipse(short x1, short y1, short x2, short y2) {
        currentArea.draw((g) -> {
            Shape shape = new Ellipse2D.Double(x1, y1, x2 - x1, y2 - y1);
            g.fill(shape);
        });
    }

    @Override
    public void FillFlood(short x, short y, long borderCol) {
        // unused
        throw new UnsupportedOperationException("Not implemented: FillFlood");
    }

    @Override
    public void FillShadow(AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height) {
        // unused
        throw new UnsupportedOperationException("Not implemented: FillShadow");
    }

    @Override
    public void DrawShadow(AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height) {
        // todo implement DrawShadow
        throw new UnsupportedOperationException("Not implemented: DrawShadow");
    }

    @Override
    public void DrawImage(Graphics.Image imageInfo, short sx, short sy, short dx, short dy, short width, short height) {
        if (currentArea instanceof JFrameArea) {
            if (imageInfo.data instanceof short[][] || imageInfo.data instanceof short[]) {
                // No palette. Use std color cube
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
                WritableRaster raster = image.getRaster();

                int bitPerPix = imageInfo.bitPerPix;
                int shifter = 8 - bitPerPix;

                // Copy pixels
                short[][] pixels2d = null;
                short[] pixels1d = null;
                if (imageInfo.data instanceof short[][] data2d)
                    pixels2d = data2d;
                else if (imageInfo.data instanceof short[] data1d)
                    pixels1d = data1d;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        short value;
                        if (pixels2d != null)
                            value = pixels2d[y][x];
                        else
                            value = pixels1d[y * width + x];
                        raster.setPixel(x, y, new int[] { (value >>> shifter) << shifter });
                    }
                }

                // Render image
                currentArea.draw((g) -> {
                    g.drawImage(image, 0, 0, null);
                });
            }
        } else if (currentArea instanceof BufferArea bufferArea) {
            short[] pixData = (short[]) imageInfo.data;
            // Every short is actually two 4-bit pixels...
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    short value = pixData[((y + sy) * width + (x + sx)) / 2];
                    int pen = ((x % 2 == 0) ? value >>> 4 : value & 0xf);
                    bufferArea.writePixel(x + dx, y + dy, pen);
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void CopyRect(AreaPtr sa, short sx, short sy, short dx, short dy, short width, short height) {
        AreaBase srcArea = (AreaBase) sa;
        BufferedImage srcImage = srcArea.getInternalImage();
        currentArea.draw((g) -> {
            g = (Graphics2D) g.create();
            Graphics.resetScale(g);
            g.drawImage(srcImage, scale(dx), scale(dy), scale(dx + width), scale(dy + height),
                    scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
            g.dispose();
        });
    }

    @Override
    public void CopyShadow(AreaPtr sa, AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height) {
        CopyMask(sa, ma, sx, sy, dx, dy, width, height);
    }


    private final Map<BlendedImageKey, BufferedImage> blendedImageCache = new HashMap<>();


    @Override
    public void CopyMask(AreaPtr sa, AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height) {
        if (USE_ALPHA_TRANSPARENCY) {
            BufferArea srcArea = (BufferArea) sa;
            BufferedImage srcImage = srcArea.getInternalImage();
            currentArea.draw((g) -> {
                g = (Graphics2D) g.create();
                Graphics.resetScale(g);
                g.setComposite(AlphaComposite.SrcOver);
                g.drawImage(srcImage, scale(dx), scale(dy), scale(dx + width), scale(dy + height),
                        scale(0), scale(0), scale(width), scale(height), null);
                g.dispose();
            });
        } else {
            BufferArea srcArea = (BufferArea) sa;
            BufferArea maskArea = (BufferArea) ma;
            BufferedImage srcImage = srcArea.getInternalImage();
            BufferedImage maskImage = maskArea.getInternalImage();

            // Create transparent blended image combining src image and mask
            BlendedImageKey key = new BlendedImageKey(sx, sy, width, height, srcArea, maskArea);
            BufferedImage blendedImage = blendedImageCache.get(key);
            if (blendedImage == null) {
                blendedImage = createBlendedImage(sx, sy, width, height, srcImage, maskImage);
                blendedImageCache.put(key, blendedImage);
            }

            // Draw blended image
            final BufferedImage blendedImage0 = blendedImage;
            currentArea.draw((g) -> {
                g = (Graphics2D) g.create();
                Graphics.resetScale(g);
                g.setComposite(AlphaComposite.SrcOver);
                g.drawImage(blendedImage0, scale(dx), scale(dy), scale(dx + width), scale(dy + height),
                        scale(0), scale(0), scale(width), scale(height), null);
                g.dispose();
            });
        }
    }

    private BufferedImage createBlendedImage(short sx, short sy, short width, short height, BufferedImage srcImage, BufferedImage maskImage) {
        Graphics2D g = srcImage.createGraphics();
        GraphicsConfiguration gc = g.getDeviceConfiguration();
        BufferedImage blendedImage = gc.createCompatibleImage(scale(width), scale(height), Transparency.BITMASK);
        g.dispose();

        g = blendedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(maskImage, 0, 0, scale(width), scale(height),
                scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(srcImage, 0, 0, scale(width), scale(height),
                scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
        g.dispose();
        return blendedImage;
    }

    @Override
    public void ScrollRect(short x, short y, short width, short height, short dx, short dy) {
        // todo implement ScrollRect
        throw new UnsupportedOperationException("Not implemented: ScrollRect");
    }

    @Override
    public void ScaleRect(AreaPtr sa, short sx1, short sy1, short sx2, short sy2, short dx1, short dy1, short dx2, short dy2) {
        AreaBase srcArea = (AreaBase) sa;
        BufferedImage srcImage = srcArea.getInternalImage();
        currentArea.draw((g) -> {
            g = (Graphics2D) g.create();
            Graphics.resetScale(g);
            g.drawImage(srcImage, scale(dx1), scale(dy1), scale(dx2), scale(dy2),
                    scale(sx1), scale(sy1), scale(sx2), scale(sy2), null);
            g.dispose();
        });
    }

}
