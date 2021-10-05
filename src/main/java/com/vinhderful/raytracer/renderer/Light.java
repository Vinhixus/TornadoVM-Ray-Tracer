package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.utils.Vector3f;

public class Light {
    private Vector3f position;

    public Light(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}