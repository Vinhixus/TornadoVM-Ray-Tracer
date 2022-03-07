package com.vinhderful.raytracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.max;
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
                min(1F, max(0F, a.getX() + b.getX())),
                min(1F, max(0F, a.getY() + b.getY())),
                min(1F, max(0F, a.getZ() + b.getZ())),
                min(1F, max(0F, a.getW() + b.getW())));
    }

    public static Float4 add(Float4 a, float b) {
        return new Float4(
                min(1F, max(0F, a.getX() + b)),
                min(1F, max(0F, a.getY() + b)),
                min(1F, max(0F, a.getZ() + b)),
                min(1F, max(0F, a.getW() + b)));
    }

    public static Float4 mult(Float4 a, Float4 b) {
        return new Float4(
                min(1F, max(0F, a.getX() * b.getX())),
                min(1F, max(0F, a.getY() * b.getY())),
                min(1F, max(0F, a.getZ() * b.getZ())),
                min(1F, max(0F, a.getW() * b.getW())));
    }

    public static Float4 mult(Float4 a, float b) {
        b = min(1F, max(0F, b));
        return new Float4(a.getX() * b, a.getY() * b, a.getZ() * b, a.getW() * b);
    }

    public static Float4 mix(Float4 a, Float4 b, float t) {
        return new Float4(
                t * b.getX() + (1F - t) * a.getX(),
                t * b.getY() + (1F - t) * a.getY(),
                t * b.getZ() + (1F - t) * a.getZ(),
                0
        );
    }

    public static Float4 fromInt(int argb) {
        int b = (argb) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        return new Float4(r / 255F, g / 255F, b / 255F, 0);
    }

    public static int toARGB(Float4 c) {
        int r = (int) (c.getX() * 255);
        int g = (int) (c.getY() * 255);
        int b = (int) (c.getZ() * 255);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
