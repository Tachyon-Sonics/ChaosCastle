package ch.chaos.library.graphics.scale;

public class IndexedImage {

    private final int width;
    private final int height;
    private final int nbColors;
    private final int[][] data;


    public IndexedImage(int width, int height, int nbColors) {
        this.width = width;
        this.height = height;
        this.nbColors = nbColors;
        this.data = new int[width][height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNbColors() {
        return nbColors;
    }

    public int get(int x, int y) {
        return data[x][y];
    }

    public void set(int x, int y, int color) {
        data[x][y] = color;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = get(x, y);
                if (color < 10)
                    result.append(" ");
                result.append(color);
                result.append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

}
