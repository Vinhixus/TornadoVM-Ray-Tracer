package com.vinhderful.raytracer.utils;

public class Vector3f {
    private float x;
    private float y;
    private float z;

    public Vector3f rotate(float yaw, float pitch) {

        double _yaw = Math.toRadians(yaw);
        double _pitch = Math.toRadians(pitch);

        float _y = (float) (y * Math.cos(_pitch) - z * Math.sin(_pitch));
        float _z = (float) (y * Math.sin(_pitch) + z * Math.cos(_pitch));

        float _x = (float) (x * Math.cos(_yaw) + _z * Math.sin(_yaw));
        _z = (float) (-x * Math.sin(_yaw) + _z * Math.cos(_yaw));

        return new Vector3f(_x, _y, _z);
    }

    public Vector3f(float x, float y, float z) {
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
            throw new IllegalArgumentException("One or more parameters are NaN!");

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
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

    public Vector3f normalize() {
        float magnitude = magnitude();
        return new Vector3f(x / magnitude, y / magnitude, z / magnitude);
    }

    public float dotProduct(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public float distanceFrom(Vector3f v) {
        return (float) Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2) + Math.pow(z - v.z, 2));
    }

    @Override
    public String toString() {
        return "Vector3f {" + "x: " + x + ", y: " + y + ", z: " + z + '}';
    }
}
