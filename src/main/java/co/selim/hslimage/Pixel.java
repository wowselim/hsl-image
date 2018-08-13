package co.selim.hslimage;

class Pixel {
    private final int color;
    private int workingColor;

    Pixel(int color) {
        this.color = color;
        this.workingColor = color;
    }

    void fromHsl(float[] hsl) {
        this.workingColor = ColorUtils.hslToRgb(hsl);
    }

    float[] toOriginalHsl() {
        return ColorUtils.rgbToHsl(color);
    }

    float[] toWorkingHsl() {
        return ColorUtils.rgbToHsl(workingColor);
    }

    int getRgb() {
        return workingColor;
    }
}
