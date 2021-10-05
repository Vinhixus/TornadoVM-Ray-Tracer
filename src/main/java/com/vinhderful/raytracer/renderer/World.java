package com.vinhderful.raytracer.renderer;

import java.util.concurrent.CopyOnWriteArrayList;

import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

public class World {

    private final CopyOnWriteArrayList<Shape> shapes;
    private final Light light;
    private final Color backgroundColor;

    public World(Color backgroundColor) {
        this.shapes = new CopyOnWriteArrayList<>();
        this.light = new Light(new Vector3f(-1, 2, -1));
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public CopyOnWriteArrayList<Shape> getShapes() {
        return shapes;
    }

    public Light getLight() {
        return light;
    }

    public void addShape(Shape shape) {
        this.shapes.add(shape);
    }
}
