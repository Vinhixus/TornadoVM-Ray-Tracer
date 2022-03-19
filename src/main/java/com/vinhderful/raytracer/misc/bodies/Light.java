package com.vinhderful.raytracer.misc.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

/**
 * Represents a sphere light using position, radius and color
 */
public class Light extends Body {


    /**
     * Construct a light given its position, radius and color
     *
     * @param position position represented by a vector in 3d space
     * @param radius   radius of the sphere
     * @param color    color of the body represented by RGB values
     */
    public Light(Float4 position, float radius, Float4 color) {
        super(position, radius, color, 0);
    }
}
