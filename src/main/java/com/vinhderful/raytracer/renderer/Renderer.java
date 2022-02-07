package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Sphere;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

/**
 * Implements functions to draw the scene on the canvas
 */
public class Renderer {

    public static float getNormalizedX(int width, int height, int x) {
        if (width > height)
            return (float) (x - width / 2 + height / 2) / height * 2 - 1;
        else
            return (float) x / width * 2 - 1;
    }

    public static float getNormalizedY(int width, int height, int y) {
        if (width > height)
            return -((float) y / height * 2 - 1);
        else
            return -((float) (y - height / 2 + width / 2) / width * 2 - 1);
    }

    public static void render(int width, int height, int[] pixels,
                              Float4 worldBGColor, VectorFloat4 bodyPositions, VectorFloat bodyRadii, VectorFloat4 bodyColors) {

        for (@Parallel int x = 0; x < width; x++)
            for (@Parallel int y = 0; y < height; y++) {

                pixels[x + y * width] = Color.toARGB(worldBGColor);

                Float4 rayOrigin = new Float4(getNormalizedX(width, height, x), getNormalizedY(width, height, y), 0, 0);
                Float4 rayDirection = new Float4(0, 0, 1, 0);

                for (int i = 0; i < bodyPositions.getLength(); i++) {
                    Float4 intersection = Sphere.getIntersection(bodyPositions.get(i), bodyRadii.get(i), rayOrigin, rayDirection);

                    if (intersection.getW() == 0)
                        pixels[x + y * width] = Color.toARGB(bodyColors.get(i));
                }
            }
    }
}
