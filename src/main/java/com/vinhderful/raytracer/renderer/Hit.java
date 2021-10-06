package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

public class Hit {

    private final Shape shape;
    private final Vector3f position;

    public Hit(Shape shape, Vector3f position) {
        this.shape = shape;
        this.position = position;
    }

    public Shape getShape() {
        return shape;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Color getColor() {
        return shape.getColor(position);
    }

    public Vector3f getNormal() {
        return shape.getNormalAt(position);
    }
}
