package com.vinhderful.raytracer.utils;

public class Vector3f {
    private float x;
    private float y;
    private float z;

    public static Vector3f normalize(Vector3f v) {
        float length = v.length();
        return new Vector3f(v.x / length, v.y / length, v.z / length);
    }

    public static float dotProduct(Vector3f a, Vector3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static float distance(Vector3f a, Vector3f b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
    }

    public static Vector3f rotate(Vector3f v, float yaw, float pitch) {

        double _yaw = Math.toRadians(yaw);
        double _pitch = Math.toRadians(pitch);

        float _y = (float) (v.y * Math.cos(_pitch) - v.z * Math.sin(_pitch));
        float _z = (float) (v.y * Math.sin(_pitch) + v.z * Math.cos(_pitch));

        float _x = (float) (v.x * Math.cos(_yaw) + _z * Math.sin(_yaw));
        _z = (float) (-v.x * Math.sin(_yaw) + _z * Math.cos(_yaw));

        return new Vector3f(_x, _y, _z);
    }

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

    public void normalize() {
        float length = length();
        x = x / length;
        y = y / length;
        z = z / length;
    }

    @Override
    public String toString() {
        return "Vector3f {" + "x: " + x + ", y: " + y + ", z: " + z + '}';
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
