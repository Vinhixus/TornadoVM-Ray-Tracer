package com.vinhphamvan.raytracer.utils;

public class Ray {
    private final Vector3f origin;
    private final Vector3f direction;

    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;

        if (direction.length() != 1)
            direction = direction.normalize();

        this.direction = direction;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Vector3f getDirection() {
        return direction;
    }
}