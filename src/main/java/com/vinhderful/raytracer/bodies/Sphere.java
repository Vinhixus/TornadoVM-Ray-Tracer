package com.vinhderful.raytracer.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatSqrt;

/**
 * Represent a sphere in a 3D scene using its position, radius and color
 */
public class Sphere {

    public static Float4 getIntersection(Float4 position, float radius, Float4 rayOrigin, Float4 rayDirection) {

        final Float4 NO_INTERSECTION = new Float4(-1F, -1F, -1F, -1F);

        float t = Float4.dot(Float4.sub(position, rayOrigin), rayDirection);
        Float4 p = Float4.add(rayOrigin, Float4.mult(rayDirection, t));

        float y = Float4.length(Float4.sub(position, p));

        if (y < radius) {
            float t1 = t - floatSqrt(radius * radius - y * y);
            if (t1 > 0) return Float4.add(rayOrigin, Float4.mult(rayDirection, t1));
            else return NO_INTERSECTION;
        } else
            return NO_INTERSECTION;
    }
}
