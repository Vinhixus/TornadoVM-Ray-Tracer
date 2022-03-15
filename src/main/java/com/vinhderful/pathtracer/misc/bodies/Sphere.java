package com.vinhderful.pathtracer.misc.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

/**
 * Represents a sphere using position, radius and color and reflectivity
 */
public class Sphere extends Body {

    /**
     * Construct a sphere given its position, radius and color and reflectivity
     *
     * @param position     position represented by a vector in 3d space
     * @param radius       radius of the sphere
     * @param color        color of the body represented by RGB values
     * @param reflectivity reflectivity of the sphere
     */
    public Sphere(Float4 position, float radius, Float4 color, float reflectivity) {
        super(position, radius, color, reflectivity);
    }
}
