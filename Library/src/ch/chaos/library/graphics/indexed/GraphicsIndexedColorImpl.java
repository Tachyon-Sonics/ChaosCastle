package ch.chaos.library.graphics.indexed;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.AreaPtr;
import ch.chaos.library.Memory;
import ch.chaos.library.graphics.AreaBase;
import ch.chaos.library.graphics.GraphicsBase;
import ch.chaos.library.graphics.scale.IndexedImage;
import ch.chaos.library.graphics.scale.IndexedImageScaler;
import ch.chaos.library.graphics.xbrz.Xbrz;
import ch.chaos.library.graphics.xbrz.Xbrz.ScalerCfg;
import ch.chaos.library.graphics.xbrz.XbrzHelper;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class GraphicsIndexedColorImpl extends GraphicsBase {

    private final static int SCALE = Graphics.SCALE;
    private final static boolean CIRCULAR_XBRZ = true;

    int nbScreenColor;
    byte[] rScreenPalette;
    byte[] gScreenPalette;
    byte[] bScreenPalette;


    private static int scale(int value) {
        return Graphics.scale(value);
    }

    public static int log2(int value) {
        return 32 - Integer.numberOfLeadingZeros(value - 1);
    }

    public static int bitsForColors(int nbColors) {
        int result = log2(nbColors);
        while (8 % result != 0)
            result++;
        return result;
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
            int nbColors = Memory.tagInt(tags, Graphics.aCOLOR, 16);
            
            { // Temporary: reject > 1 scale and 256 colors until implemented:
                if (width > 320 || height > 240)
                    return null;
                if (nbColors > 16) {
                    return null;
                }
            }
            JFrameArea frameArea = new JFrameArea(width, height);
            DoubleBufferArea area = new DoubleBufferArea(this, width, height, nbColors, SCALE);
            frameArea.setupBuffer(area);
            this.nbScreenColor = nbColors;
            this.rScreenPalette = area.getrExtPalette();
            this.gScreenPalette = area.getgExtPalette();
            this.bScreenPalette = area.getbExtPalette();
            return frameArea;
        } else if (type == Graphics.atMEMORY || type == Graphics.atMASK) {
            int nbColors = Memory.tagInt(tags, Graphics.aCOLOR, 16);
            MemoryArea area = new MemoryArea(this, width, height, nbColors, SCALE);
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
            /*
             * This is invoked continuously from the game loop, without any pause.
             * Hence we wait here for one frame since last call, based on the refresh rate.
             */
            super.vsync(true);

            frameArea.switchArea();
        }
    }

    @Override
    public void SetBuffer(boolean first, boolean off) {
        // Ignore as we handle buffers manually. However, this is generally invoked
        // after some drawings; hence refresh:
        UpdateArea();
    }

    @Override
    public void GetBuffer(IRef<Boolean> first, IRef<Boolean> off) {
        if (currentArea instanceof JFrameArea frameArea) {
            off.set(true);
            first.set(frameArea.isFirstBuffer());
        } else {
            super.GetBuffer(first, off);
        }
    }

    @Override
    public void UpdateArea() {
        if (currentArea instanceof JFrameArea frameArea) {
            frameArea.updateArea();
        }
    }

    @Override
    public void SetArea(AreaPtr a) {
        currentArea = (AreaBase) a;
    }

    @Override
    public void SetPalette(short color, short red, short green, short blue) {
        currentArea.setPalette(color, red, green, blue);
    }

    @Override
    public void SetCopyMode(EnumSet<Graphics.Modes> dm) {
        if (dm.equals(Graphics.cmCopy)) {
            currentArea.draw((g) -> {
                g.setPaintMode();
                g.setComposite(AlphaComposite.Src);
            });
        } else if (dm.equals(Graphics.cmTrans)) {
            currentArea.draw((g) -> {
                g.setPaintMode();
                g.setComposite(AlphaComposite.SrcOver);
            });
        } else if (dm.equals(Graphics.cmXor)) {
            currentArea.draw((g) -> {
                g.setXORMode(Color.BLACK);
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
        currentArea.setCurrentColor(color);
        applyColorAndPattern();
    }

    @Override
    public void SetBPen(long bpen) {
        Color color = currentArea.getColor((int) bpen);
        currentArea.setCurrentBackground(color);
        applyColorAndPattern();
    }

    @Override
    public void SetPat(short v) {
        currentArea.setCurrentPattern(v);
        applyColorAndPattern();
    }

    private void applyColorAndPattern() {
        Color color = new Color(currentArea.getCurrentColor().getRGB());
        Color back = new Color(currentArea.getCurrentBackground().getRGB());
        int pat = currentArea.getCurrentPattern();
        currentArea.draw((g) -> {
            if (pat == 4) {
                g.setPaint(color);
            } else if (pat == 0) {
                g.setPaint(back);
            } else {
                BufferedImage texture = g.getDeviceConfiguration().createCompatibleImage(2, 2);
                Graphics2D gt = texture.createGraphics();
                gt.setColor(back);
                gt.fillRect(0, 0, 2, 2);
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
                TexturePaint texturePaint = new TexturePaint(texture,
                        new Rectangle2D.Float(0.0f, 0.0f, 2.0f / (float) SCALE, 2.0f / (float) SCALE));
                g.setPaint(texturePaint);
            }
        });
    }

    @Override
    public void SetPattern(/* VAR */ byte[] pattern) {
        // Not used
        throw new UnsupportedOperationException("Not implemented: SetPattern");
    }

    @Override
    public void DrawPixel(short x, short y) {
        currentArea.draw((g) -> {
            g.fillRect(x, y, 1, 1);
        });
    }

    @Override
    public void DrawLine(short x1, short y1, short x2, short y2) {
        currentArea.draw((g) -> {
            g.drawLine(x1, y1, x2, y2);
        });
    }

    @Override
    public void OpenPoly(short x, short y) {
        currentArea.setCurrentPoly(new ArrayList<>());
        AddLine(x, y);
    }

    @Override
    public void AddLine(short x, short y) {
        currentArea.getCurrentPoly().add(new int[] { x, y });
    }

    /*
     * Based on https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order/1180256#1180256
     * Fixed for the fact y-coordinate is upside down in Java
     */
    private static boolean isPolygonClockwise(List<int[]> poly) {
        int area = 0;
        for (int i = 0; i < poly.size(); i++) {
            int p = (i - 1 + poly.size()) % poly.size();
            int[] p1 = poly.get(p);
            int[] p2 = poly.get(i);
            area += (p2[0] - p1[0]) * (-p2[1] - p1[1]);
        }
        return area >= 0;
    }

    private static Rectangle getPolygonBounds(List<int[]> poly) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int[] pt : poly) {
            minX = Math.min(minX, pt[0]);
            maxX = Math.max(maxX, pt[0]);
            minY = Math.min(minY, pt[1]);
            maxY = Math.max(maxY, pt[1]);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public void FillPoly() {
        List<int[]> poly0 = new ArrayList<>(currentArea.getCurrentPoly());

        /*
         * Damn, this is quite a nasty Java "bug". When Graphics.SCALE is 1, Java
         * does not fill/draw polygons consistently if we reverse the order of the
         * points.
         * 
         * Experimentally though, it seems the result is always better when the points
         * are in clockwise order.
         * 
         * Exemples that are counter-clockwise and do not draw nicely if not reversed:
         * - Player, 2nd shape (facing top right)
         * - Diamond bonus
         */
        if (!isPolygonClockwise(poly0)) {
            Collections.reverse(poly0);
        }
        Rectangle bounds = getPolygonBounds(poly0);
        currentArea.setCurrentPoly(null);
        currentArea.draw((g) -> {
            g = (Graphics2D) g.create();
            g.setClip(bounds.x, bounds.y, bounds.width + 1, bounds.height + 1);
            Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO);
            boolean first = true;
            for (int[] points : poly0) {
                if (first) {
                    path.moveTo(points[0] + 0.5, points[1] + 0.5);
                    first = false;
                } else {
                    path.lineTo(points[0] + 0.5, points[1] + 0.5);
                }
            }
            path.closePath();
            g.fill(path);
            g.draw(path);
            g.dispose();
        });
    }

    @Override
    public void FillRect(short x1, short y1, short x2, short y2) {
        /*
         * The only situation in which we modify a BufferArea during the game is when we draw
         * a pop message, and this starts by filling the background with FillRect.
         * 
         * bufferArea.modified is used to invalidate the cache of blended images
         */
        if (currentArea instanceof MemoryArea bufferArea) {
            bufferArea.modify(x1, y1, x2 - x1, y2 - y1);
        }
        currentArea.draw((g) -> {
            g.fillRect(x1, y1, x2 - x1, y2 - y1);
        });
    }

    @Override
    public void FillEllipse(short x1, short y1, short x2, short y2) {
        currentArea.draw((g) -> {
            Ellipse2D ellipse = new Ellipse2D.Double(x1, y1, x2 - x1, y2 - y1);
            g.fill(ellipse);
        });
    }

    @Override
    public void FillFlood(short x, short y, long borderCol) {
        // not used
        throw new UnsupportedOperationException("Not implemented: FillFlood");
    }

    @Override
    public void FillShadow(AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height) {
        // unused
        throw new UnsupportedOperationException("Not implemented: FillShadow");
    }

    @Override
    public void DrawShadow(AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height) {
        MemoryArea maskArea = (MemoryArea) ma;

        BufferedImage maskImage = maskArea.getInternalImage();
        WritableRaster maskRaster = maskImage.getRaster();

        // shadow has two colors (black and white)
        // Create a color model where 0 is transparent, and 1 is current color
        Color color = currentArea.getCurrentColor();
        byte[] reds = new byte[] { 0, (byte) color.getRed() };
        byte[] greens = new byte[] { 0, (byte) color.getGreen() };
        byte[] blues = new byte[] { 0, (byte) color.getBlue() };
        IndexColorModel shadowColorModel = new IndexColorModel(1, 2, reds, greens, blues, 0);

        BufferedImage shadowImage = new BufferedImage(shadowColorModel, maskRaster, false, null);
        currentArea.draw((g) -> {
            g = (Graphics2D) g.create();
            Graphics.resetScale(g);
            g.setComposite(AlphaComposite.SrcOver); // Copy, applying alpha
            g.drawImage(shadowImage,
                    scale(dx), scale(dy), scale(dx + width), scale(dy + height),
                    scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
            g.dispose();
        });
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
                            value = pixels2d[y + sy][x + sy];
                        else
                            value = pixels1d[(y + sy) * width + (x + sx)];
                        raster.setPixel(x, y, new int[] { (value >>> shifter) << shifter });
                    }
                }

                // Render image
                currentArea.draw((g) -> {
                    g.drawImage(image, dx, dy, null);
                });
            }
        } else if (currentArea instanceof MemoryArea bufferArea) {
            int nbColors = bufferArea.getNbColors();
            if (nbColors == 16) {
                if (Graphics.SCALE_XBRZ && Graphics.SCALE != 1) {
                    if (CIRCULAR_XBRZ) {
                        scaleXbrz16Circular(imageInfo, sx, sy, dx, dy, width, height, bufferArea);
                    } else {
                        scaleXbrz16(imageInfo, sx, sy, dx, dy, width, height, bufferArea);
                    }
                } else {
                    scaleCubic16(imageInfo, sx, sy, dx, dy, width, height, bufferArea);
                }
            } else {
                throw new UnsupportedOperationException("" + nbColors + " colors");
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    void scaleXbrz16(Graphics.Image imageInfo, short sx, short sy, 
            short dx, short dy, short width, short height, MemoryArea bufferArea) {
        final int Scale = Graphics.SCALE;
        
        // Extract block to scale into an RGB image
        BufferedImage srcImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = srcImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        short[] pixData = (short[]) imageInfo.data;
        // Every short is actually two 4-bit pixels...
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                short value = pixData[((y + sy) * width + (x + sx)) / 2];
                int pen = ((x % 2 == 0) ? value >>> 4 : value & 0xf);
                g2.setColor(bufferArea.getColor(pen));
                g2.fillRect(x, y, 1, 1);
            }
        }
        g2.dispose();
        
        // Decompose any large scale into multiple smaller steps
        List<Integer> scales = XbrzHelper.getScales(Scale);

        // Scale RGB image using xbrz
        int[] srcPixels = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] destPixels = null;
        int curWidth = width;
        int curHeight = height;
        int destWidth = width;
        int destHeight = height;

        for (int scale : scales) {
            destWidth = curWidth * scale;
            destHeight = curHeight * scale;
            ScalerCfg cfg = new ScalerCfg();
            cfg.equalColorTolerance = 0.0;
            destPixels = new Xbrz(scale, false, cfg).scaleImage(srcPixels, null, curWidth, curHeight);
            
            // Prepare for next iteration
            curWidth = destWidth;
            curHeight = destHeight;
            srcPixels = destPixels;
        }

        BufferedImage scaled = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
        scaled.setRGB(0, 0, destWidth, destHeight, destPixels, 0, destWidth);
        
        // Draw RGB image into target indexed image
        BufferedImage dstImage = bufferArea.getInternalImage();
        g2 = dstImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2.drawImage(scaled, dx * Scale, dy * Scale, null);
        g2.dispose();
    }
    
    
    private final static int CIRCULAR_PAD = 4;
    
    void scaleXbrz16Circular(Graphics.Image imageInfo, short sx, short sy, 
            short dx, short dy, short width, short height, MemoryArea bufferArea) {
        final int Scale = Graphics.SCALE;
        
        // Extract block to scale into an RGB image
        int extWidth = width + CIRCULAR_PAD * 2;
        int extHeight = height + CIRCULAR_PAD * 2;
        BufferedImage srcImage = new BufferedImage(extWidth, extHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = srcImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        short[] pixData = (short[]) imageInfo.data;
        // Every short is actually two 4-bit pixels...
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                short value = pixData[((y + sy) * width + (x + sx)) / 2];
                int pen = ((x % 2 == 0) ? value >>> 4 : value & 0xf);
                g2.setColor(bufferArea.getColor(pen));
                int px = x + CIRCULAR_PAD - width;
                while (px < extWidth) {
                    int py = y + CIRCULAR_PAD - height;
                    while (py < extHeight) {
                        if (px >= 0 && py >= 0)
                            g2.fillRect(px, py, 1, 1);
                        py += height;
                    }
                    px += width;
                }
            }
        }
        g2.dispose();
        
        // Decompose any large scale into multiple smaller steps
        List<Integer> scales = XbrzHelper.getScales(Scale);

        // Scale RGB image using xbrz
        int[] srcPixels = srcImage.getRGB(0, 0, extWidth, extHeight, null, 0, extWidth);
        int[] destPixels = null;
        int curWidth = extWidth;
        int curHeight = extHeight;
        int destWidth = extWidth;
        int destHeight = extHeight;

        for (int scale : scales) {
            destWidth = curWidth * scale;
            destHeight = curHeight * scale;
            ScalerCfg cfg = new ScalerCfg();
            cfg.equalColorTolerance = 0.0;
            destPixels = new Xbrz(scale, false, cfg).scaleImage(srcPixels, null, curWidth, curHeight);
            
            // Prepare for next iteration
            curWidth = destWidth;
            curHeight = destHeight;
            srcPixels = destPixels;
        }

        BufferedImage scaled = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
        scaled.setRGB(0, 0, destWidth, destHeight, destPixels, 0, destWidth);
        
        // Draw RGB image into target indexed image
        BufferedImage dstImage = bufferArea.getInternalImage();
        g2 = dstImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2.drawImage(scaled, 
                dx * Scale, dy * Scale, 
                (dx + width) * Scale, (dy + height) * Scale,
                CIRCULAR_PAD * Scale, CIRCULAR_PAD * Scale,
                (CIRCULAR_PAD + width) * Scale, (CIRCULAR_PAD + height) * Scale, null);
