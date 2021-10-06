package com.vinhderful.raytracer.utils;

public class Color {

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color GRAY = new Color(122, 122, 122);
    public static final Color DARK_GRAY = new Color(51, 51, 51);

    private final int red;
    private final int green;
    private final int blue;

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color add(Color other) {
        return new Color(Math.min(255, this.red + other.red), Math.min(255, this.green + other.green), Math.min(255, this.blue + other.blue));
    }

    public Color multiply(float f) {
        return new Color(Math.min(255, (int) (this.red * f)), Math.min(255, (int) (this.green * f)), Math.min(255, (int) (this.blue * f)));
    }

    public Color multiply(Color other) {
        return new Color(Math.min(255, this.red * other.red), Math.min(255, this.green * other.green), Math.min(255, this.blue * other.blue));
    }

    public int toARGB() {
        return 0xFF000000 | (red << 16) & 0x00FF0000 | (green << 8) & 0x0000FF00 | blue & 0x000000FF;
    }
}
