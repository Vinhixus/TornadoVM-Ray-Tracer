package com.vinhderful.raytracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import static com.vinhderful.raytracer.misc.bodies.Body.PLANE;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatSqrt;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floor;

/**
 * Represent a sphere in a 3D scene using its position, radius and color
 */
public class BodyOps {

    public static Float4 getIntersection(int bodyType, Float4 position, float size,
                                         Float4 rayOrigin, Float4 rayDirection) {

        final Float4 NO_INTERSECTION = new Float4(-1000F, -1000F, -1000F, -1000F);

        // Plane
        if (bodyType == PLANE) {
            float t = -(rayOrigin.getY() - position.getY()) / rayDirection.getY();
            if (t > 0 && Float.isFinite(t))
                return Float4.add(rayOrigin, Float4.mult(rayDirection, t));

            return NO_INTERSECTION;
        }

        // Sphere
        else {
            float t = Float4.dot(Float4.sub(position, rayOrigin), rayDirection);
            Float4 p = Float4.add(rayOrigin, Float4.mult(rayDirection, t));

            float y = Float4.length(Float4.sub(position, p));

            if (y < size) {
                float t1 = t - floatSqrt(size * size - y * y);
                if (t1 > 0) return Float4.add(rayOrigin, Float4.mult(rayDirection, t1));
                else return NO_INTERSECTION;
            } else
                return NO_INTERSECTION;
        }
    }

    public static Float4 getNormal(int bodyType, Float4 point, Float4 position) {

        // Plane normal is an up vector in the y direction
        if (bodyType == PLANE) {
            return new Float4(0, 1F, 0, 0);
        }

        // Sphere normal will be the direction from sphere origin to the surface point
        else return Float4.normalise(Float4.sub(point, position));
    }

    public static Float4 getColor(int hitIndex, Float4 point, VectorFloat4 bodyColors) {

        // Get checkerboard pattern for plane
        if (hitIndex == PLANE) {
            if ((int) (floor(point.getX()) + floor(point.getZ())) % 2 == 0)
                return new Float4(0.5F, 0.5F, 0.5F, 0);
            else
                return new Float4(0.2F, 0.2F, 0.2F, 0);
        }

        // Else simply return body color
        else return bodyColors.get(hitIndex);
    }
}
