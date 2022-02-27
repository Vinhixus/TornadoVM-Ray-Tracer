package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Body;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.VectorOps;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.collections.types.VectorInt;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

public class Shader {

    public static final float AMBIENT_STRENGTH = 0.05F;
    public static final float SPECULAR_STRENGTH = 0.5F;
    public static final float MAX_REFLECTIVITY = 128F;
    public static final float SHADOW_STRENGTH = 1F / 0.8F;

    public static final float PHI = floatPI() * (3 - floatSqrt(5));

    public static Float4 getPhong(Float4 cameraPosition, int bodyType, Float4 hitPosition,
                                  Float4 bodyPosition, Float4 bodyColor, float bodyReflectivity,
                                  Float4 lightPosition, Float4 lightColor) {
        return Color.add(Color.add(
                        Shader.getAmbient(bodyColor, lightColor),
                        Shader.getDiffuse(bodyType, hitPosition, bodyPosition, bodyColor, lightPosition, lightColor)),
                Shader.getSpecular(cameraPosition, bodyType, hitPosition, bodyPosition, bodyReflectivity, lightPosition, lightColor));
    }

    public static Float4 getAmbient(Float4 bodyColor, Float4 lightColor) {
        return Color.mult(Color.mult(bodyColor, lightColor), AMBIENT_STRENGTH);
    }

    public static Float4 getDiffuse(int bodyType, Float4 hitPosition,
                                    Float4 bodyPosition, Float4 bodyColor,
                                    Float4 lightPosition, Float4 lightColor) {
        float diffuseBrightness = max(0, Float4.dot(Body.getNormal(bodyType, hitPosition, bodyPosition), Float4.normalise(Float4.sub(lightPosition, hitPosition))));
        return Color.mult(Color.mult(bodyColor, lightColor), diffuseBrightness);
    }

    public static Float4 getSpecular(Float4 cameraPosition, int bodyType, Float4 hitPosition,
                                     Float4 bodyPosition, float bodyReflectivity,
                                     Float4 lightPosition, Float4 lightColor) {

        Float4 rayDirection = Float4.normalise(Float4.sub(hitPosition, cameraPosition));
        Float4 lightDirection = Float4.normalise(Float4.sub(lightPosition, hitPosition));

        Float4 reflectionVector = Float4.sub(lightDirection,
                Float4.mult(Body.getNormal(bodyType, hitPosition, bodyPosition),
                        2 * Float4.dot(lightDirection, Body.getNormal(bodyType, hitPosition, bodyPosition))));

        float specularFactor = max(0, Float4.dot(reflectionVector, rayDirection));
        float specularBrightness = pow(specularFactor, bodyReflectivity);

        return Color.mult(Color.mult(lightColor, specularBrightness), SPECULAR_STRENGTH);
    }

    public static float getShadow(Float4 hitPosition,
                                  VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                  Float4 lightPosition, float lightSize, int sampleSize) {

        int raysHit = 0;

        Float4 n = Float4.normalise(Float4.sub(hitPosition, lightPosition));
        Float4 u = VectorOps.getPerpVector(n);
        Float4 v = VectorOps.cross(u, n);

        for (int i = 0; i < sampleSize; i++) {

            float t = PHI * i;
            float r = floatSqrt((float) i / sampleSize);

            float x = 2 * lightSize * r * floatCos(t);
            float y = 2 * lightSize * r * floatSin(t);

            Float4 samplePoint = Float4.add(Float4.add(lightPosition, Float4.mult(u, x)), Float4.mult(v, y));
            Float4 rayDir = Float4.normalise(Float4.sub(samplePoint, hitPosition));
            Float4 rayOrigin = Float4.add(hitPosition, Float4.mult(rayDir, 0.001F));

            if (Renderer.intersects(bodyTypes, bodyPositions, bodySizes, rayOrigin, rayDir))
                raysHit++;
        }

        if (raysHit == 0) return 1;
        else return 1 - (float) raysHit / (sampleSize * SHADOW_STRENGTH);
    }

    public static Float4 getReflection(int bounceLimit, int hitIndex, Float4 hitPosition, Float4 rayDirection,
                                       VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                                       Float4 lightPosition, float lightSize, Float4 lightColor, int lightSampleSize) {

        Float4 reflectionColor = new Float4(0, 0, 0, 0);
        int _index = hitIndex;
        float reflectivity = 1F;
        Float4 _rayDirection = new Float4(rayDirection.getX(), rayDirection.getY(), rayDirection.getZ(), 0);
        Float4 _hitPosition = new Float4(hitPosition.getX(), hitPosition.getY(), hitPosition.getZ(), 0);

        for (int i = 0; i < bounceLimit; i++) {

            Float4 hitNormal = Body.getNormal(bodyTypes.get(_index), _hitPosition, bodyPositions.get(_index));
            Float4 reflectionDir = Float4.sub(_rayDirection, Float4.mult(hitNormal, 2 * Float4.dot(_rayDirection, hitNormal)));
            Float4 reflectionOrigin = Float4.add(_hitPosition, Float4.mult(reflectionDir, 0.001F));

            reflectivity *= bodyReflectivities.get(_index) / MAX_REFLECTIVITY;
            Float4 closestHit = Renderer.getClosestHit(bodyTypes, bodyPositions, bodySizes, reflectionOrigin, reflectionDir);
            int closestHitIndex = (int) closestHit.getW();

            if (closestHitIndex != -1000) {

                Float4 closestHitPosition = new Float4(closestHit.getX(), closestHit.getY(), closestHit.getZ(), 0);

                int bodyType = bodyTypes.get(closestHitIndex);
                Float4 bodyPosition = bodyPositions.get(closestHitIndex);
                float bodyReflectivity = bodyReflectivities.get(closestHitIndex);

                Float4 bodyColor = (bodyType == 1) ? Body.getPlaneColor(closestHitPosition) : bodyColors.get(closestHitIndex);

                reflectionColor = Color.add(reflectionColor, Color.mult(Color.mult(
                                getPhong(reflectionOrigin, bodyType, closestHitPosition, bodyPosition, bodyColor, bodyReflectivity, lightPosition, lightColor),
                                getShadow(closestHitPosition, bodyTypes, bodyPositions, bodySizes, lightPosition, lightSize, lightSampleSize)),
                        reflectivity));

                _rayDirection = new Float4(reflectionDir.getX(), reflectionDir.getY(), reflectionDir.getZ(), 0);
                _hitPosition = new Float4(closestHitPosition.getX(), closestHitPosition.getY(), closestHitPosition.getZ(), 0);
                _index = closestHitIndex;
            } else
                break;
        }

        return reflectionColor;
    }
}
