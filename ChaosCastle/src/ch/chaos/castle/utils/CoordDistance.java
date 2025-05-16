package ch.chaos.castle.utils;


public record CoordDistance(Coord coord, double distance) implements Comparable<CoordDistance> {

    @Override
    public int compareTo(CoordDistance other) {
        return Double.compare(this.distance, other.distance);
    }

}
