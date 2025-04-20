package ch.chaos.castle.utils;

import java.util.List;

public record Coord(int x, int y) {

    public static List<Coord> n4() {
        return List.of(new Coord(-1, 0), new Coord(0, 1), new Coord(1, 0), new Coord(0, -1));
    }
    
    public Coord add(Coord delta) {
        return new Coord(x() + delta.x(), y() + delta.y());
    }
    
    public Coord add(int dx, int dy) {
        return new Coord(x() + dx, y() + dy);
    }
}