//        g2.drawImage(scaled, dx * Scale, dy * Scale, null);
        g2.dispose();
    }
    
    private void scaleCubic16(Graphics.Image imageInfo, short sx, short sy, 
            short dx, short dy, short width, short height, MemoryArea bufferArea) {
        IndexedImage srcImage = new IndexedImage(width, height, bufferArea.getNbColors());
        short[] pixData = (short[]) imageInfo.data;
        // Every short is actually two 4-bit pixels... TODO (3) handle black&white
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                short value = pixData[((y + sy) * width + (x + sx)) / 2];
                int pen = ((x % 2 == 0) ? value >>> 4 : value & 0xf);
                srcImage.set(x, y, pen);
            }
        }

        IndexedImage dstImage;
        final int Scale = Graphics.SCALE;
        if (Scale > 1) {
            // Scale
            IndexedImageScaler scaler = new IndexedImageScaler(Scale);
            dstImage = scaler.scale(srcImage);
        } else {
            dstImage = srcImage;
        }

        // Write
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int py = 0; py < Scale; py++) {
                    for (int px = 0; px < Scale; px++) {
                        int pen = dstImage.get(x * Scale + px, y * Scale + py);
                        bufferArea.writeUnscaledPixel((x + dx) * Scale + px, (y + dy) * Scale + py, pen);
                    }
                }
            }
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

    @Override
    public void CopyMask(AreaPtr sa, AreaPtr ma, short sx, short sy, short dx, short dy, short width, short height) {
        if (width <= 0 || height <= 0)
            return; // Damn, I REALLY called CopyMask with zero width/height !

        MemoryArea srcArea = (MemoryArea) sa;
        MemoryArea maskArea = (MemoryArea) ma;
        boolean invalidated = false;

        // Invalidate cache entry if either image was modified
        BlendedImageKey key = new BlendedImageKey(sx, sy, width, height, srcArea, maskArea);
        if (srcArea.isModified(sx, sy, width, height)
                || maskArea.isModified(sx, sy, width, height)) {
            invalidated = true;
            for (BlendedImageKey otherKey : new ArrayList<>(blendedImageCache.keySet())) {
                if (otherKey.srcArea() == sa || otherKey.maskArea() == ma) {
                    if (srcArea.isModified(otherKey.sx(), otherKey.sy(), otherKey.width(), otherKey.height())
                            || maskArea.isModified(otherKey.sx(), otherKey.sy(), otherKey.width(), otherKey.height())) {
                        blendedImageCache.remove(otherKey);
                    }
                }
            }
            assert !blendedImageCache.containsKey(key);
        }

        // Use a cache for blended images as they are expensive to create
        BufferedImage blendedImage = blendedImageCache.get(key);
        if (blendedImage == null) {
            int nbColors = srcArea.getNbColors();
            int transPen = findTransparentPen(srcArea);
            if (transPen >= 0 && transPen != nbColors - 1) {
                // A unused pen can be used as the transparent one
                blendedImage = createBlendedImageUsingTransPen(srcArea, maskArea, sx, sy, width, height, transPen);
            } else {
                // No unused pen is available
                blendedImage = createBlendedImageUsingAdditionalPen(srcArea, maskArea, sx, sy, width, height);
            }
            blendedImageCache.put(key, blendedImage);
            if (invalidated) {
                srcArea.clearModified();
                maskArea.clearModified();
            }
        }

        // Draw the transparent image to the destination
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

    private int findTransparentPen(MemoryArea srcArea) {
        int transPen = -1;
        for (int i = 0; i < srcArea.getNbColors(); i++) {
            if (!srcArea.getUsedPens().get(i)) {
                transPen = i;
                break;
            }
        }
        return transPen;
    }


    private final Map<BlendedImageKey, BufferedImage> blendedImageCache = new HashMap<>();


    /**
     * Create a BITMASK-Transparent image using the given unused color as the transparent one.
     */
    private BufferedImage createBlendedImageUsingTransPen(MemoryArea srcArea, MemoryArea maskArea, short sx, short sy,
            short width, short height, int transPen) {
        // Convert mask into an image with two colors: transparent (0) and white (1)
        byte[] rgbMask = new byte[] { 0, -1 };
        IndexColorModel maskModel = new IndexColorModel(1, 2, rgbMask, rgbMask, rgbMask, 0);
        BufferedImage maskImage = maskArea.getInternalImage();
        BufferedImage bitmaskImage = new BufferedImage(maskModel, maskImage.getRaster(), false, null);

        // Create a temporary transparent image
        int nbColors = srcArea.getNbColors();
        byte[] rBlend = srcArea.rIntPalette;
        byte[] gBlend = srcArea.gIntPalette;
        byte[] bBlend = srcArea.bIntPalette;

        // Last color must be white so that we can copy the mask:
        assert rBlend[nbColors - 1] == (byte) 255 && gBlend[nbColors - 1] == (byte) 255 && bBlend[nbColors - 1] == (byte) 255;
//      int nbBits = GraphicsIndexedColorImpl.bitsForColors(nbColors);
        int nbBits = 8; // Seems that transparent color only works in 8-bit

        SampleModel blendSampleModel = Helper.createSampleModel(scale(width), scale(height), nbBits);
        DataBuffer blendBuffer = blendSampleModel.createDataBuffer();
        WritableRaster blendRaster = Raster.createPackedRaster(blendBuffer, scale(width), scale(height), nbBits, null);
        IndexColorModel blendColorModel = new IndexColorModel(nbBits, nbColors, rBlend, gBlend, bBlend, transPen);
        BufferedImage blendedImage = new BufferedImage(blendColorModel, blendRaster, false, null);

        // Blend mask and source into temporary image
        Graphics2D g2 = blendedImage.createGraphics();
        g2.setComposite(AlphaComposite.Src); // Copy both colors and alpha from src
        g2.drawImage(bitmaskImage, 0, 0, scale(width), scale(height),
                scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
        g2.dispose();

        blendedImage = new BufferedImage(blendColorModel, blendRaster, false, null);
        g2 = blendedImage.createGraphics();
        BufferedImage srcImage = srcArea.getInternalImage();
        g2.setComposite(AlphaComposite.SrcIn); // Copy colors from src using existing alpha of dst
        g2.drawImage(srcImage, 0, 0, scale(width), scale(height),
                scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
        g2.dispose();

        return blendedImage;
    }

    /**
     * Create a BITMASK-Transparent image using an additional color as the transparent one
     */
    private BufferedImage createBlendedImageUsingAdditionalPen(MemoryArea srcArea, MemoryArea maskArea, short sx, short sy,
            short width, short height) {
        // Convert mask into an image with two colors: transparent (0) and white (1)
        byte[] rgbMask = new byte[] { 0, -1 };
        IndexColorModel maskModel = new IndexColorModel(1, 2, rgbMask, rgbMask, rgbMask, 0);
        BufferedImage maskImage = maskArea.getInternalImage();
        BufferedImage bitmaskImage = new BufferedImage(maskModel, maskImage.getRaster(), false, null);

        // Create a temporary transparent image
        /// Create a +1 color palette for it, the additional color 0 is transparent
        int nbColors = srcArea.getNbColors();
        byte[] rBlend = new byte[nbColors + 1];
        byte[] gBlend = new byte[nbColors + 1];
        byte[] bBlend = new byte[nbColors + 1];
        System.arraycopy(srcArea.rIntPalette, 0, rBlend, 1, nbColors);
        System.arraycopy(srcArea.gIntPalette, 0, gBlend, 1, nbColors);
        System.arraycopy(srcArea.bIntPalette, 0, bBlend, 1, nbColors);
        /// Make sure we have a white color so that we can copy the mask
        rBlend[nbColors] = (byte) 255;
        gBlend[nbColors] = (byte) 255;
        bBlend[nbColors] = (byte) 255;
        int nbBits = bitsForColors(nbColors + 1);
        SampleModel blendSampleModel = Helper.createSampleModel(scale(width), scale(height), nbBits);
        DataBuffer blendBuffer = blendSampleModel.createDataBuffer();
        WritableRaster blendRaster = Raster.createPackedRaster(blendBuffer, scale(width), scale(height), nbBits, null);
        IndexColorModel blendColorModel = new IndexColorModel(nbBits, nbColors + 1, rBlend, gBlend, bBlend, 0);
        BufferedImage blendedImage = new BufferedImage(blendColorModel, blendRaster, false, null);

        // Blend mask and source into temporary image
        Graphics2D g2 = blendedImage.createGraphics();
        g2.setComposite(AlphaComposite.Src); // Copy both colors and alpha from src
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, scale(width), scale(height));
        g2.drawImage(bitmaskImage, 0, 0, scale(width), scale(height),
                scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
        g2.dispose();

        rBlend[nbColors] = srcArea.rIntPalette[nbColors - 1];
        gBlend[nbColors] = srcArea.gIntPalette[nbColors - 1];
        bBlend[nbColors] = srcArea.bIntPalette[nbColors - 1];
        blendColorModel = new IndexColorModel(nbBits, nbColors + 1, rBlend, gBlend, bBlend, 0);
        blendedImage = new BufferedImage(blendColorModel, blendRaster, false, null);
        g2 = blendedImage.createGraphics();
        BufferedImage srcImage = srcArea.getInternalImage();
        g2.setComposite(AlphaComposite.SrcIn); // Copy colors from src using existing alpha of dst
        g2.drawImage(srcImage, 0, 0, scale(width), scale(height),
                scale(sx), scale(sy), scale(sx + width), scale(sy + height), null);
        g2.dispose();
        return blendedImage;
    }


    private BufferedImage image4scroll;


    @Override
    public void ScrollRect(short x, short y, short width, short height, short dx, short dy) {
        BufferedImage image = currentArea.getInternalImage();
        currentArea.draw((g) -> {
            // in-place copy does not seem to work...
//            g = (Graphics2D) g.create();
//            g.setComposite(AlphaComposite.Src);
//            g.scale(1.0 / SCALE, 1.0 / SCALE);
//            g.setClip(scale(x), scale(y), scale(width), scale(height));
//            g.drawImage(image,
//                    scale(x + dx), scale(y + dy),
//                    scale(x + dx + width), scale(y + dy + height),
//                    scale(x), scale(y),
//                    scale(x + width), scale(y + height), null);
//            g.dispose();

            g = (Graphics2D) g.create();

            // Copy to temp image
            if (image4scroll == null || image4scroll.getWidth() < scale(width) || image4scroll.getHeight() < scale(height)) {
                image4scroll = g.getDeviceConfiguration().createCompatibleImage(scale(width), scale(height));
            }
            Graphics2D gt = image4scroll.createGraphics();
            gt.setComposite(AlphaComposite.Src);
            gt.drawImage(image, 0, 0, scale(width), scale(height),
                    scale(x), scale(y), scale(x + width), scale(y + height), null);
            gt.dispose();

            // Copy temp image back
            g.setComposite(AlphaComposite.Src);
            Graphics.resetScale(g);
            g.setClip(scale(x), scale(y), scale(width), scale(height));
            g.drawImage(image4scroll,
                    scale(x + dx), scale(y + dy),
                    scale(x + dx + width), scale(y + dy + height),
                    0, 0,
                    scale(width), scale(height), null);
            g.dispose();
        });
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
