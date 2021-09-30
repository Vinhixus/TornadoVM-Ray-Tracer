package com.vinhderful.raytracer.shapes;

import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.scene.paint.Color;

public class Sphere extends Shape {

    private final float radius;

    public Sphere(Vector3f position, float radius, Color color) {
        super(position, color);
        this.radius = radius;
    }

    @Override
    public Vector3f calculateIntersection(Ray ray) {
        float t = Vector3f.getDotProduct(position.subtract(ray.getOrigin()), ray.getDirection());
        Vector3f p = ray.getOrigin().add(ray.getDirection().multiply(t));

        float y = position.subtract(p).length();
        if (y < radius) {
            float x = (float) Math.sqrt(radius * radius - y * y);
            float t1 = t - x;
            if (t1 > 0)
                return ray.getOrigin().add(ray.getDirection().multiply(t1));
            else
                return null;
        } else {
            return null;
        }
    }
}
