package co.selim.hslimage;

class Pixel {
    private final int color;
    private int workingColor;

    Pixel(int color) {
        this.color = color;
        this.workingColor = color;
    }

    public void fromHsl(float[] hsl) {
        this.workingColor = ColorUtils.hslToRgb(hsl);
    }

    public float[] toOriginalHsl() {
        return ColorUtils.rgbToHsl(color);
    }

    float[] toWorkingHsl() {
        return ColorUtils.rgbToHsl(workingColor);
    }

    public int getRgb() {
        return workingColor;
    }
}
