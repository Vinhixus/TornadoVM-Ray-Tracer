package com.vinhderful.pathtracer.misc.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

/**
 * Abstract class representing a solid body in a 3D space using its
 * position, size, color and reflectivity.
 * Class is extended by Light, Plane and Sphere
 */
public abstract class Body {

    private final Float4 position;
    private final float size;
    private final Float4 color;
    private final float reflectivity;

    /**
     * Create a body with given position, size, color and reflectivity
     *
     * @param position     position represented by a vector in 3d space
     * @param size         scale of the body
     * @param color        color of the body represented by RGB values
     * @param reflectivity reflectivity of the object
     */
    public Body(Float4 position, float size, Float4 color, float reflectivity) {
        this.position = position;
        this.size = size;
        this.color = color;
        this.reflectivity = reflectivity;
    }

    /**
     * Return the position of the body
     *
     * @return the position of the body
     */
    public Float4 getPosition() {
        return position;
    }

    /**
     * Return the size of the body
     *
     * @return the size of the body
     */
    public float getSize() {
        return size;
    }

    /**
     * Return the color of the body
     *
     * @return the color of the body
     */
    public Float4 getColor() {
        return color;
    }

    /**
     * Return the reflectivity of the body
     *
     * @return the reflectivity of the body
     */
    public float getReflectivity() {
        return reflectivity;
    }
}
