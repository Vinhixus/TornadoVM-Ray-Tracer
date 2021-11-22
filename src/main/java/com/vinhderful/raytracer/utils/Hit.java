package com.vinhderful.raytracer.utils;

import com.vinhderful.raytracer.bodies.Body;

/**
 * Represents an event of a ray hitting a body, storing the ray, the body and
 * the hit position
 */
public class Hit {

    private final Body body;
    private final Ray ray;
    private final Vector3f position;

    /**
     * Construct a hit event using a body, ray and hit position
     *
     * @param body     the body being hit
     * @param ray      the ray hitting the body
     * @param position the position of the hit
     */
    public Hit(Body body, Ray ray, Vector3f position) {
        this.body = body;
        this.ray = ray;
        this.position = position;
    }

    /**
     * Get the body being hit
     *
     * @return the body being hit
     */
    public Body getBody() {
        return body;
    }

    /**
     * Get the ray hitting the body
     *
     * @return the ray hitting the body
     */
    public Ray getRay() {
        return ray;
    }

    /**
     * Get the position of the hit
     *
     * @return the position of the hit
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Get the color of the body at the hit position
     *
     * @return the color of the body at the hit position
     */
    public Color getColor() {
        return body.getColor(position);
    }

    /**
     * Get the normal vector from the body at the hit position
     *
     * @return the normal vector from the body at the hit position
     */
    public Vector3f getNormal() {
        return body.getNormalAt(position);
    }
}
