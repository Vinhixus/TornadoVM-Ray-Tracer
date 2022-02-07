package com.vinhderful.raytracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

/**
 * Additional 3D vector operations
 */
public class VectorOps {

    public static Float4 rotate(Float4 a, float yaw, float pitch) {

        float _yaw = yaw * floatPI() / 180;
        float _pitch = pitch * floatPI() / 180;

        float _y = a.getY() * floatCos(_pitch) - a.getZ() * floatSin(_pitch);
        float _z = a.getY() * floatSin(_pitch) + a.getZ() * floatCos(_pitch);

        float _x = a.getX() * floatCos(_yaw) + _z * floatSin(_yaw);
        _z = -a.getX() * floatSin(_yaw) + _z * floatCos(_yaw);

        return new Float4(_x, _y, _z, 0);
    }

    public static float distance(Float4 a, Float4 b) {
        return floatSqrt(pow(a.getX() - b.getX(), 2) + pow(a.getY() - b.getY(), 2) + pow(a.getZ() - b.getZ(), 2));
    }
}
