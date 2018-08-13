package co.selim.hslimage;

import java.util.HashSet;
import java.util.Set;

/**
 * Color and color space related utility class.
 */
public class ColorUtils {
    private static int hueToDegrees(float hue) {
        if (hue < 0 || hue > 1) {
            throw new IllegalStateException("Hue out of bounds: " + hue);
        }
        return (int) (hue * 360);
    }

    static Set<Color> getColorsByHsl(float[] hsl) {
        int degrees = hueToDegrees(hsl[0]);
        float lightness = hsl[2];

        if (lightness > 1 || lightness < 0) {
            throw new IllegalStateException("Lightness out of bounds: " + lightness);
        }

        Set<Color> colors = new HashSet<>(3);

        if (degrees > 340 || degrees < 15) {
            colors.add(Color.RED);
        } else if (degrees > 335 && lightness < 0.5 || degrees < 35 && lightness < 0.5) {
            colors.add(Color.RED);
        }

        if (degrees > 5 && degrees < 45) {
            colors.add(Color.ORANGE);
        }

        if (degrees > 35 && degrees < 95) {
            colors.add(Color.YELLOW);
        } else if (degrees > 25 && degrees < 105 && lightness > 0.5) {
            colors.add(Color.YELLOW);
        }

        if (degrees > 55 && degrees < 165) {
            colors.add(Color.GREEN);
        }

        if (degrees > 95 && degrees < 205) {
            colors.add(Color.AQUA);
        }

        if (degrees > 175 && degrees < 285) {
            colors.add(Color.BLUE);
        } else if (degrees > 285 && degrees < 295 && lightness < 0.5) {
            colors.add(Color.BLUE);
        }

        if (degrees > 215 && degrees < 315) {
            colors.add(Color.PINK);
        }

        if (degrees > 295 && degrees < 345) {
            colors.add(Color.MAGENTA);
        }

        return colors;
    }

    /**
     * Converts a single RGB int into an array of its RGB components.
     *
     * @param rgb the int to convert
     * @return an array of size 3 with the RGB components
     */
    public static int[] toRgbInts(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        return new int[]{red, green, blue};
    }

    private static float[] toRgbFloats(int[] rgbInts) {
        return new float[]{rgbInts[0] / 255f, rgbInts[1] / 255f, rgbInts[2] / 255f};
    }

    static int hslToRgb(float[] hsl) {
        float h = hsl[0], s = hsl[1], l = hsl[2];
        float r, g, b;

        if (s == 0f) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }
        int rI = (int) (r * 255);
        int gI = (int) (g * 255);
        int bI = (int) (b * 255);
        return (rI & 0xFF) << 16 | (gI & 0xFF) << 8 | bI & 0xFF;
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0f)
            t += 1f;
        if (t > 1f)
            t -= 1f;
        if (t < 1f / 6f)
            return p + (q - p) * 6f * t;
        if (t < 1f / 2f)
            return q;
        if (t < 2f / 3f)
            return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    static float[] rgbToHsl(int rgbInt) {
        float[] rgb = toRgbFloats(toRgbInts(rgbInt));
        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        float max = (r > g && r > b) ? r : (g > b) ? g : b;
        float min = (r < g && r < b) ? r : (g < b) ? g : b;

        float h, s, l;
        l = (max + min) / 2f;

        if (max == min) {
            h = s = 0f;
        } else {
            float d = max - min;
            s = (l > 0.5f) ? d / (2f - max - min) : d / (max + min);

            if (r > g && r > b)
                h = (g - b) / d + (g < b ? 6f : 0f);

            else if (g > b)
                h = (b - r) / d + 2f;

            else
                h = (r - g) / d + 4f;

            h /= 6f;
        }

        return new float[]{h, s, l};
    }
}
