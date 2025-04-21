package ch.chaos.castle.utils;


public record Rect(int x, int y, int w, int h) {

    public int ex() {
        return x + w;
    }
    
    public int ey() {
        return y + h;
    }
}
