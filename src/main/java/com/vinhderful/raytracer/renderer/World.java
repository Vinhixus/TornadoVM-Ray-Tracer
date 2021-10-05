package com.vinhderful.raytracer.renderer;

import java.util.concurrent.CopyOnWriteArrayList;

import com.vinhderful.raytracer.shapes.Plane;
import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

public class World {

    private final CopyOnWriteArrayList<Shape> shapes;
    private final Light light;
    private final Plane plane;
    private final Color backgroundColor;

    public World(Color backgroundColor) {
        this.shapes = new CopyOnWriteArrayList<>();
        this.plane = new Plane(-1F);
        this.light = new Light(new Vector3f(-1F, 0.8F, -1F), Color.WHITE);
        this.shapes.add(light);
        this.shapes.add(plane);
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public CopyOnWriteArrayList<Shape> getShapes() {
        return shapes;
    }

    public Plane getPlane() {
        return plane;
    }

    public Light getLight() {
        return light;
    }

    public void setLightX(float x) {
        light.setX(x);
    }

    public void setLightY(float y) {
        light.setY(y);
    }

    public void setLightZ(float z) {
        light.setZ(z);
    }

    public void addShape(Shape shape) {
        this.shapes.add(shape);
    }
}
