package co.selim.hslimage;

/**
 * An image with a certain width and height as well as methods
 * to get and set pixel values in RGB.
 */
public interface Image {
    /**
     * The width of this image.
     *
     * @return the width of this image
     */
    int getWidth();

    /**
     * The height of this image.
     *
     * @return the height of this image
     */
    int getHeight();

    /**
     * Returns the RGB value of the pixel at the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the RGB value of the pixel at ({@code x}, {@code y})
     */
    int getPixel(int x, int y);

    /**
     * Sets the RGB value of the pixel at the specified coordinates.
     *
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @param rgb the RGB value for the pixel at ({@code x}, {@code y})
     */
    void setPixel(int x, int y, int rgb);
}
