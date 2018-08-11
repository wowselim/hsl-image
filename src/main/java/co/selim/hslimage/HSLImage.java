package co.selim.hslimage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

/**
 * An image that provides easy access for HSL related operations.
 */
public class HSLImage implements Image {
    private static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();
    private static BiConsumer<Pixel, float[]> processingTask = (pixel, hsl) -> {
        float[] originalHsl = pixel.toOriginalHsl();
        float[] workingHsl = pixel.toWorkingHsl();
        workingHsl[0] = Math.max(0, Math.min(1, originalHsl[0] * hsl[0]));
        workingHsl[1] = Math.max(0, Math.min(1, originalHsl[1] * hsl[1]));
        workingHsl[2] = Math.max(0, Math.min(1, originalHsl[2] * hsl[2]));
        pixel.fromHsl(workingHsl);
    };
    private final int width;
    private final int height;
    private final Pixel[][] pixels;
    private final Map<Color, List<Pixel>> colorGroupedPixels = new EnumMap<>(Color.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(PROCESSOR_COUNT, runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("ImageProcessingThread-" + UUID.randomUUID().toString());
        thread.setDaemon(true);
        return thread;
    });

    public HSLImage(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Pixel[width][height];
        for (Color color : Color.values()) {
            colorGroupedPixels.put(color, new ArrayList<>(1_000_000));
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getPixel(int x, int y) {
        return pixels[x][y].getRgb();
    }

    @Override
    public void setPixel(int x, int y, int rgb) {
        Pixel p = new Pixel(rgb);
        pixels[x][y] = p;
        Iterable<Color> colors = ColorUtils.getColorsByHsl(p.toOriginalHsl());
        for (Color c : colors) {
            List<Pixel> pixels = colorGroupedPixels.get(c);
            pixels.add(p);
        }
    }

    /**
     * Updates all HSL components of each pixel that can be mapped to a given color.
     * Updating is done by multiplying the original HSL values by the given values.
     * Consecutive calls to this method will always yields results based on the original values.
     *
     * @param color      the color of the pixels to be modified
     * @param hue        the hue factor
     * @param saturation the saturation factor
     * @param lightness  the lightness factor
     */
    public void setHSL(Color color, float hue, float saturation, float lightness) {
        float[] hsl = new float[]{hue, saturation, lightness};
        List<Pixel> colorGroup = colorGroupedPixels.get(color);
        int chunkSize = colorGroup.size() / PROCESSOR_COUNT;
        List<Future> futures = new ArrayList<>(PROCESSOR_COUNT + 1);
        for (int i = 0; i < colorGroup.size(); i += chunkSize) {
            int end = Math.min(colorGroup.size(), i + chunkSize);
            futures.add(executorService.submit(new PixelProcessor(processingTask,
                    colorGroup.subList(i, end),
                    hsl)));
        }
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException("Error while processing image", e);
            }
        }
    }
}
