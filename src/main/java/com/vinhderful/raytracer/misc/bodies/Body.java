package com.vinhderful.raytracer.misc.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

public abstract class Body {

    private final Float4 position;
    private final float size;
    private final Float4 color;
    private final float reflectivity;

    public Body(Float4 position, float size, Float4 color, float reflectivity) {
        this.position = position;
        this.size = size;
        this.color = color;
        this.reflectivity = reflectivity;
    }

    public Float4 getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }

    public Float4 getColor() {
        return color;
    }

    public float getReflectivity() {
        return reflectivity;
    }
}
