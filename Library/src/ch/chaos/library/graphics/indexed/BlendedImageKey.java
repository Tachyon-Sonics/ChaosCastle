package ch.chaos.library.graphics.indexed;

public record BlendedImageKey(int sx, int sy, int width, int height,
        MemoryArea srcArea, MemoryArea maskArea) {

}
