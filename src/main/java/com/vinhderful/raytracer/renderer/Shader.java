package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.utils.BodyOps;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Float4Ext;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import static com.vinhderful.raytracer.misc.World.LIGHT_INDEX;
import static com.vinhderful.raytracer.misc.World.PLANE_INDEX;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

public class Shader {

    public static final float AMBIENT_STRENGTH = 0.05F;
    public static final float SPECULAR_STRENGTH = 0.5F;
    public static final float MAX_REFLECTIVITY = 96F;
    public static final float SHADOW_STRENGTH = 1.1F;

    public static final float PHI = floatPI() * (3 - floatSqrt(5));

    public static Float4 getPhong(int hitIndex, Float4 hitPosition, Float4 rayDirection,
                                  Float4 bodyPosition, Float4 bodyColor, float bodyReflectivity,
                                  Float4 lightPosition) {

        float diffuse = max(AMBIENT_STRENGTH, getDiffuse(hitIndex, hitPosition, bodyPosition, lightPosition));
        float specular = getSpecular(hitIndex, hitPosition, rayDirection, bodyPosition, bodyReflectivity, lightPosition);
        return Color.add(Color.mult(bodyColor, diffuse), specular);
    }

    public static float getDiffuse(int hitIndex, Float4 hitPosition, Float4 bodyPosition, Float4 lightPosition) {
        Float4 hitNormal = BodyOps.getNormal(hitIndex, hitPosition, bodyPosition);
        Float4 lightDirection = Float4.normalise(Float4.sub(lightPosition, hitPosition));
        return max(0, Float4.dot(hitNormal, lightDirection));
    }

    public static float getSpecular(int hitIndex, Float4 hitPosition, Float4 rayDirection,
                                    Float4 bodyPosition, float bodyReflectivity,
                                    Float4 lightPosition) {

        Float4 lightDirection = Float4.normalise(Float4.sub(lightPosition, hitPosition));
        Float4 hitNormal = BodyOps.getNormal(hitIndex, hitPosition, bodyPosition);

        Float4 reflectionVector = Float4.sub(lightDirection, Float4.mult(hitNormal, 2 * Float4.dot(lightDirection, hitNormal)));
        float specularFactor = max(0, Float4.dot(reflectionVector, rayDirection));
        float specularBrightness = pow(specularFactor, bodyReflectivity);

        return specularBrightness * SPECULAR_STRENGTH;
    }

    public static float getShadow(Float4 hitPosition, VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                  Float4 lightPosition, float lightSize, int sampleSize) {

        Float4 n = Float4.normalise(Float4.sub(hitPosition, lightPosition));
        Float4 u = Float4Ext.perpVector(n);
        Float4 v = Float4Ext.cross(u, n);

        int raysHit = 0;

        for (int i = 0; i < sampleSize; i++) {

            float t = PHI * i;
            float r = floatSqrt((float) i / sampleSize);

            float x = 2 * lightSize * r * floatCos(t);
            float y = 2 * lightSize * r * floatSin(t);

            Float4 samplePoint = Float4.add(Float4.add(lightPosition, Float4.mult(u, x)), Float4.mult(v, y));
            Float4 rayDir = Float4.normalise(Float4.sub(samplePoint, hitPosition));
            Float4 rayOrigin = Float4.add(hitPosition, Float4.mult(rayDir, 0.001F));

            if (Renderer.getClosestHit(bodyPositions, bodySizes, rayOrigin, rayDir).getW() > PLANE_INDEX)
                raysHit++;
        }

        if (raysHit == 0) return 1;
        else return 1 - (float) raysHit / (sampleSize * SHADOW_STRENGTH);
    }

    public static Float4 getReflection(int hitIndex, Float4 hitPosition, Float4 rayDirection,
                                       VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                                       Float4 lightPosition, float lightSize,
                                       int shadowSampleSize, int reflectionBounceLimit) {

        Float4 reflectionColor = new Float4(0, 0, 0, 0);
        float reflectivity = 1F;

        for (int i = 0; i < reflectionBounceLimit; i++) {

            Float4 hitNormal = BodyOps.getNormal(hitIndex, hitPosition, bodyPositions.get(hitIndex));
            Float4 reflectionDir = Float4.sub(rayDirection, Float4.mult(hitNormal, 2 * Float4.dot(rayDirection, hitNormal)));
            Float4 reflectionOrigin = Float4.add(hitPosition, Float4.mult(reflectionDir, 0.001F));

            float t = bodyReflectivities.get(hitIndex) / MAX_REFLECTIVITY;

            Float4 hit = Renderer.getClosestHit(bodyPositions, bodySizes, reflectionOrigin, reflectionDir);
            hitIndex = (int) hit.getW();

            if (hitIndex != -1) {
                hitPosition = new Float4(hit.getX(), hit.getY(), hit.getZ(), 0);

                Float4 bodyPosition = bodyPositions.get(hitIndex);
                float bodyReflectivity = bodyReflectivities.get(hitIndex);

                Float4 bodyColor = BodyOps.getColor(hitIndex, hitPosition, bodyColors);
                Float4 phongColor = getPhong(hitIndex, hitPosition, reflectionDir, bodyPosition, bodyColor, bodyReflectivity, lightPosition);
                float shadow = getShadow(hitPosition, bodyPositions, bodySizes, lightPosition, lightSize, shadowSampleSize);
                Float4 color = Color.mult(phongColor, shadow);

                reflectionColor = Color.add(reflectionColor, Color.mult(color, reflectivity * (1 - t)));
                reflectivity *= t;
                rayDirection = reflectionDir;
            } else
                break;
        }

        return reflectionColor;
    }

    public static Float4 getPixelColor(int hitIndex, Float4 hitPosition, Float4 rayDirection,
                                       VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                                       int shadowSampleSize, int reflectionBounceLimit) {

        Float4 lightPosition = bodyPositions.get(LIGHT_INDEX);
        float lightSize = bodySizes.get(LIGHT_INDEX);

        Float4 bodyPosition = bodyPositions.get(hitIndex);
        Float4 bodyColor = BodyOps.getColor(hitIndex, hitPosition, bodyColors);
        float bodyReflectivity = bodyReflectivities.get(hitIndex);

        Float4 reflectionColor = getReflection(hitIndex, hitPosition, rayDirection,
                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                lightPosition, lightSize, shadowSampleSize, reflectionBounceLimit);
        Float4 color = Color.mix(bodyColor, reflectionColor, bodyReflectivity / MAX_REFLECTIVITY);
        Float4 phongColor = getPhong(hitIndex, hitPosition, rayDirection, bodyPosition, color, bodyReflectivity, lightPosition);
        float shadow = getShadow(hitPosition, bodyPositions, bodySizes, lightPosition, lightSize, shadowSampleSize);
        return Color.mult(phongColor, shadow);
    }
}
