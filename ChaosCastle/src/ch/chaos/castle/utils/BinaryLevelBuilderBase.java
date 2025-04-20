package ch.chaos.castle.utils;


public class BinaryLevelBuilderBase {

    protected final int width;
    protected final int height;
    protected final boolean[][] walls;
    
    
    public BinaryLevelBuilderBase(int width, int height) {
        this.width = width;
        this.height = height;
        this.walls = new boolean[width][height];
    }

    protected int nbSurroundingWalls4(Coord coord) {
        int result = 0;
        for (Coord delta : Coord.n4()) {
            if (isWall(coord.x() + delta.x(), coord.y() + delta.y())) {
                result++;
            }
        }
        return result;
    }
    
    protected boolean isWall(Coord coord) {
        return isWall(coord.x(), coord.y());
    }
    
    protected boolean isWall(int x, int y) {
        if (isOutside(x, y))
            return true;
        return walls[x][y];
    }
    
    protected boolean isInsideWall(int x, int y) {
        if (isOutside(x, y))
            return false;
        return walls[x][y];
    }
    
    protected void setWall(int x, int y, boolean wall) {
        if (isOutside(x, y))
            return;
        walls[x][y] = wall;
    }
    
    protected boolean isOutside(Coord coord) {
        return isOutside(coord.x(), coord.y());
    }
    
    protected boolean isOutside(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return true;
        return false;
    }
    
    protected boolean isBoundary(Coord coord) {
        return isBoundary(coord.x(), coord.y());
    }
    
    protected boolean isBoundary(int x, int y) {
        if (isOutside(x, y))
            return true;
        return (x == 0 || y == 0 || x == width - 1 || y == height - 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < height; y+= 2) {
            for (int x = 0; x < width; x++) {
                if (isInsideWall(x, y) && isInsideWall(x, y + 1)) {
                    builder.append("█");
                } else if (isInsideWall(x, y) && !isInsideWall(x, y + 1)) {
                    builder.append("▀");
                } else if (!isInsideWall(x, y) && isInsideWall(x, y + 1)) {
                    builder.append("▄");
                } else {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

}
