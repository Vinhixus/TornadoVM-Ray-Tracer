package com.vinhderful.pathtracer.misc.bodies;

import com.vinhderful.pathtracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;

/**
 * Represents a plane using position, height and reflectivity
 * The plane will have an appearance of a checkerboard
 */
public class Plane extends Body {

    /**
     * Construct a plane given its height and reflectivity
     * Plane parallel to the X-Z plane
     *
     * @param height       height of the plane (Y coordinate)
     * @param reflectivity reflectivity of the plane
     */
    public Plane(float height, float reflectivity) {
        super(new Float4(0, height, 0, 0), -1F, Color.BLACK, reflectivity);
    }
}
