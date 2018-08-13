package co.selim.hslimage;

import java.util.List;
import java.util.function.BiConsumer;

class PixelProcessor implements Runnable {
    private final BiConsumer<Pixel, float[]> task;
    private final List<Pixel> pixels;
    private final float[] hsl;

    PixelProcessor(BiConsumer<Pixel, float[]> task,
                   List<Pixel> pixels,
                   float[] hsl) {
        this.task = task;
        this.hsl = hsl;
        this.pixels = pixels;
    }

    @Override
    public void run() {
        pixels.forEach(pixel -> task.accept(pixel, hsl));
    }
}
