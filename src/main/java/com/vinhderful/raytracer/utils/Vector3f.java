package com.vinhderful.raytracer.utils;

public class Vector3f {
    private float x;
    private float y;
    private float z;

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

    public void translate(Vector3f vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
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

    public static float distance(Vector3f a, Vector3f b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
    }

    public Vector3f normalize() {
        float length = length();
        return new Vector3f(x / length, y / length, z / length);
    }

    public Vector3f rotateYP(float yaw, float pitch) {
        // Convert to radians
        double yawRads = Math.toRadians(yaw);
        double pitchRads = Math.toRadians(pitch);

        // Step one: Rotate around X axis (pitch)
        float _y = (float) (y * Math.cos(pitchRads) - z * Math.sin(pitchRads));
        float _z = (float) (y * Math.sin(pitchRads) + z * Math.cos(pitchRads));

        // Step two: Rotate around the Y axis (yaw)
        float _x = (float) (x * Math.cos(yawRads) + _z * Math.sin(yawRads));
        _z = (float) (-x * Math.sin(yawRads) + _z * Math.cos(yawRads));

        return new Vector3f(_x, _y, _z);
    }

    public static float getDotProduct(Vector3f a, Vector3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    @Override
    public String toString() {
        return "Vector3f {" + "x: " + x + ", y: " + y + ", z: " + z + '}';
    }
}
