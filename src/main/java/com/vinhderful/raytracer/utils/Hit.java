package com.vinhderful.raytracer.utils;

import com.vinhderful.raytracer.shapes.Shape;

public class Hit {

    private final Shape shape;
    private final Ray ray;
    private final Vector3f position;

    public Hit(Shape shape, Ray ray, Vector3f position) {
        this.shape = shape;
        this.ray = ray;
        this.position = position;
    }

    public Shape getShape() {
        return shape;
    }

    public Ray getRay() {
        return ray;
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
