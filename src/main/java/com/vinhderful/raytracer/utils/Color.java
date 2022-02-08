package com.vinhderful.raytracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.min;

/**
 * Color definitions and operations
 */
public class Color {

    /**
     * Default colors
     */
    public static final Float4 BLACK = new Float4(0, 0, 0, 0);
    public static final Float4 WHITE = new Float4(1F, 1F, 1F, 0);
    public static final Float4 RED = new Float4(1F, 0, 0, 0);
    public static final Float4 GREEN = new Float4(0, 1F, 0, 0);
    public static final Float4 BLUE = new Float4(0, 0, 1F, 0);
    public static final Float4 GRAY = new Float4(0.5F, 0.5F, 0.5F, 0);
    public static final Float4 DARK_GRAY = new Float4(0.2F, 0.2F, 0.2F, 0);


    public static Float4 add(Float4 a, Float4 b) {
        return new Float4(
                min(1F, a.getX() + b.getX()),
                min(1F, a.getY() + b.getY()),
                min(1F, a.getZ() + b.getZ()),
                min(1F, a.getW() + b.getW()));
    }

    public static Float4 mult(Float4 a, Float4 b) {
        return new Float4(
                min(1F, a.getX() * b.getX()),
                min(1F, a.getY() * b.getY()),
                min(1F, a.getZ() * b.getZ()),
                min(1F, a.getW() * b.getW()));
    }

    public static Float4 mult(Float4 a, float b) {
        b = min(1F, b);
        return new Float4(a.getX() * b, a.getY() * b, a.getZ() * b, a.getW() * b);
    }

    public static int toARGB(Float4 color) {

        int r = (int) (color.getX() * 255);
        int g = (int) (color.getY() * 255);
        int b = (int) (color.getZ() * 255);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
