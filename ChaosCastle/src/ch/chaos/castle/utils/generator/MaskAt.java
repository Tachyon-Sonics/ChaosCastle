package ch.chaos.castle.utils.generator;

import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.Rect;

public record MaskAt(BinaryLevel mask, Coord where) {
    
    public Rect rect() {
        return new Rect(where.x(), where.y(), mask.getWidth(), mask.getHeight());
    }

}
