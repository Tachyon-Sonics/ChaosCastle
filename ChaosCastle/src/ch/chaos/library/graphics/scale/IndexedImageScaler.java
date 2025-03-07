package ch.chaos.library.graphics.scale;

public class IndexedImageScaler {

    private final static boolean ENHANCE_ISOLATED_PIXELS = false;

    private final int scale;
    private final Kernel kernel;


    public IndexedImageScaler(int scale) {
        this.scale = scale;
//        int length = scale * 3;
//        this.kernel = new SincKernel(((length + 1) / 2) * 2, 2);
//        this.kernel = new LinearKernel(scale);
        this.kernel = new BicubicKernel(scale * 5 / 2);
    }

    public IndexedImage scale(IndexedImage srcImage) {
        int nbColors = srcImage.getNbColors();
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        // Create float black/white images for each color
        FloatImage[] colorImages = new FloatImage[nbColors];
        for (int color = 0; color < nbColors; color++) {
            colorImages[color] = new FloatImage(width, height);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (srcImage.get(x, y) == color) {
                        colorImages[color].set(x, y, 1.0f);
                    }
                }
            }

            // Enhance isolated pixels
            if (ENHANCE_ISOLATED_PIXELS) {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        if (colorImages[color].get(x, y) >= 1.0f) {
                            int n = 0;
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    if (colorImages[color].get(x + dx, y + dy) >= 1.0f)
                                        n++;
                                }
                            }
                            if (n <= 1) {
                                colorImages[color].set(x, y, 1.5f);
                            } else if (n <= 2) {
                                colorImages[color].set(x, y, 1.2f);
                            }
                        }
                    }
                }
            }
        }

        // Create sinc-scaled black/white images for each color
        FloatImage[] scaledColorImages = new FloatImage[nbColors];
        for (int color = 0; color < nbColors; color++) {
            scaledColorImages[color] = scale(colorImages[color]);
            assert scaledColorImages[color].getWidth() == width * scale;
            assert scaledColorImages[color].getHeight() == height * scale;
        }

        // Create final image
        IndexedImage dstImage = new IndexedImage(width * scale, height * scale, nbColors);
        for (int x = 0; x < width * scale; x++) {
            for (int y = 0; y < height * scale; y++) {
                int maxColor = 0;
                float maxValue = 0.0f;
                for (int color = 0; color < nbColors; color++) {
                    float colorValue = scaledColorImages[color].get(x, y);
                    if (colorValue > maxValue) {
                        maxValue = colorValue;
                        maxColor = color;
                    }
                }
                dstImage.set(x, y, maxColor);
            }
        }

        return dstImage;
    }

    private FloatImage scale(FloatImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int kernelLength = kernel.getLength();
        FloatImage scaled = new FloatImage(width * scale, height * scale);
        for (int sx = 0; sx < width; sx++) {
            for (int sy = 0; sy < height; sy++) {
                float srcValue = image.get(sx, sy);
                int dx = sx * scale;
                int dy = sy * scale;
                for (int kx = -kernelLength + 1; kx < kernelLength; kx++) {
                    for (int ky = -kernelLength + 1; ky < kernelLength; ky++) {
                        float kernelValue = kernel.get(kx, ky);
                        scaled.add(dx + kx, dy + ky, srcValue * kernelValue);
                    }
                }
            }
        }
        return scaled;
    }

}
