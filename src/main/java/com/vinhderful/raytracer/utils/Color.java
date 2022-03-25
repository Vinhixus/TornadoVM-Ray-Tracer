/*
 * This file is part of Tornado-Ray-Tracer: A Java-based ray tracer running on TornadoVM.
 * URL: https://github.com/Vinhixus/TornadoVM-Ray-Tracer
 *
 * Copyright (c) 2021-2022, Vinh Pham Van
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    public static final Color MAGENTA = new Color(1F, 0, 1F);

    private final float red;
    private final float green;
    private final float blue;

    /**
     * Construct a Color object using red, green and blue values
     *
     * @param red   red value
     * @param green green value
     * @param blue  blue value
     */
    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Get a Color object from an ARGB value
     *
     * @return the Color object
     */
    public static Color fromInt(int argb) {
        int b = (argb) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        return new Color(r / 255F, g / 255F, b / 255F);
    }

    /**
     * Return the red value of the color
     *
     * @return the red value
     */
    public float getRed() {
        return red;
    }

    /**
     * Return the green value of the color
     *
     * @return the green value
     */
    public float getGreen() {
        return green;
    }

    /**
     * Return the blue value of the color
     *
     * @return the blue value
     */
    public float getBlue() {
        return blue;
    }

    /**
     * Add this color to a given scalar
     *
     * @param scalar the scalar
     * @return the resulting color
     */
    public Color add(float scalar) {
        return new Color(Math.min(1F, this.red + scalar), Math.min(1F, this.green + scalar), Math.min(1F, this.blue + scalar));
    }

    /**
     * Add this color to another given color
     *
     * @param other the other color
     * @return the resulting color
     */
    public Color add(Color other) {
        return new Color(Math.min(1F, this.red + other.red), Math.min(1F, this.green + other.green), Math.min(1F, this.blue + other.blue));
    }

    /**
     * Multiply this color with a given scalar
     *
     * @param scalar the scalar
     * @return the resulting color
     */
    public Color multiply(float scalar) {
        scalar = Math.min(1F, scalar);
        return new Color(this.red * scalar, this.green * scalar, this.blue * scalar);
    }

    /**
     * Multiply this color with another given color
     *
     * @param other the other color
     * @return the resulting color
     */
    public Color multiply(Color other) {
        return new Color(Math.min(1F, this.red * other.red), Math.min(1F, this.green * other.green), Math.min(1F, this.blue * other.blue));
    }

    /**
     * Mix this color with another color given a ratio t
     *
     * @param other the other color
     * @param r     the ratio
     * @return result
     */
    public Color mix(Color other, float r) {
        return new Color(
                r * other.getRed() + (1F - r) * red,
                r * other.getGreen() + (1F - r) * green,
                r * other.getBlue() + (1F - r) * blue);
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
