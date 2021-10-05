package com.vinhderful.raytracer.utils;

public class Color {

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color MAGENTA = new Color(255, 0, 255);

    private final int red;
    private final int green;
    private final int blue;

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public Color add(Color other) {
        return new Color(Math.min(255, this.red + other.red), Math.min(255, this.green + other.green), Math.min(255, this.blue + other.blue));
    }

    public Color multiply(float f) {
        f = Math.min(1F, f);
        return new Color(Math.min(255, (int) (this.red * f)), Math.min(255, (int) (this.green * f)), Math.min(255, (int) (this.blue * f)));
    }

    public Color multiply(Color other) {
        return new Color(Math.min(255, this.red * other.red), Math.min(255, this.green * other.green), Math.min(255, this.blue * other.blue));
    }

    public int toARGB() {
        String color = "ff";

        String r = red < 10 ? "0" + Integer.toHexString(red) : Integer.toHexString(red);
        String g = green < 10 ? "0" + Integer.toHexString(green) : Integer.toHexString(green);
        String b = blue < 10 ? "0" + Integer.toHexString(blue) : Integer.toHexString(blue);

        color += r + g + b;
        return (int) Long.parseLong(color, 16);
    }
}
