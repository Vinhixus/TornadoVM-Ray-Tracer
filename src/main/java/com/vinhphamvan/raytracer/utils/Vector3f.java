package com.vinhphamvan.raytracer.utils;

public class Vector3f {
    private final float x,y,z;

    public Vector3f(float x, float y, float z) {
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
            throw new IllegalArgumentException("One or more parameters are NaN!");

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f add(Vector3f vec) {
        return new Vector3f(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vector3f subtract(Vector3f vec) {
        return new Vector3f(x - vec.x, y - vec.y, z - vec.z);
    }

    public Vector3f multiply(float scalar) {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f normalize() {
        float length = length();
        return new Vector3f(x / length, y / length, z / length);
    }

    public static float dot(Vector3f a, Vector3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    @Override
    public String toString() {
        return "Vector3f {" + "x: " + x + ", y: " + y + ", z: " + z + '}';
    }
}
