package com.vinhderful.raytracer.renderer;

import java.util.concurrent.CopyOnWriteArrayList;

import com.vinhderful.raytracer.shapes.Shape;

import javafx.scene.paint.Color;

public class World {

    private Color backgroundColor;
    private final CopyOnWriteArrayList<Shape> shapes;

    public World(Color backgroundColor) {
        this.shapes = new CopyOnWriteArrayList<>();
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public CopyOnWriteArrayList<Shape> getShapes() {
        return shapes;
    }

    public void addShape(Shape shape) {
        this.shapes.add(shape);
    }
}
