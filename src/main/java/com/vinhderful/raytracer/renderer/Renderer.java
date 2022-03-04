package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.utils.BodyOps;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Float4Ext;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.collections.types.VectorInt;

import static com.vinhderful.raytracer.utils.Angle.TO_RADIANS;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatTan;

/**
 * Implements functions to draw the scene on the canvas
 */
public class Renderer {

    public static boolean intersects(VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                     Float4 rayOrigin, Float4 rayDirection) {

        boolean intersects = false;

        for (int i = 2; i < bodyPositions.getLength(); i++)
            if (BodyOps.getIntersection(bodyTypes.get(i), bodyPositions.get(i), bodySizes.get(i), rayOrigin, rayDirection).getW() == 0)
                intersects = true;

        return intersects;
    }

    public static Float4 getClosestHit(VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                       Float4 rayOrigin, Float4 rayDirection) {

        Float4 closestHit = new Float4(-1000F, -1000F, -1000F, -1000F);

        for (int i = 0; i < bodyPositions.getLength(); i++) {
            Float4 intersection = BodyOps.getIntersection(bodyTypes.get(i), bodyPositions.get(i), bodySizes.get(i), rayOrigin, rayDirection);

            if (intersection.getW() == 0 && (closestHit.getW() == -1000F ||
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

    public static void render(int[] pixels, int[] dimensions, float[] camera,
                              VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                              Float4 worldBGColor, int[] pathTracingProperties) {

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

                Float4 hit = getClosestHit(bodyTypes, bodyPositions, bodySizes, camPos, rayDirection);
                int hitIndex = (int) hit.getW();

                if (hitIndex != -1000) {

                    if (hitIndex == 0) pixels[x + y * width] = Color.toARGB(bodyColors.get(0));
                    else {
                        Float4 lightPosition = bodyPositions.get(0);
                        Float4 lightColor = bodyColors.get(0);
                        float lightSize = bodySizes.get(0);

                        Float4 hitPosition = new Float4(hit.getX(), hit.getY(), hit.getZ(), 0);

                        int bodyType = bodyTypes.get(hitIndex);
                        Float4 bodyPosition = bodyPositions.get(hitIndex);
                        float bodyReflectivity = bodyReflectivities.get(hitIndex);

                        Float4 bodyColor = BodyOps.getColor(hitIndex, hitPosition, bodyColors);
                        bodyColor = Float4.add(bodyColor, Shader.getReflection(reflectionBounceLimit, hitIndex, hitPosition, rayDirection, bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities, lightPosition, lightSize, lightColor, shadowSampleSize));

                        pixels[x + y * width] = Color.toARGB(Color.mult(
                                Shader.getPhong(camPos, hitPosition, bodyType, bodyPosition, bodyColor, bodyReflectivity, lightPosition, lightColor),
                                Shader.getShadow(hitPosition, bodyTypes, bodyPositions, bodySizes, lightPosition, lightSize, shadowSampleSize)));
                    }

                } else
                    pixels[x + y * width] = Color.toARGB(worldBGColor);
            }
    }
}
