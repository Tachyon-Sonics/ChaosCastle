package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import ch.chaos.castle.utils.Coord;

public class BinaryLevel {

    protected final int width;
    protected final int height;
    protected final boolean[][] walls;
    
    
    public BinaryLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.walls = new boolean[width][height];
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
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
    
    protected void setWall(Coord coord, boolean wall) {
        setWall(coord.x(), coord.y(), wall);
    }
    
    protected void setWall(int x, int y, boolean wall) {
        if (isOutside(x, y))
            return;
        walls[x][y] = wall;
    }
    
    protected void fillOval(int sx, int sy, int width, int height, boolean wall) {
        boolean odd = ((width % 2) != 0);
        int ph2 = height * height;
        int pw2 = width * width;
        int phd = ph2 / 2;
        int h = -height + 1;
        int w = 1;
        int y = 0;
        do {
            int w2 = ((ph2 - h * h) * pw2 + phd) / ph2;
            while (w * w < w2) {
                w++;
            }
            if (((w % 2) != 0) != odd)
                w--;
            int x = sx + (width - w) / 2;
            int by = sy + y;
            int ey = sy + height - y - 1;
            fillRect(x, by, w, 1, wall);
            if (h != 0)
                fillRect(x, ey, w, 1, wall);
            y++;
            h += 2;
        } while (h <= 0);
    }
    
    protected void fillRect(int sx, int sy, int width, int height, boolean wall) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setWall(sx + x, sy + y, wall);
            }
        }
    }
    
    protected void drawRect(int sx, int sy, int width, int height, boolean wall) {
        for (int x = 0; x < width; x++) {
            setWall(x, sy, wall);
            setWall(x, sy + height - 1, wall);
        }
        for (int y = 0; y < height; y++) {
            setWall(sx, y, wall);
            setWall(sx + width - 1, y, wall);
        }
    }
    
    /**
     * @return whether at least one coordinate was filled. If false, the root coordinate already had the
     * given wall status
     */
    protected boolean fillFlood(Coord root, boolean wall) {
        return fillFlood(root, wall, null);
    }
    
    protected boolean fillFlood(Coord root, boolean wall, Consumer<Coord> onFill) {
        if (isWall(root) == wall)
            return false;
        Set<Coord> todo = new HashSet<>();
        todo.add(root);
        while (!todo.isEmpty()) {
            Set<Coord> nextBatch = new HashSet<>();
            for (Coord coord : todo) {
                setWall(coord, wall);
                if (onFill != null)
                    onFill.accept(coord);
                for (Coord delta : Coord.n4()) {
                    Coord next = coord.add(delta);
                    if (isWall(next) != wall && !todo.contains(next)) {
                        nextBatch.add(next);
                    }
                }
            }
            todo = nextBatch;
        }
        return true;
    }
    
    /**
     * Fill all empty space whose distance to a wall (euclidean) is greater than the given value
     */
    public void fillInterior(int distance) {
        List<Coord> toFill = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isWall(x, y)) {
                    boolean isFree = true;
                    for (int dx = -distance; dx <= distance; dx++) {
                        for (int dy = -distance; dy <= distance; dy++) {
                            if (dx * dx + dy * dy <= distance * distance) {
                                if (isWall(x + dx, y + dy)) {
                                    isFree = false;
                                }
                            }
                        }
                    }
                    if (isFree) {
                        toFill.add(new Coord(x, y));
                    }
                }
            }
        }
        for (Coord coord : toFill) {
            setWall(coord, true);
        }
    }
    
    public void removeDiagonalsMakeHole() {
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                /*
                 * 01 -> 01
                 * 10    00
                 */
                if (!isWall(x, y) && !isWall(x + 1, y + 1) && isWall(x + 1, y) && isWall(x, y + 1)) {
                    setWall(x, y + 1, false);
                } else 
                /*
                 * 10 -> 00
                 * 01    01
                 */
                if (!isWall(x + 1, y) && !isWall(x, y + 1) && isWall(x, y) && isWall(x + 1, y + 1)) {
                    setWall(x, y, false);
                }
            }
        }
    }
    
    public void removeDiagonalsMakeWall() {
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                /*
                 * 01 -> 11
                 * 10    10
                 */
                if (!isWall(x, y) && !isWall(x + 1, y + 1) && isWall(x + 1, y) && isWall(x, y + 1)) {
                    setWall(x, y, true);
                } else 
                /*
                 * 10 -> 11
                 * 01    01
                 */
                if (!isWall(x + 1, y) && !isWall(x, y + 1) && isWall(x, y) && isWall(x + 1, y + 1)) {
                    setWall(x + 1, y, true);
                }
            }
        }
    }
    
    /**
     * Get all 4-distances from the given coordinate.
     * <p>
     * In the resulting list, index N contains the list of coordinates whose distance to
     * the given coordinate is N.
     */
    public List<List<Coord>> getDistancesFrom(Coord coord) {
        List<List<Coord>> result = new ArrayList<>();
        // BFS
        Set<Coord> visited = new HashSet<>();
        Set<Coord> toVisit = new HashSet<>();
        toVisit.add(coord);
        while (!toVisit.isEmpty()) {
            result.add(new ArrayList<>(toVisit));
            Set<Coord> next = new HashSet<>();
            for (Coord curCoord : toVisit) {
                visited.add(curCoord);
                for (Coord delta : Coord.n4()) {
                    Coord nextCoord = curCoord.add(delta);
                    if (!isWall(nextCoord)) {
                        if (!visited.contains(nextCoord) && !toVisit.contains(nextCoord)) {
                            next.add(nextCoord);
                        }
                    }
                }
            }
            toVisit = next;
        }
        return result;
    }
    
    public Map<Coord, Integer> remapDistances(List<List<Coord>> distances) {
        Map<Coord, Integer> result = new HashMap<>();
        for (int distance = 0; distance < distances.size(); distance++) {
            for (Coord coord : distances.get(distance)) {
                result.put(coord, distance);
            }
        }
        return result;
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
