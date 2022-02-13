package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Body;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.VectorOps;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.collections.types.VectorInt;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatPI;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatTan;

/**
 * Implements functions to draw the scene on the canvas
 */
public class Renderer {

    public static Float4 getClosestHit(VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodyRadii,
                                       Float4 rayOrigin, Float4 rayDirection) {

        Float4 closestHit = new Float4(-1000F, -1000F, -1000F, -1000F);

        for (int i = 0; i < bodyPositions.getLength(); i++) {
            Float4 intersection = Body.getIntersection(bodyTypes.get(i), bodyPositions.get(i), bodyRadii.get(i), rayOrigin, rayDirection);

            if (intersection.getW() == 0 && (closestHit.getW() == -1000F ||
                    VectorOps.distance(closestHit, rayOrigin) > VectorOps.distance(intersection, rayOrigin)))
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

    public static void render(int[] dimensions, int[] pixels, float[] camera,
                              VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                              Float4 worldBGColor, Float4 lightPosition, float[] lightSize, Float4 lightColor, int[] ssSample) {

        Float4 eyePos = new Float4(0, 0, -1 / floatTan(camera[5] * floatPI() / 360F), 0);
        Float4 camPos = new Float4(camera[0], camera[1], camera[2], 0);

        int width = dimensions[0];
        int height = dimensions[1];

        for (@Parallel int x = 0; x < width; x++)
            for (@Parallel int y = 0; y < height; y++) {

                Float4 normalizedCoords = new Float4(getNormalizedX(dimensions[0], dimensions[1], x), getNormalizedY(dimensions[0], dimensions[1], y), 0, 0);
                Float4 rayDirection = VectorOps.rotate(Float4.normalise(Float4.sub(normalizedCoords, eyePos)), camera[3], camera[4]);

                Float4 hit = getClosestHit(bodyTypes, bodyPositions, bodySizes, camPos, rayDirection);
                int hitIndex = (int) hit.getW();

                if (hitIndex != -1000) {
                    Float4 hitPosition = new Float4(hit.getX(), hit.getY(), hit.getZ(), 0);

                    int bodyType = bodyTypes.get(hitIndex);
                    Float4 bodyPosition = bodyPositions.get(hitIndex);
                    float bodyReflectivity = bodyReflectivities.get(hitIndex);

                    Float4 bodyColor;
                    if (bodyType == 0) bodyColor = Body.getPlaneColor(hitPosition);
                    else bodyColor = bodyColors.get(hitIndex);

                    pixels[x + y * width] = Color.toARGB(Color.add(Color.mult(Color.add(Color.add(
                                                    Shader.getAmbient(bodyColor, lightColor),
                                                    Shader.getDiffuse(bodyType, hitPosition, bodyPosition, bodyColor, lightPosition, lightColor)),
                                            Shader.getSpecular(camPos, bodyType, hitPosition, bodyPosition, bodyReflectivity, lightPosition, lightColor)),
                                    Shader.getShadow(ssSample[0], hitPosition, bodyTypes, bodyPositions, bodySizes, lightPosition, lightSize[0])),
                            Shader.getReflection(hitIndex, hitPosition, rayDirection, bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities, worldBGColor, lightPosition, lightColor)));
                } else
                    pixels[x + y * width] = Color.toARGB(worldBGColor);
            }
    }
}
