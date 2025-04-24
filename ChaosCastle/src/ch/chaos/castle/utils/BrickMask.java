package ch.chaos.castle.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickMask {

    private final int width;
    private final int height;
    private final boolean[][] bricks;
    private final boolean[][] shadow; // neighbour4 of bricks
    private BrickMask clone;
    private boolean borderHit = false;


    public BrickMask(int width, int height) {
        this.width = width;
        this.height = height;
        this.bricks = new boolean[height][];
        for (int i = 0; i < height; i++)
            bricks[i] = new boolean[width];
        this.shadow = new boolean[height][];
        for (int i = 0; i < height; i++)
            shadow[i] = new boolean[width];
        setBrick(width / 2, height / 2);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private boolean isBorderHit() {
        return borderHit;
    }

    private void setBrick(int x, int y) {
        bricks[y][x] = true;
        
        // Update shadow
        setShadow(x, y, false);
        for (Coord delta : Coord.n4()) {
            if (!isBrick(x + delta.x(), y + delta.y())) {
                setShadow(x + delta.x(), y + delta.y(), true);
            } else {
                setShadow(x + delta.x(), y + delta.y(), false);
            }
        }
    }

    private void setShadow(int x, int y, boolean value) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return;
        shadow[y][x] = value;
        if (value) {
            if (x <= 1 || y <= 1 || x >= width - 2 || y >= height - 2)
                borderHit = true;
        }
    }

    /**
     * Whether the given coordinate is a brick (or wall)
     */
    public boolean isBrick(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return false;
        return bricks[y][x];
    }

    /**
     * Whether the given coordinate is in the 4-neighbourhood of a brick
     */
    private boolean isShadow(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return false;
        return shadow[y][x];
    }

    /**
     * Whether the given coordinate is a candidate for adding a brick.
     * <p>
     * It must
     * <ul>
     * <li>Not already be a brick ({@link #isBrick(int, int)})
     * <li>Be a 4-neighbour of an existing brick ({@link #isShadow(int, int)})
     * <li>Not be a bridge ({@link #isBridge(int, int)})
     * </ul>
     */
    private boolean getRoadCandidate(int x, int y) {
        if (x < 2 || y < 2 || x >= width - 2 || y >= height - 2)
            return false;
        boolean result = isShadow(x, y);
        if (!result)
            return false;
        if (isBridge(x, y))
            return false;
        return result;
    }

    /**
     * Whether the given coordinate is a "bridge" that would break the surrounding curve formed
     * by the shadow coordinates.
     * The given coordinate is assumed to be part of the shadow (4-neighbour of an existing brick).
     * <p>
     * To check if the given coordinate is a bridge:
     * <ul>
     * <li>It is set as a brick (on a copy)
     * <li>For all 8 neighbours of that brick that are part of the shadow ({@link #isShadow(int, int)} - note that the shadow
     * are coordinates that are 4-neighbourg of an existing brick)
     * <ul>
     * <li>The number of 8-neighbours that are also shadow are counted
     * <li>If that number is greater than 2 for at least one of them, the initial coordinate is considered a bridge
     * <li>If none of the 8-neighbour have more than 2 shadow 8-neighbours, the initial coordinate is not a bridge
     * </ul>
     * </ul>
     * <p>
     * Exemple:
     * <pre>
     *  SS
     * SBBS
     *  Ss
     * 
     * Check on 's':
     *  SS
     * SBBS
     *  SbS
     *   S
     * </pre>
     * No 'S' in the 8-neibourghood of 'b' has more than 2 8-neighbourgs that are also 'S'. -&gt; 'b' is not a bridge
     * <p>
     * Example 2:
     * <pre>
     *  SS
     * SBBBS
     * SBSs
     * SBBS
     *  SS
     *  
     * Check on 's':
     *  SS
     * SBBBS
     * SBSbS
     * SBB$
     *  SS
     * </pre>
     * The '$' has 3 8-neighbours that are also 'S'. -&gt; 'b' is a bridge
     * <p>
     * The intuitive idea is that every shadow must always have two neighbours that are also
     * shadow, so that a "curve" can be built by moving from one shadow to the next.
     * As soon as one shadow has 3 neighbours, it is no longer a single curve.
     */
    private boolean isBridge(int x, int y) {
        getClone();
        BrickMask clone = this.clone;
        clone.setBrick(x, y);
        try {
            for (Coord delta : Coord.n8()) {
                // Check shadows with more than two neighbours
                if (clone.isShadow(x + delta.x(), y + delta.y())) {
                    int nbNeighbours = 0;
                    for (Coord delta2 : Coord.n8()) {
                        if (clone.isShadow(x + delta.x() + delta2.x(), y + delta.y() + delta2.y()))
                            nbNeighbours++;
                    }
                    if (nbNeighbours > 2)
                        return true;
                }
            }
            return false;
        } finally {
            clone.copyFrom(this, x - 1, y - 1, x + 1, y + 1);
        }
    }

    private int getNbRoadCandidates() {
        int result = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (getRoadCandidate(x, y))
                    result++;
            }
        }
        return result;
    }

    private void setNthRoadCandidate(int index) {
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (getRoadCandidate(x, y)) {
                    if (i == index) {
                        setBrick(x, y);
                        return;
                    }
                    i++;
                }
            }
        }
    }

    public int[] toTravel(long seed, boolean filterLines) {
        final int[] dxs = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
        final int[] dys = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
        List<Integer> result = new ArrayList<Integer>();
        Random rnd = new Random(seed);

        // Find start location
        int x = width / 2;
        int y = height / 2;
        int direction = 0;
        while (!isShadow(x, y))
            y--;
        int sx = x;
        int sy = y;

        // Travel
        do {
            int loc = y * width + x;
            if (result.contains(loc))
                throw new IllegalStateException("Circular Error in Travel");
            result.add(loc);
            while (!isShadow(x + dxs[direction], y + dys[direction]))
                direction = (direction + 1) % 8;
            x += dxs[direction];
            y += dys[direction];
            direction = (direction + 6) % 8;
        } while (x != sx || y != sy);

        // Rotate at random position and randomly invert
        int count = result.size();
        List<Integer> rotated = new ArrayList<Integer>(count);
        int offset = rnd.nextInt(count * 2);
        int sign = 1;
        if (offset >= count) {
            offset -= count;
            sign = -1;
        }
        for (int i = 0; i < count; i++)
            rotated.add(result.get((i * sign + offset + count) % count));

        // Filter lines
        List<Integer> filtered = new ArrayList<Integer>();
        if (filterLines) {
            int index = 0;
            while (index < count) {
                int loc = rotated.get(index);
                y = loc / width;
                x = loc % width;
                boolean includeIt = true;
                if (index > 1 && index < count - 2 && rnd.nextInt(4) > 0) {
                    int prevLoc = rotated.get(index - 1);
                    int py = prevLoc / width;
                    int px = prevLoc % width;
                    int dy1 = y - py;
                    int dx1 = x - px;
                    int nextLoc = rotated.get(index + 1);
                    int ny = nextLoc / width;
                    int nx = nextLoc % width;
                    int dy2 = ny - y;
                    int dx2 = nx - x;
                    if (dy1 == dy2 && dx1 == dx2)
                        includeIt = false;
                }
                if (includeIt)
                    filtered.add(loc);
                index++;
            }
        } else {
            filtered.addAll(rotated);
        }

        int[] arr = new int[filtered.size()];
        for (int i = 0; i < arr.length; i++)
            arr[i] = filtered.get(i);
        return arr;
    }

    private void copyFrom(BrickMask other) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.bricks[y][x] = other.bricks[y][x];
                this.shadow[y][x] = other.shadow[y][x];
            }
        }
    }

    private void copyFrom(BrickMask other, int sx, int sy, int ex, int ey) {
        for (int y = sy; y <= ey; y++) {
            for (int x = sx; x <= ex; x++) {
                this.bricks[y][x] = other.bricks[y][x];
                this.shadow[y][x] = other.shadow[y][x];
            }
        }
    }

    public BrickMask getClone() {
        if (clone == null) {
            clone = new BrickMask(width, height);
            clone.copyFrom(this);
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isShadow(x, y)) {
                    builder.append("██");
                } else if (isBrick(x, y)) {
                    builder.append("░░");
                } else {
                    builder.append("  ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }
    
    public BinaryLevelBuilderBase toBinaryLevel() {
        BinaryLevelBuilderBase level = new BinaryLevelBuilderBase(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean empty = isBrick(x, y) || isBrick(x + 1, y) || isBrick(x, y + 1);
                level.setWall(x, y, !empty);
            }
        }
        return level;
    }

    public String toString(int[] travel) {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < height; y++) {
            builder.append(toString(y, travel));
            builder.append("\n");
        }
        return builder.toString();
    }

    public String toString(int rowNum, int[] travel) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < width; x++) {
            if (isShadow(x, rowNum)) {
                int index = -1;
                int loc = rowNum * width + x;
                for (int i = 0; i < travel.length; i++) {
                    if (travel[i] == loc)
                        index = i;
                }
                if (index >= 0) {
                    builder.append(index / 10);
                    builder.append(index % 10);
                } else {
//                    builder.append("██");
                    builder.append("??");
                }
            } else if (isBrick(x, rowNum)) {
                builder.append("##");
            } else {
                builder.append("  ");
            }
        }
        return builder.toString();
    }

    public static BrickMask buildBrickMask(long seed, int nbBricksHorz, int nbBricksVert) {
        BrickMask brickMask = new BrickMask(nbBricksHorz, nbBricksVert);
        Random rnd = new Random(seed);
        int base = nbBricksHorz * nbBricksVert / 7 + 1;
        int nbBlocks = base + rnd.nextInt(base);
        int k = 0;
        while (k < nbBlocks || !brickMask.isBorderHit()) {
            // Get number of candidates for next hole
            int num = brickMask.getNbRoadCandidates();
            if (num == 0)
                break;
            
            // Choose a candidate randomly and mark as hole
            int index = rnd.nextInt(num);
            brickMask.setNthRoadCandidate(index);
            k++;
        }
        return brickMask;
    }

    public static void main(String[] args) {
        BrickMask mask = buildBrickMask(new Random().nextLong(), 100, 100);
        int[] travel = mask.toTravel(1, false);
        System.out.println(mask.toString(travel));
        System.out.println(mask.toString());
        System.out.println(mask.toBinaryLevel().toString());
    }

}
