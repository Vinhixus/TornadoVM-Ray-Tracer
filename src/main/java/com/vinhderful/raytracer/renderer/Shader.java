package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Sphere;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.max;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.pow;

public class Shader {

    public static Float4 getAmbient(Float4 bodyColor, Float4 lightColor) {

        final float AMBIENT_STRENGTH = 0.05F;
        return Color.mult(Color.mult(bodyColor, lightColor), AMBIENT_STRENGTH);
    }

    public static Float4 getDiffuse(Float4 hitPosition, Float4 bodyPosition, Float4 bodyColor,
                                    Float4 lightPosition, Float4 lightColor) {
        float diffuseBrightness = max(0, Float4.dot(Sphere.getNormal(hitPosition, bodyPosition), Float4.normalise(Float4.sub(lightPosition, hitPosition))));
        return Color.mult(Color.mult(bodyColor, lightColor), diffuseBrightness);
    }

    public static Float4 getSpecular(Float4 cameraPosition, Float4 hitPosition,
                                     Float4 bodyPosition, float bodyReflectivity,
                                     Float4 lightPosition, Float4 lightColor) {

        final float SPECULAR_STRENGTH = 0.5F;

        Float4 rayDirection = Float4.normalise(Float4.sub(hitPosition, cameraPosition));
        Float4 lightDirection = Float4.normalise(Float4.sub(lightPosition, hitPosition));

        Float4 reflectionVector = Float4.sub(lightDirection,
                Float4.mult(Sphere.getNormal(hitPosition, bodyPosition),
                        2 * Float4.dot(lightDirection, Sphere.getNormal(hitPosition, bodyPosition))));

        float specularFactor = max(0, Float4.dot(reflectionVector, rayDirection));
        float specularBrightness = pow(specularFactor, bodyReflectivity);

        return Color.mult(Color.mult(lightColor, specularBrightness), SPECULAR_STRENGTH);
    }
}
