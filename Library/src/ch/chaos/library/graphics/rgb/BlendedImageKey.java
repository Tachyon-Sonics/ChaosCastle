package ch.chaos.library.graphics.rgb;

public record BlendedImageKey(int sx, int sy, int width, int height,
        BufferArea srcArea, BufferArea maskArea) {

}
