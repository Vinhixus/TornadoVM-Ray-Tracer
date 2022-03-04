package com.vinhderful.raytracer.misc.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

public class Light extends Body {

    public Light(Float4 position, float radius, Float4 color) {
        super(position, radius, color, 0);
    }
}
