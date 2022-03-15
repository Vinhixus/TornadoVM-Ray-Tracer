package com.vinhderful.pathtracer.renderer;

import com.vinhderful.pathtracer.utils.BodyOps;
import com.vinhderful.pathtracer.utils.Color;
import com.vinhderful.pathtracer.utils.Float4Ext;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import static com.vinhderful.pathtracer.misc.World.LIGHT_INDEX;
import static com.vinhderful.pathtracer.misc.World.PLANE_INDEX;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

public class Shader {

    public static final float AMBIENT_STRENGTH = 0.1F;
    public static final float MAX_REFLECTIVITY = 128F;
    public static final float SHADOW_STRENGTH = 0.15F;

    public static final float PHI = floatPI() * (3 - floatSqrt(5));

    public static Float4 getBlinnPhong(int hitIndex, Float4 hitPosition, Float4 rayOrigin,
                                       Float4 bodyPosition, Float4 bodyColor, float bodyReflectivity,
                                       Float4 lightPosition) {

        float diffuse = max(AMBIENT_STRENGTH, getDiffuse(hitIndex, hitPosition, bodyPosition, lightPosition));
        float specular = getSpecular(hitIndex, hitPosition, rayOrigin, bodyPosition, bodyReflectivity, lightPosition);
        return Color.add(Color.mult(bodyColor, diffuse), specular);
    }

    public static float getDiffuse(int hitIndex, Float4 hitPosition, Float4 bodyPosition, Float4 lightPosition) {
        Float4 hitNormal = BodyOps.getNormal(hitIndex, hitPosition, bodyPosition);
        Float4 lightDirection = Float4.normalise(Float4.sub(lightPosition, hitPosition));
        return Float4.dot(hitNormal, lightDirection);
    }

    public static float getSpecular(int hitIndex, Float4 hitPosition, Float4 rayOrigin,
                                    Float4 bodyPosition, float bodyReflectivity,
                                    Float4 lightPosition) {

        Float4 viewDirection = Float4.normalise(Float4.sub(rayOrigin, hitPosition));
        Float4 lightDirection = Float4.normalise(Float4.sub(lightPosition, hitPosition));
        Float4 hitNormal = BodyOps.getNormal(hitIndex, hitPosition, bodyPosition);

        Float4 halfwayDirection = Float4.normalise(Float4.add(lightDirection, viewDirection));
        float specularFactor = max(0, Float4.dot(hitNormal, halfwayDirection));

        float k = (8.0F + bodyReflectivity) / (8.0F * floatPI());
        return k * pow(specularFactor, bodyReflectivity) * (bodyReflectivity / MAX_REFLECTIVITY);
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
        else return 1 - (float) raysHit / (sampleSize * (1 + SHADOW_STRENGTH));
    }

    public static Float4 getReflection(int hitIndex, Float4 hitPosition, Float4 rayDirection,
                                       VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                                       Float4 lightPosition, float lightSize,
                                       VectorFloat4 skybox, int[] skyboxDimensions,
                                       int shadowSampleSize, int reflectionBounceLimit) {

        Float4 reflectionColor = new Float4(0, 0, 0, 0);
        float reflectivity = 1F;
        float diffuse = 1F;

        for (int i = 0; i < reflectionBounceLimit && hitIndex > LIGHT_INDEX; i++) {

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

                Float4 color = BodyOps.getColor(hitIndex, hitPosition, bodyColors);

                if (hitIndex > LIGHT_INDEX) {
                    float shadow = diffuse * getShadow(hitPosition, bodyPositions, bodySizes, lightPosition, lightSize, shadowSampleSize);
                    float specular = diffuse * getSpecular(hitIndex, hitPosition, reflectionOrigin, bodyPosition, bodyReflectivity, lightPosition);
                    diffuse *= max(AMBIENT_STRENGTH, getDiffuse(hitIndex, hitPosition, bodyPosition, lightPosition));
                    color = Color.mult(Color.add(Color.mult(color, diffuse), specular), shadow);
                }

                if (i == reflectionBounceLimit - 1)
                    reflectionColor = Color.add(reflectionColor, Color.mult(color, reflectivity));
                else
                    reflectionColor = Color.add(reflectionColor, Color.mult(color, reflectivity * (1 - t)));

                reflectivity *= t;
                rayDirection = reflectionDir;
            } else {
                Float4 color = BodyOps.getSkyboxColor(skybox, skyboxDimensions, reflectionDir);
                reflectionColor = Color.add(reflectionColor, Color.mult(color, reflectivity));
            }
        }

        return reflectionColor;
    }

    public static Float4 getPixelColor(int hitIndex, Float4 hitPosition, Float4 rayOrigin, Float4 rayDirection,
                                       VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                                       VectorFloat4 skybox, int[] skyboxDimensions,
                                       int shadowSampleSize, int reflectionBounceLimit) {

        Float4 lightPosition = bodyPositions.get(LIGHT_INDEX);
        float lightSize = bodySizes.get(LIGHT_INDEX);

        Float4 bodyPosition = bodyPositions.get(hitIndex);
        Float4 bodyColor = BodyOps.getColor(hitIndex, hitPosition, bodyColors);
        float bodyReflectivity = bodyReflectivities.get(hitIndex);

        Float4 reflectionColor = getReflection(hitIndex, hitPosition, rayDirection,
                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                lightPosition, lightSize,
                skybox, skyboxDimensions,
                shadowSampleSize, reflectionBounceLimit);
        Float4 color = Color.mix(bodyColor, reflectionColor, bodyReflectivity / MAX_REFLECTIVITY);

        if (hitIndex > LIGHT_INDEX)
            color = getBlinnPhong(hitIndex, hitPosition, rayOrigin, bodyPosition, color, bodyReflectivity, lightPosition);

        float shadow = getShadow(hitPosition, bodyPositions, bodySizes, lightPosition, lightSize, shadowSampleSize);
        return Color.mult(color, shadow);
    }
}
