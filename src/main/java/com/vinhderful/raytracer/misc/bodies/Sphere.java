package com.vinhderful.raytracer.misc.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

public class Sphere extends Body {

    public Sphere(Float4 position, float radius, Float4 color, float reflectivity) {
        super(SPHERE, position, radius, color, reflectivity);
    }
}
