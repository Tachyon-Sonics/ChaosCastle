package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    
    public boolean isWall(Coord coord) {
        return isWall(coord.x(), coord.y());
    }
    
    public boolean isWall(int x, int y) {
        if (isOutside(x, y))
            return true;
        return walls[x][y];
    }
    
    public boolean isInsideWall(int x, int y) {
        if (isOutside(x, y))
            return false;
        return walls[x][y];
    }
    
    public void setWall(Coord coord, boolean wall) {
        setWall(coord.x(), coord.y(), wall);
    }
    
    public void setWall(int x, int y, boolean wall) {
        if (isOutside(x, y))
            return;
        walls[x][y] = wall;
    }
    
    public void fillOval(int sx, int sy, int width, int height, boolean wall) {
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
    
    public void fillRect(int sx, int sy, int width, int height, boolean wall) {
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
     * @param wall as how to fill
     * @return whether at least one coordinate was filled. If false, the root coordinate already had the
     * given wall status
     */
    protected boolean fillFlood(Coord root, boolean wall) {
        return fillFlood(root, wall, null);
    }
    
    public boolean fillFlood(Coord root, boolean wall, Consumer<Coord> onFill) {
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
                    if (isWall(next) != wall && !todo.contains(next) && !isOutside(next)) {
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
    
    public int countHoles() {
        int result = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isWall(x, y))
                    result++;
            }
        }
        return result;
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
    
    public List<Coord> getFarthestFrom(Coord base) {
        List<List<Coord>> distances = getDistancesFrom(base);
        return distances.get(distances.size() - 1);
    }
    
    public Coord pickFarthestFrom(Coord base, Random rnd) {
        List<Coord> candidates = getFarthestFrom(base);
        int index = rnd.nextInt(candidates.size());
        return candidates.get(index);
    }
    
    public void invert() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setWall(x, y, !isWall(x, y));
            }
        }
    }
    
    /**
     * For each walls, set all the 8-neighbours as wall
     */
    public void growWalls8() {
        List<Coord> toAdd = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isWall(x, y)) {
                    for (Coord delta : Coord.n8()) {
                        Coord n = delta.add(x, y);
                        if (!isOutside(n) && !isWall(n)) {
                            toAdd.add(n);
                        }
                    }
                }
            }
        }
        for (Coord coord : toAdd) {
            setWall(coord, true);
        }
    }
    
    /**
     * Get all the top-left locations of which the given shape (considering wall cells) can be placed without hitting
     * a wall (onWall false) or hole (onWall true).
     * <p>
     * Only the rectangle specified by the given coordinates is considered
     * @param shape the shape, as wall-set coordinates
     * @param onWall whether to place the shape entirely on walls, or entirely on holes
     */
    public List<Coord> randomPlacesFor(BinaryLevel shape, boolean onWall, int sx, int sy, int width, int height) {
        List<Coord> result = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Coord topLeft = new Coord(x + sx, y + sy);
                
                boolean isValid = true;
                for (int mx = 0; mx < shape.getWidth(); mx++) {
                    for (int my = 0; my < shape.getHeight(); my++) {
                        if (shape.isWall(mx, my)) {
                            Coord pos = topLeft.add(mx, my);
                            if (pos.x() < sx || pos.y() < sy || pos.x() >= sx + width || pos.y() >= sy + height) {
                                isValid = false; // Outside of given rectangle
                            }
                            if (isWall(pos) != onWall) {
                                isValid = false; // Not entirely in wall / hole
                            }
                        }
                    }
                }
                
                if (isValid) {
                    result.add(topLeft);
                }
            }
        }
        return result;
    }
    
    /**
     * Draw all cells of the given shape that are wall into this shape
     */
    public void drawShape(BinaryLevel shape, Coord where, boolean wall) {
        for (int x = 0; x < shape.getWidth(); x++) {
            for (int y = 0; y < shape.getHeight(); y++) {
                if (shape.isWall(x, y)) {
                    setWall(where.add(x, y), wall);
                }
            }
        }
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
    
    public BinaryLevel copy() {
        BinaryLevel result = new BinaryLevel(width, height);
        result.drawShape(this, new Coord(0, 0), true);
        return result;
    }
    
    public void forWalls(Consumer<Coord> onWall) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isWall(x, y)) {
                    onWall.accept(new Coord(x, y));
                }
            }
        }
    }
    
    public void forHoles(Consumer<Coord> onWall) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isWall(x, y)) {
                    onWall.accept(new Coord(x, y));
                }
            }
        }
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
