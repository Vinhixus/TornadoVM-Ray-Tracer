package com.vinhderful.raytracer.utils;

/**
 * Represents a color using red, green and blue values
 */
public class Color {

    /**
     * Default colors
     */
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(1F, 1F, 1F);
    public static final Color RED = new Color(1F, 0, 0);
    public static final Color GREEN = new Color(0, 1F, 0);
    public static final Color BLUE = new Color(0, 0, 1F);
    public static final Color GRAY = new Color(0.5F, 0.5F, 0.5F);
    public static final Color DARK_GRAY = new Color(0.2F, 0.2F, 0.2F);

    private final float red;
    private final float green;
    private final float blue;

    /**
     * Construct a Color object using red, green and blue values
     * 
     * @param red
     *            red value
     * @param green
     *            green value
     * @param blue
     *            blue value
     */
    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Add this color to another given color
     * 
     * @param other
     *            the other color
     * @return the resulting color
     */
    public Color add(Color other) {
        return new Color(Math.min(1F, this.red + other.red), Math.min(1F, this.green + other.green), Math.min(1F, this.blue + other.blue));
    }

    /**
     * Multiply this color with a given scalar
     *
     * @param scalar
     *            the scalar
     * @return the resulting color
     */
    public Color multiply(float scalar) {
        scalar = Math.min(1F, scalar);
        return new Color(this.red * scalar, this.green * scalar, this.blue * scalar);
    }

    /**
     * Multiply this color with another given color
     *
     * @param other
     *            the other color
     * @return the resulting color
     */
    public Color multiply(Color other) {
        return new Color(Math.min(1F, this.red * other.red), Math.min(1F, this.green * other.green), Math.min(1F, this.blue * other.blue));
    }

    /**
     * Return the color as an ARGB int value
     * 
     * @return the color as an ARGB int value
     */
    public int toARGB() {
        int r = (int) (red * 255);
        int g = (int) (green * 255);
        int b = (int) (blue * 255);
        return 0xFF000000 | (r << 16) & 0x00FF0000 | (g << 8) & 0x0000FF00 | b & 0x000000FF;
    }
}
