package com.vinhderful.raytracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

/**
 * Additional 3D vector operations
 */
public class VectorOps {

    public static Float4 rotate(Float4 a, float yaw, float pitch) {

        float _yaw = yaw * 0.017453292F;
        float _pitch = pitch * 0.017453292F;

        float _y = a.getY() * floatCos(_pitch) - a.getZ() * floatSin(_pitch);
        float _z = a.getY() * floatSin(_pitch) + a.getZ() * floatCos(_pitch);

        float _x = a.getX() * floatCos(_yaw) + _z * floatSin(_yaw);
        _z = -a.getX() * floatSin(_yaw) + _z * floatCos(_yaw);

        return new Float4(_x, _y, _z, 0);
    }

    public static float distance(Float4 a, Float4 b) {
        float x = a.getX() - b.getX();
        float y = a.getY() - b.getY();
        float z = a.getZ() - b.getZ();
        return floatSqrt(x * x + y * y + z * z);
    }

    public static Float4 cross(Float4 a, Float4 b) {
        return new Float4(
                a.getY() * b.getZ() - a.getZ() * b.getY(),
                a.getZ() * b.getX() - a.getX() * b.getZ(),
                a.getX() * b.getY() - a.getY() * b.getX(),
                0);
    }

    public static Float4 getPerpVector(Float4 a) {
        if (a.getY() == 0 && a.getZ() == 0)
            return VectorOps.cross(a, new Float4(0, 1, 0, 0));

        return VectorOps.cross(a, new Float4(1, 0, 0, 0));
    }
}
