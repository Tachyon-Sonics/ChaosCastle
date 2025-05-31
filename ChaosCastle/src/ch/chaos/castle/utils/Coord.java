package ch.chaos.castle.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

public record Coord(int x, int y) {
    
    private final static List<Coord> N4 = List.of(new Coord(-1, 0), new Coord(0, 1), new Coord(1, 0), new Coord(0, -1));
    private final static List<Coord> N8 = Collections.unmodifiableList(buildN8());
    private final static List<Coord> N9 = Collections.unmodifiableList(buildN9());
    private final static List<Coord> CW_N8 = List.of(
            new Coord(0, -1),
            new Coord(1, -1),
            new Coord(1, 0),
            new Coord(1, 1),
            new Coord(0, 1),
            new Coord(-1, 1),
            new Coord(-1, 0),
            new Coord(-1, -1)
            );

    
    /**
     * @return the 4 delta coordinates (-1, 0), (0, 1), (1, 0) and (0, -1), that can be used
     * to get the 4-neighbours of a given coordinate, using {@link #add(Coord)}.
     */
    public static List<Coord> n4() {
        return N4;
    }
    
    /**
     * @return the 8 delta coordinates, that can be used to get the 8-neighbours of a given
     * coordinate, using {@link #add(Coord)}.
     */
    public static List<Coord> n8() {
        return N8;
    }
    
    public static List<Coord> n9() {
        return N9;
    }
    
    /**
     * @return same as {@link #n8()}, but is clockwise order
     */
    public static List<Coord> clockwiseN8() {
        return CW_N8;
    }
    
    private static List<Coord> buildN8() {
        List<Coord> result = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    result.add(new Coord(dx, dy));
                }
            }
        }
        return result;
    }
    
    private static List<Coord> buildN9() {
        List<Coord> result = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                result.add(new Coord(dx, dy));
            }
        }
        return result;
    }
    
    public Coord add(Coord delta) {
        return new Coord(x() + delta.x(), y() + delta.y());
    }
    
    public Coord add(int dx, int dy) {
        return new Coord(x() + dx, y() + dy);
    }
    
    /**
     * @return -this
     */
    public Coord neg() {
        return new Coord(-x(), -y());
    }
    
    public String toShortString() {
        return "(" + x + "," + y + ")";
    }
    
    public static String toShortString(Collection<Coord> coords) {
        StringJoiner result = new StringJoiner(", ");
        for (Coord coord : coords) {
            result.add(coord.toShortString());
        }
        return result.toString();
    }
    
    /**
     * Predicate for coordinates that are at least at the given euclidean distance from this coordinate
     */
    public Predicate<Coord> atAtLeast(double minDistance) {
        return (Coord coord) -> {
            int dx = coord.x() - this.x();
            int dy = coord.y() - this.y();
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance >= minDistance;
        };
    }
    
}
