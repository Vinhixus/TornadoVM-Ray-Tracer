package com.vinhderful.raytracer.scene;

import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Represents the camera/eye looking into the scene using position, yaw, pitch
 * and FOV
 */
public class Camera {

    private final Vector3f position;
    private float yaw;
    private float pitch;
    private float fieldOfVision;

    /**
     * Construct a Camera object. Position defaults to {0, 0, 0}, yaw and pitch
     * defaults to 0, FOV defaults to 60
     */
    public Camera() {
        this.position = new Vector3f(0, 0, -4F);
        this.yaw = 0;
        this.pitch = 0;
        this.fieldOfVision = 60;
    }

    /**
     * Set x attribute of the position to a given x value
     *
     * @param x
     *            x value to set position.x to
     */
    public void setX(float x) {
        position.setX(x);
    }

    /**
     * Set y attribute of the position to a given y value
     *
     * @param y
     *            y value to set position.y to
     */
    public void setY(float y) {
        position.setY(y);
    }

    /**
     * Set z attribute of the position to a given z value
     *
     * @param z
     *            z value to set position.z to
     */
    public void setZ(float z) {
        position.setZ(z);
    }

    /**
     * Get the camera's position
     * 
     * @return the camera's position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Get the camera's yaw
     * 
     * @return the camera's yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Set the camera's yaw
     *
     * @param yaw
     *            the yaw value to set the camera's yaw to
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Get the camera's pitch
     *
     * @return the camera's pitch
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Set the camera's pitch
     *
     * @param pitch
     *            the pitch value to set the camera's pitch to
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Get the camera's field of vision
     *
     * @return the camera's field of vision
     */
    public float getFOV() {
        return fieldOfVision;
    }

    /**
     * Set the camera's field of vision
     *
     * @param fieldOfVision
     *            the FOV value to set the camera's field of vision to
     */
    public void setFOV(float fieldOfVision) {
        this.fieldOfVision = fieldOfVision;
    }
}