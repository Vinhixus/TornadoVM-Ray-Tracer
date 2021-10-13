package com.vinhderful.raytracer.scene;

import com.vinhderful.raytracer.utils.Vector3f;

public class Camera {
    private final Vector3f position;
    private float yaw;
    private float pitch;
    private float fieldOfVision;

    public Camera() {
        this.position = new Vector3f(0, 0, -4F);
        this.yaw = 0;
        this.pitch = 0;
        this.fieldOfVision = 60;
    }

    public void setX(float x) {
        position.setX(x);
    }

    public void setY(float y) {
        position.setY(y);
    }

    public void setZ(float z) {
        position.setZ(z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getFOV() {
        return fieldOfVision;
    }

    public void setFOV(float fieldOfVision) {
        this.fieldOfVision = fieldOfVision;
    }
}