package com.vinhderful.raytracer.misc.bodies;

import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;

public class Plane extends Body {

    public Plane(float height, float reflectivity) {
        super(PLANE, new Float4(0, height, 0, 0), -1F, Color.BLACK, reflectivity);
    }
}
