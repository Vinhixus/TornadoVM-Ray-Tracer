package com.vinhderful.raytracer.utils;

public class Color {

    public static final Color BLACK = new Color(0F, 0F, 0F);
    public static final Color WHITE = new Color(1F, 1F, 1F);
    public static final Color RED = new Color(1F, 0F, 0F);
    public static final Color GREEN = new Color(0F, 1F, 0F);
    public static final Color BLUE = new Color(0F, 0F, 1F);

    private final float red;
    private final float green;
    private final float blue;

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public Color add(Color other) {
        return new Color(Math.min(1F, this.red + other.red), Math.min(1F, this.green + other.green), Math.min(1F, this.blue + other.blue));
    }

    public Color multiply(float f) {
        return new Color(Math.min(1F, this.red * f), Math.min(1F, this.green * f), Math.min(1F, this.blue * f));
    }

    public Color multiply(Color other) {
        return new Color(Math.min(1F, this.red * other.red), Math.min(1F, this.green * other.green), Math.min(1F, this.blue * other.blue));
    }

    public javafx.scene.paint.Color toPaint() {
        return new javafx.scene.paint.Color(red, green, blue, 1.0);
    }
}
