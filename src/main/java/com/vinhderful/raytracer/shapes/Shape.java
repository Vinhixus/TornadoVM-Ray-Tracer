package com.vinhderful.raytracer.shapes;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

public abstract class Shape {
    protected Vector3f position;
    protected Color color;
    protected float reflectivity;

    public Shape(Vector3f position) {
        this.position = position;
        this.color = Color.BLACK;
        this.reflectivity = 16F;
    }

    public Shape(Vector3f position, Color color) {
        this.position = position;
        this.color = color;
        this.reflectivity = 16F;
    }

    public Shape(Vector3f position, Color color, float reflectivity) {
        this.position = position;
        this.color = color;
        this.reflectivity = reflectivity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setX(float x) {
        position.setX(x);
    }

    public void setZ(float z) {
        position.setZ(z);
    }

    public Color getColor(Vector3f point) {
        return color;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public abstract Vector3f getIntersection(Ray ray);

    public abstract Vector3f getNormalAt(Vector3f point);
}
