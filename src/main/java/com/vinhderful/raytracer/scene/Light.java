package com.vinhderful.raytracer.scene;

import com.vinhderful.raytracer.shapes.Sphere;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

public class Light extends Sphere {

    public Light(Vector3f position, Color color) {
        super(position, 0.1F, color);
    }

    public Color getColor() {
        return color;
    }
}