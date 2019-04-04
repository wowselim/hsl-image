package co.selim.hslimage

import co.selim.hslimage.ColorUtils.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import java.util.*

class HSLImage(val width: Int, val height: Int) {
    private val pixels = LongArray(width * height)
    private val pixelsByColor = EnumMap<Color, IntArrayList>(Color::class.java)

    init {
        Color.values().forEach { color ->
            pixelsByColor[color] = IntArrayList()
        }
    }

    fun setPixel(x: Int, y: Int, rgb: Int) {
        val index = getIndex(x, y)
        pixels[index] = rgb.toLong().shl(32).or(rgb.toLong().and(0xffffffffL))
        val matchingColors = getColorsByHsl(rgbToHsl(rgb))
        matchingColors
                .map(pixelsByColor::getValue)
                .forEach { colorGroup ->
                    colorGroup += getIndex(x, y)
                }
    }

    fun getPixel(x: Int, y: Int) = getWorkingInt(getIndex(x, y))

    private fun getOriginalInt(index: Int): Int {
        return pixels[index].shr(32).toInt()
    }

    private fun getWorkingInt(index: Int): Int {
        return pixels[index].toInt()
    }

    private fun setWorkingIntFromHSL(index: Int, hsl: FloatArray) {
        val value = hslToRgb(hsl)
        pixels[index] = getOriginalInt(index).toLong().shl(32).or(value.toLong().and(0xffffffffL))
    }

    private fun getIndex(x: Int, y: Int) = x + y * width

    fun setHSL(color: Color, hue: Float, saturation: Float, lightness: Float) {
        val pixelsToModify = pixelsByColor.getValue(color)
        pixelsToModify
                .parallelStream()
                .forEach { index ->
                    val originalHsl = rgbToHsl(getOriginalInt(index))
                    val workingHsl = rgbToHsl(getWorkingInt(index))

                    workingHsl[0] = Math.max(0f, Math.min(1f, originalHsl[0] * hue))
                    workingHsl[1] = Math.max(0f, Math.min(1f, originalHsl[1] * saturation))
                    workingHsl[2] = Math.max(0f, Math.min(1f, originalHsl[2] * lightness))
                    setWorkingIntFromHSL(index, workingHsl)
                }
    }
}