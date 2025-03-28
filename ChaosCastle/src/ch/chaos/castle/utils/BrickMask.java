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
        setBrick(height / 2, width / 2);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private boolean isNeighbour4(int fx, int fy) {
        return (fx != 0) != (fy != 0);
    }

    private boolean isNeighbour8(int fx, int fy) {
        return fx != 0 || fy != 0;
    }

    public boolean isBorderHit() {
        return borderHit;
    }

    public void setBrick(int x, int y) {
        bricks[y][x] = true;
        for (int fy = -1; fy <= 1; fy++) {
            for (int fx = -1; fx <= 1; fx++) {
                if (isNeighbour4(fx, fy))
                    setShadow(x + fx, y + fy);
            }
        }
    }

    public void setRoad(int x, int y) {
        bricks[y][x] = false;
        setShadow(x, y);
        ;
    }

    private void setShadow(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return;
        shadow[y][x] = true;
        if (x <= 1 || y <= 1 || x >= width - 2 || y >= height - 2)
            borderHit = true;
    }

    public boolean getBrick(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return false;
        return bricks[y][x];
    }

    public boolean getShadow(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return false;
        return shadow[y][x];
    }

    public boolean getRoad(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return false;
        boolean result = getShadow(x, y) && !getBrick(x, y);
        if (!result)
            return false;
        return result;
    }

    public boolean getRoadCandidate(int x, int y) {
        if (x < 2 || y < 2 || x >= width - 2 || y >= height - 2)
            return false;
        boolean result = getShadow(x, y) && !getBrick(x, y);
        if (!result)
            return false;
        if (isBridge(x, y))
            return false;
        return result;
    }

    private boolean isBridge(int x, int y) {
        getClone();
        BrickMask clone = this.clone;
        clone.setBrick(x, y);
        try {
            for (int fy = -1; fy <= 1; fy++) {
                for (int fx = -1; fx <= 1; fx++) {
                    if (isNeighbour8(fx, fy)) {
                        // Check Roads with more than two neighbours
                        if (clone.getRoad(x + fx, y + fy)) {
                            int nbNeighbours = 0;
                            for (int fy2 = -1; fy2 <= 1; fy2++) {
                                for (int fx2 = -1; fx2 <= 1; fx2++) {
                                    if (isNeighbour8(fx2, fy2)) {
                                        if (clone.getRoad(x + fx + fx2, y + fy + fy2))
                                            nbNeighbours++;
                                    }
                                }
                            }
                            if (nbNeighbours > 2)
                                return true;
                        }
                    }
                }
            }
            return false;
        } finally {
            clone.copyFrom(this, x - 1, y - 1, x + 1, y + 1);
        }
    }

    public int getNbRoadCandidates() {
        int result = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (getRoadCandidate(x, y))
                    result++;
            }
        }
        return result;
    }

    public void setNthRoadCandidate(int index) {
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

    public int getNbShadowAround(int x, int y) {
        int result = 0;
        for (int fy = -1; fy <= 1; fy++) {
            for (int fx = -1; fx <= 1; fx++) {
                if (getShadow(x + fx, y + fy))
                    result++;
            }
        }
        return result;
    }

    public int getNbRoadAround(int x, int y) {
        int result = 0;
        for (int fy = -1; fy <= 1; fy++) {
            for (int fx = -1; fx <= 1; fx++) {
                if (getRoad(x + fx, y + fy))
                    result++;
            }
        }
        return result;
    }

    /**
     * Get, among the 8 directions, the one for which there is no road nearby
     * @return an array of directions, where each direction is given as a two element array [dx, dy]
     */
    public int[][] getFreeDirectionsAround(int x, int y) {
        final int[] dxs = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
        final int[] dys = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
        final int[] startOffsets = new int[] { -2, -1, 1 };
        final int[] stopOffsets = new int[] { -1, 1, 2 };
        final int[] startOffsetsD = new int[] { -2, -1, 0 };
        final int[] stopOffsetsD = new int[] { 0, 1, 2 };
        List<int[]> result = new ArrayList<int[]>();
        for (int dir = 0; dir < dxs.length; dir++) {
            int dx = dxs[dir];
            int dy = dys[dir];
            boolean diag = (dx != 0) == (dy != 0);
            int sx = x + (diag ? startOffsetsD[dx + 1] : startOffsets[dx + 1]);
            int ex = x + (diag ? stopOffsetsD[dx + 1] : stopOffsets[dx + 1]);
            int sy = y + (diag ? startOffsetsD[dy + 1] : startOffsets[dy + 1]);
            int ey = y + (diag ? stopOffsetsD[dy + 1] : stopOffsets[dy + 1]);
            boolean isFree = true;
            for (int py = sy; py <= ey; py++) {
                for (int px = sx; px <= ex; px++) {
                    if (px != x || py != y) {
                        if (getRoad(px, py))
                            isFree = false;
                    }
                }
            }
            if (isFree)
                result.add(new int[] { dx, dy });
        }
        int[][] arr = new int[result.size()][];
        for (int i = 0; i < arr.length; i++)
            arr[i] = result.get(i);
        return arr;
    }

    public int[] toTravel(long seed) {
        final int[] dxs = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
        final int[] dys = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
        List<Integer> result = new ArrayList<Integer>();
        Random rnd = new Random(seed);

        // Find start location
        int x = width / 2;
        int y = height / 2;
        int direction = 0;
        while (!getRoad(x, y))
            y--;
        int sx = x;
        int sy = y;

        // Travel
        do {
            int loc = y * width + x;
            if (result.contains(loc))
                throw new IllegalStateException("Circular Error in Travel");
            result.add(loc);
            while (!getRoad(x + dxs[direction], y + dys[direction]))
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
                if (getRoad(x, y)) {
                    builder.append("██");
                } else if (getBrick(x, y)) {
                    builder.append("░░");
                } else {
                    builder.append("  ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
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
            if (getRoad(x, rowNum)) {
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
            } else if (getBrick(x, rowNum)) {
                builder.append("##");
            } else {
                builder.append("  ");
            }
        }
        return builder.toString();
    }

    public static BrickMask buildBrickMask(long seed) {
        int nbBricksHorz = 25;
        int nbBricksVert = 25;
        BrickMask brickMask = new BrickMask(nbBricksHorz, nbBricksVert);
        Random rnd = new Random(seed);
        int base = nbBricksHorz * nbBricksVert / 7 + 1;
        int nbBlocks = base + rnd.nextInt(base);
        int k = 0;
        while (k < nbBlocks || !brickMask.isBorderHit()) {
            int num = brickMask.getNbRoadCandidates();
            if (num == 0)
                break;
            int index = rnd.nextInt(num);
            brickMask.setNthRoadCandidate(index);
            k++;
        }
        return brickMask;
    }

    public static void main(String[] args) {
        BrickMask mask = buildBrickMask(new Random().nextLong());
        int[] travel = mask.toTravel(1);
        System.out.println(mask.toString(travel));
        System.out.println(mask.toString());
    }

}
