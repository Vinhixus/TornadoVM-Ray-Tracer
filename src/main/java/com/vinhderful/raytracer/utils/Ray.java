package com.vinhderful.raytracer.utils;

/**
 * Represents a ray using its origin and direction
 */
public class Ray {

    private final Vector3f origin;
    private final Vector3f direction;

    /**
     * Construct a Ray object using origin and direction
     * 
     * @param origin
     *            the origin point of the ray
     * @param direction
     *            the direction of the ray
     */
    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * Get the origin of the ray
     *
     * @return the origin of the ray
     */
    public Vector3f getOrigin() {
        return origin;
    }

    /**
     * Get the direction of the ray
     *
     * @return the direction of the ray
     */
    public Vector3f getDirection() {
        return direction;
    }
}