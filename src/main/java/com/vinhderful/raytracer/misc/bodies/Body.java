package com.vinhderful.raytracer.misc.bodies;

import uk.ac.manchester.tornado.api.collections.types.Float4;

public abstract class Body {

    public static final int LIGHT = 0;
    public static final int PLANE = 1;
    public static final int SPHERE = 2;

    private final int type;
    private final Float4 position;
    private final float size;
    private final Float4 color;
    private final float reflectivity;

    public Body(int type, Float4 position, float size, Float4 color, float reflectivity) {
        this.type = type;
        this.position = position;
        this.size = size;
        this.color = color;
        this.reflectivity = reflectivity;
    }

    public int getType() {
        return type;
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
