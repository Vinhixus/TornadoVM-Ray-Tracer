package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

public class Light {
    private Vector3f position;
    private Color color;

    public Light(Vector3f position) {
        this.position = position;
        this.color = new Color(1F, 1F, 1F);
    }

    public Light(Vector3f position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}