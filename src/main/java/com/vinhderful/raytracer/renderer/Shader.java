package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Sphere;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.max;

public class Shader {

    public static Float4 getDiffuse(Float4 hitPosition, Float4 bodyPosition, Float4 bodyColor,
                                    Float4 lightPosition, Float4 lightColor) {
        float diffuseBrightness = max(0, Float4.dot(Sphere.getNormal(hitPosition, bodyPosition), Float4.normalise(Float4.sub(lightPosition, hitPosition))));
        return Color.mult(Color.mult(bodyColor, lightColor), diffuseBrightness);
    }
}
