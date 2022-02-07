package com.vinhderful.raytracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;

/**
 * Represents a color using red, green and blue values
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

    public static int toARGB(Float4 color) {

        int r = (int) (color.getX() * 255);
        int g = (int) (color.getY() * 255);
        int b = (int) (color.getZ() * 255);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
