package com.vinhderful.raytracer.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.abs;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatSqrt;

/**
 * Represent a sphere in a 3D scene using its position, radius and color
 */
public class Body {

    public static Float4 getIntersection(int bodyType, Float4 position, float size,
                                         Float4 rayOrigin, Float4 rayDirection) {

        final Float4 NO_INTERSECTION = new Float4(-1F, -1F, -1F, -1F);

        // Plane
        if (bodyType == 0) {
            float t = -(rayOrigin.getY() - position.getY()) / rayDirection.getY();
            if (t > 0 && Float.isFinite(t))
                return Float4.add(rayOrigin, Float4.mult(rayDirection, t));

            return NO_INTERSECTION;
        }

        // Cube
        else if (bodyType == 1) {
            Float4 min = Float4.sub(position, size * 0.5F);
            Float4 max = Float4.add(position, size * 0.5F);

            float t1, t2, tNear = Float.NEGATIVE_INFINITY, tFar = Float.POSITIVE_INFINITY;
            boolean intersects = true;

            for (int i = 0; i < 3; i++) {
                if (rayDirection.get(i) == 0) {
                    if (rayOrigin.get(i) < min.get(i) || rayOrigin.get(i) > max.get(i))
                        intersects = false;
                } else {
                    t1 = (min.get(i) - rayOrigin.get(i)) / rayDirection.get(i);
                    t2 = (max.get(i) - rayOrigin.get(i)) / rayDirection.get(i);

                    if (t1 > t2) {
                        float temp = t1;
                        t1 = t2;
                        t2 = temp;
                    }

                    if (t1 > tNear)
                        tNear = t1;
                    if (t2 < tFar)
                        tFar = t2;
                    if (tNear > tFar || tFar < 0)
                        intersects = false;
                }
            }

            if (intersects) return Float4.add(rayOrigin, Float4.mult(rayDirection, tNear));
            else return NO_INTERSECTION;
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

        // Plane
        if (bodyType == 0)
            return new Float4(0, 1F, 0, 0);

            // Cube
        else if (bodyType == 1) {

            Float4 direction = Float4.sub(point, position);
            float biggestValue = Float.POSITIVE_INFINITY;

            for (int i = 0; i < 3; i++)
                if (biggestValue == Float.POSITIVE_INFINITY || biggestValue < abs(direction.get(i)))
                    biggestValue = abs(direction.get(i));

            if (biggestValue == 0) return new Float4(0, 0, 0, 0);
            else
                for (int i = 0; i < 3; i++)
                    if (abs(direction.get(i)) == biggestValue) {
                        Float4 normal = new Float4(0, 0, 0, 0);
                        normal.set(i, direction.get(i) > 0 ? 1 : -1);
                        return normal;
                    }

            return new Float4(0, 0, 0, 0);
        }

        // Sphere
        else
            return Float4.normalise(Float4.sub(point, position));
    }

    public static Float4 getPlaneColor(Float4 point) {

        float x = point.getX();
        float z = point.getZ();
        // int xInt = (int) x;
        // int zInt = (int) z;
        // boolean b = (xInt % 2 == 0 & zInt % 2 != 0) || (xInt % 2 != 0 & zInt % 2 == 0);

        if ((x > 0 & z > 0) || (x < 0 & z < 0))
            return new Float4(0.5F, 0.5F, 0.5F, 0);
            // return b ? new Float4(0.5F, 0.5F, 0.5F, 0) : new Float4(0.2F, 0.2F, 0.2F, 0);
        else
            return new Float4(0.2F, 0.2F, 0.2F, 0);
        // return b ? new Float4(0.2F, 0.2F, 0.2F, 0) : new Float4(0.5F, 0.5F, 0.5F, 0);
    }
}
