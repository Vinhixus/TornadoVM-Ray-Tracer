package com.vinhderful.raytracer.scene;

import java.util.concurrent.CopyOnWriteArrayList;

import com.vinhderful.raytracer.shapes.Plane;
import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

public class World {

    private final CopyOnWriteArrayList<Shape> shapes;
    private final Camera camera;
    private final Light light;
    private final Plane plane;
    private final Color backgroundColor;

    public World() {
        this.shapes = new CopyOnWriteArrayList<>();
        this.camera = new Camera();
        this.plane = new Plane(-1F);
        this.light = new Light(new Vector3f(-1F, 0.8F, -1F), Color.WHITE);
        this.shapes.add(light);
        this.shapes.add(plane);
        this.backgroundColor = Color.BLACK;
    }

    public CopyOnWriteArrayList<Shape> getShapes() {
        return shapes;
    }

    public Camera getCamera() {
        return camera;
    }

    public Light getLight() {
        return light;
    }

    public void setLightX(float x) {
        light.setX(x);
    }

    public void setLightZ(float z) {
        light.setZ(z);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void addShape(Shape shape) {
        this.shapes.add(shape);
    }
}
