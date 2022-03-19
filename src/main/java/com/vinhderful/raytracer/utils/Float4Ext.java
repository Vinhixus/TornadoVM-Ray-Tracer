package com.vinhderful.raytracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;

import static com.vinhderful.raytracer.utils.Angle.TO_RADIANS;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

/**
 * Additional Float4 operations
 */
public class Float4Ext {

    /**
     * Given a vector a yaw and a pitch value, return the vector rotated by the given yaw and pitch
     *
     * @param a     the vector
     * @param yaw   the yaw value in degrees
     * @param pitch the pitch value in degrees
     * @return Float4 containing the 3D vector a rotated around yaw and pitch
     */
    public static Float4 rotate(Float4 a, float yaw, float pitch) {

        // Convert yaw and pitch to radians
        float _yaw = yaw * TO_RADIANS;
        float _pitch = pitch * TO_RADIANS;

        // Perform horizontal rotation
        float _y = a.getY() * floatCos(_pitch) - a.getZ() * floatSin(_pitch);
        float _z = a.getY() * floatSin(_pitch) + a.getZ() * floatCos(_pitch);

        // Perform vertical rotation
        float _x = a.getX() * floatCos(_yaw) + _z * floatSin(_yaw);
        _z = -a.getX() * floatSin(_yaw) + _z * floatCos(_yaw);

        return new Float4(_x, _y, _z, 0);
    }

    /**
     * Return the distance in 3D space between two points
     *
     * @param a first vector
     * @param b second vector
     * @return float containing the distance between a and b
     */
    public static float distance(Float4 a, Float4 b) {
        float x = a.getX() - b.getX();
        float y = a.getY() - b.getY();
        float z = a.getZ() - b.getZ();
        return floatSqrt(x * x + y * y + z * z);
    }

    /**
     * Calculate the cross product between two vectors
     *
     * @param a first vector
     * @param b seconds vector
     * @return float containing the cross product between a and b
     */
    public static Float4 cross(Float4 a, Float4 b) {
        return new Float4(
                a.getY() * b.getZ() - a.getZ() * b.getY(),
                a.getZ() * b.getX() - a.getX() * b.getZ(),
                a.getX() * b.getY() - a.getY() * b.getX(),
                0);
    }

    /**
     * Get and arbitrary perpendicular vector to a vector
     *
     * @param a the vector
     * @return an arbitrary perpendicular vector to a
     */
    public static Float4 perpVector(Float4 a) {
        if (a.getY() == 0 && a.getZ() == 0)
            return cross(a, new Float4(0, 1, 0, 0));

        return cross(a, new Float4(1, 0, 0, 0));
    }
}
