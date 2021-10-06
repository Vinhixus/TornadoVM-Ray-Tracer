package com.vinhderful.raytracer.shapes;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

public class Plane extends Shape {

    public Plane(float height) {
        super(new Vector3f(0, height, 0));
    }

    @Override
    public Vector3f getIntersection(Ray ray) {
        float t = -(ray.getOrigin().getY() - position.getY()) / ray.getDirection().getY();
        if (t > 0 && Float.isFinite(t))
            return ray.getOrigin().add(ray.getDirection().multiply(t));

        return null;
    }

    @Override
    public Vector3f getNormalAt(Vector3f point) {
        return new Vector3f(0F, 1F, 0F);
    }

    @Override
    public Color getColor(Vector3f point) {
        if ((point.getX() > 0 & point.getZ() > 0) || (point.getX() < 0 & point.getZ() < 0)) {
            if ((int) point.getX() % 2 == 0 ^ (int) point.getZ() % 2 != 0)
                return Color.GRAY;
            else
                return Color.DARK_GRAY;
        } else {
            if ((int) point.getX() % 2 == 0 ^ (int) point.getZ() % 2 != 0)
                return Color.DARK_GRAY;
            else
                return Color.GRAY;
        }
    }
}
