package com.vinhderful.raytracer.renderer;

import java.util.concurrent.CopyOnWriteArrayList;

import com.vinhderful.raytracer.shapes.Shape;

public class World {

    private final CopyOnWriteArrayList<Shape> shapes;

    public World() {
        this.shapes = new CopyOnWriteArrayList<>();
    }

    public CopyOnWriteArrayList<Shape> getShapes() {
        return shapes;
    }

    public void addShape(Shape shape) {
        this.shapes.add(shape);
    }
}
