package com.vinhderful.raytracer.misc.bodies;

import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;

/**
 * Represents a plane using position, height and reflectivity
 * The plane will have an appearance of a checkerboard
 */
public class Plane extends Body {

    /**
     * Construct a plane given its height, side size and reflectivity
     * Plane parallel to the X-Z plane
     *
     * @param height       height of the plane (Y coordinate)
     * @param size         the size of the side of the plane
     * @param reflectivity reflectivity of the plane
     */
    public Plane(float height, float size, float reflectivity) {
        super(new Float4(0, height, 0, 0), size, Color.GRAY, reflectivity);
    }
}
