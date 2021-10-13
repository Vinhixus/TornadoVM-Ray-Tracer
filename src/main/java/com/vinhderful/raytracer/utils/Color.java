package com.vinhderful.raytracer.utils;

public class Color {

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

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color add(Color other) {
        return new Color(Math.min(1F, this.red + other.red), Math.min(1F, this.green + other.green), Math.min(1F, this.blue + other.blue));
    }

    public Color multiply(float f) {
        f = Math.min(1F, f);
        return new Color(this.red * f, this.green * f, this.blue * f);
    }

    public Color multiply(Color other) {
        return new Color(Math.min(1F, this.red * other.red), Math.min(1F, this.green * other.green), Math.min(1F, this.blue * other.blue));
    }

    public int toARGB() {
        int r = (int) (red * 255);
        int g = (int) (green * 255);
        int b = (int) (blue * 255);
        return 0xFF000000 | (r << 16) & 0x00FF0000 | (g << 8) & 0x0000FF00 | b & 0x000000FF;
    }
}
