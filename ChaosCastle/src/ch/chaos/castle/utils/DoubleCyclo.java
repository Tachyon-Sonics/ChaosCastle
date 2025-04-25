package ch.chaos.castle.utils;

import java.util.Random;
import java.util.function.BiPredicate;

public class DoubleCyclo extends BinaryLevelBuilderBase {
    
    private final int size;
    private final int minTrackWidth;
    private final int maxTrackWidth;
    private final BiPredicate<Integer, Integer> acceptPeriods;
    private final Coord center;
    

    public DoubleCyclo(int size, int minTrackWidth, int maxTrackWidth, BiPredicate<Integer, Integer> acceptPeriods) {
        super(size, size);
        this.size = size;
        this.minTrackWidth = minTrackWidth;
        this.maxTrackWidth = maxTrackWidth;
        this.acceptPeriods = acceptPeriods;
        this.center = new Coord(size / 2, size / 2);
    }
    
    private double rad(double degree) {
        return degree / 360.0 * Math.PI * 2.0;
    }
    
    public void build() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setWall(x, y, true);
            }
        }
        
        Random rnd = new Random();
        int period1, period2;
        do {
            period1 = 1 + rnd.nextInt(8); // 1 .. 8
            period2 = 1 + rnd.nextInt(8); // 1 .. 8
        } while (!acceptPeriods.test(period1, period2));
        int phase1 = rnd.nextInt(360);
        int phase2 = rnd.nextInt(360);
        System.out.println("Periods: " + period1 + ", " + period2);
        
        int prevSize = (minTrackWidth + maxTrackWidth) / 2;
        int nextSize = (minTrackWidth + maxTrackWidth) / 2;
        int sizeCount = 0;
        int sizeIndex = 0;

        for (int angle = 0; angle < 360; angle++) {
            if (sizeIndex >= sizeCount) {
                prevSize = nextSize;
                nextSize = minTrackWidth + (minTrackWidth == maxTrackWidth ? 0 : rnd.nextInt(maxTrackWidth - minTrackWidth));
                sizeCount = 2 + rnd.nextInt(6);
                sizeIndex = 0;
            }
            int curSize = (prevSize * (sizeCount - sizeIndex) + nextSize * sizeIndex) / sizeCount;
            sizeIndex++;

            double radius = Math.sin(rad(angle * period1 + phase1)) + Math.sin(rad(angle * period2 + phase2)); // -2..2
            radius += 6.0; // 4..8
            radius /= 2.0; // 2..4
            radius = radius * ((size - 4) / 2) / 4.0; // size / 4 .. size / 8
            int x = (int) (Math.cos(rad(angle)) * radius);
            int y = (int) (Math.sin(rad(angle)) * radius);
            Coord coord = center.add(x, y);
            int sizeH = curSize / 2;
            fillOval(coord.x() - sizeH, coord.y() - sizeH, curSize, curSize, false);
//            setWall(center.add(x, y), false);
        }
    }
    
    static void snowTracks() {
        // Double cycloide
        DoubleCyclo dc = new DoubleCyclo(84, 2, 6, (period1, period2) -> {
            return (period1 != period2 && period1 + period2 > 7 && period1 > 1 && period2 > 1);
        });
        dc.build();
        
        // Simple brick mask
        SimpleBrickMask mask = new SimpleBrickMask(30, 30);
        mask.build(0.4);
        mask.fillInterior(3);
        
        // Add brick mask to interior of double cycloid
        for (int x = 0; x < mask.getWidth(); x++) {
            for (int y = 0; y < mask.getHeight(); y++) {
                if (!mask.isWall(x, y)) {
                    int px = (dc.getWidth() - mask.getWidth()) / 2 + x;
                    int py = (dc.getHeight() - mask.getHeight()) / 2 + y;
                    dc.setWall(px, py, false);
                }
            }
        }
        dc.removeDiagonals();
        
        // Put road from one to the other
        int cx = dc.getWidth() / 2;
        int y = 0;
        while (dc.isWall(cx, y)) {
            y++;
        }
        while (!dc.isWall(cx, y)) {
            y++; // In cycloid
        }
        while (dc.isWall(cx, y)) {
            dc.setWall(cx, y, false);
            y++;
        }
        
        // Initial wall
        cx++;
        y = 0;
        while (dc.isWall(cx, y)) {
            y++;
        }
        while (!dc.isWall(cx, y)) {
            dc.setWall(cx, y, true);
            y++;
        }
        
        // Start
        cx++;
        y = 0;
        while (dc.isWall(cx, y)) {
            y++;
        }
        Coord start = new Coord(cx, y);
        
        // Exit
        cx = (dc.getWidth() + 1) / 2;
        y = dc.getHeight() - 1;
        while (dc.isWall(cx, y)) {
            y--;
        }
        while (!dc.isWall(cx, y)) {
            y--; // In cycloid
        }
        while (dc.isWall(cx, y)) {
            y--;
        }
        Coord exit = new Coord(cx, y);
        
        System.out.println(dc.toString());
        System.out.println("Start: " + start.toShortString() + "; Exit: " + exit.toShortString());
    }
    
    static void blob() {
        // Double cycloide
        DoubleCyclo dc = new DoubleCyclo(20, 1, 1, (period1, period2) -> true);
        dc.build();
        dc.fillFlood(new Coord(dc.getWidth() / 2, dc.getHeight() / 2), false);
        System.out.println(dc.toString());
    }

    public static void main(String[] args) {
        snowTracks();
//        blob();
    }

}
