package com.vinhderful.raytracer.shapes;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

public abstract class Shape {
    protected Vector3f position;
    protected Color color;

    public Shape(Vector3f position) {
        this.position = position;
        this.color = Color.BLACK;
    }

    public Shape(Vector3f position, Color color) {
        this.position = position;
        this.color = color;
    }

    public abstract Vector3f getIntersection(Ray ray);

    public abstract Vector3f getNormalVectorAt(Vector3f point);

    public Color getColor(Vector3f point) {
        return color;
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
}
