package com.vinhderful.pathtracer.renderer;

import com.vinhderful.pathtracer.utils.BodyOps;
import com.vinhderful.pathtracer.utils.Color;
import com.vinhderful.pathtracer.utils.Float4Ext;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import static com.vinhderful.pathtracer.misc.World.LIGHT_INDEX;
import static com.vinhderful.pathtracer.utils.Angle.TO_RADIANS;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatTan;

/**
 * Implements functions to draw the scene on the canvas
 */
public class Renderer {

    public static Float4 getClosestHit(VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                       Float4 rayOrigin, Float4 rayDirection) {

        Float4 closestHit = new Float4(-1F, -1F, -1F, -1F);

        for (int i = 0; i < bodyPositions.getLength(); i++) {
            Float4 intersection = BodyOps.getIntersection(i, bodyPositions.get(i), bodySizes.get(i), rayOrigin, rayDirection);

            if (intersection.getW() == 0 && (closestHit.getW() == -1F ||
                    Float4Ext.distance(closestHit, rayOrigin) > Float4Ext.distance(intersection, rayOrigin)))
                closestHit = new Float4(intersection.getX(), intersection.getY(), intersection.getZ(), i);
        }

        return closestHit;
    }

    public static float getNormalizedX(int width, int height, int x) {
        if (width > height)
            return (x - width * 0.5F + height * 0.5F) / height * 2 - 1;
        else
            return x * 2F / width - 1;
    }

    public static float getNormalizedY(int width, int height, int y) {
        if (width > height)
            return -(y * 2F / height - 1);
        else
            return -((y - height * 0.5F + width * 0.5F) / width * 2 - 1);
    }

    public static void render(int[] pixels, int[] dimensions, float[] camera, int[] pathTracingProperties,
                              VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                              VectorFloat4 skybox, int[] skyboxDimensions) {

        Float4 eyePos = new Float4(0, 0, -1 / floatTan(camera[5] * 0.5F * TO_RADIANS), 0);
        Float4 camPos = new Float4(camera[0], camera[1], camera[2], 0);

        int width = dimensions[0];
        int height = dimensions[1];

        int shadowSampleSize = pathTracingProperties[0];
        int reflectionBounceLimit = pathTracingProperties[1];

        for (@Parallel int x = 0; x < width; x++)
            for (@Parallel int y = 0; y < height; y++) {

                Float4 normalizedCoords = new Float4(getNormalizedX(width, height, x), getNormalizedY(width, height, y), 0, 0);
                Float4 rayDirection = Float4Ext.rotate(Float4.normalise(Float4.sub(normalizedCoords, eyePos)), camera[3], camera[4]);

                Float4 hit = getClosestHit(bodyPositions, bodySizes, camPos, rayDirection);
                int hitIndex = (int) hit.getW();

                if (hitIndex != -1) {
                    if (hitIndex == LIGHT_INDEX)
                        pixels[x + y * width] = Color.toARGB(bodyColors.get(LIGHT_INDEX));
                    else {
                        Float4 hitPosition = new Float4(hit.getX(), hit.getY(), hit.getZ(), 0);
                        Float4 pixelColor = Shader.getPixelColor(
                                hitIndex, hitPosition, camPos, rayDirection,
                                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                                skybox, skyboxDimensions,
                                shadowSampleSize, reflectionBounceLimit);

                        pixels[x + y * width] = Color.toARGB(pixelColor);
                    }
                } else {
                    // pixels[x + y * width] = Color.toARGB(BodyOps.getSkyboxColor(skybox, skyboxDimensions, rayDirection));
                    pixels[x + y * width] = 0xFF000000;
                }
            }
    }
}
