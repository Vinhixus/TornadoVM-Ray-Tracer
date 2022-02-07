package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Sphere;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.VectorOps;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatPI;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatTan;

/**
 * Implements functions to draw the scene on the canvas
 */
public class Renderer {

    public static Float4 getClosestHit(VectorFloat4 bodyPositions, VectorFloat bodyRadii,
                                       Float4 rayOrigin, Float4 rayDirection) {

        Float4 closestHit = new Float4(-1F, -1F, -1F, -1F);

        for (int i = 0; i < bodyPositions.getLength(); i++) {

            Float4 intersection = Sphere.getIntersection(bodyPositions.get(i), bodyRadii.get(i), rayOrigin, rayDirection);

            if (intersection.getW() == 0 && (closestHit.getW() == -1F ||
                    VectorOps.distance(bodyPositions.get((int) closestHit.getW()), rayOrigin) > VectorOps.distance(intersection, rayOrigin)))
                closestHit = new Float4(intersection.getX(), intersection.getY(), intersection.getZ(), i);
        }

        return closestHit;
    }

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
                              Float4 cameraPosition, float[] cameraYaw, float[] cameraPitch, float[] cameraFOV,
                              VectorFloat4 bodyPositions, VectorFloat bodyRadii, VectorFloat4 bodyColors,
                              Float4 worldBGColor) {

        Float4 eyePos = new Float4(0, 0, -1 / floatTan(cameraFOV[0] * floatPI() / 360F), 0);

        for (@Parallel int x = 0; x < width; x++)
            for (@Parallel int y = 0; y < height; y++) {

                Float4 normalizedCoords = new Float4(getNormalizedX(width, height, x), getNormalizedY(width, height, y), 0, 0);
                Float4 rayDirection = VectorOps.rotate(Float4.normalise(Float4.sub(normalizedCoords, eyePos)), cameraYaw[0], cameraPitch[0]);

                Float4 hit = getClosestHit(bodyPositions, bodyRadii, cameraPosition, rayDirection);
                int hitIndex = (int) hit.getW();

                if (hitIndex != -1)
                    pixels[x + y * width] = Color.toARGB(bodyColors.get(hitIndex));
                else
                    pixels[x + y * width] = Color.toARGB(worldBGColor);
            }
    }
}
