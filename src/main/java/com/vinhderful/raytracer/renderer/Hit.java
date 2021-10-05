package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

public class Hit {

    private final Ray ray;
    private final Shape shape;
    private final Vector3f position;

    public Hit(Ray ray, Shape shape, Vector3f position) {
        this.ray = ray;
        this.shape = shape;
        this.position = position;
    }

    public Ray getRay() {
        return ray;
    }

    public Shape getShape() {
        return shape;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getNormal() {
        return shape.getNormalVectorAt(position);
    }
}
