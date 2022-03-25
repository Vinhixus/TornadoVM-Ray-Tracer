/*
 * This file is part of Tornado-Ray-Tracer: A Java-based ray tracer running on TornadoVM.
 * URL: https://github.com/Vinhixus/TornadoVM-Ray-Tracer
 *
 * Copyright (c) 2021-2022, Vinh Pham Van
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
     * Construct a default Camera object.
     * Position defaults to {0, 0.4, -6},
     * yaw and pitch default to 0 and 5,
     * field of view defaults to 50
     */
    public Camera() {
        this.position = new Vector3f(0, 0.4F, -6F);
        this.yaw = 0;
        this.pitch = 5;
        this.fieldOfVision = 50;
    }

    /**
     * Set x attribute of the position to a given x value
     *
     * @param x x value to set position.x to
     */
    public void setX(float x) {
        position.setX(x);
    }

    /**
     * Set y attribute of the position to a given y value
     *
     * @param y y value to set position.y to
     */
    public void setY(float y) {
        position.setY(y);
    }

    /**
     * Set z attribute of the position to a given z value
     *
     * @param z z value to set position.z to
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
     * @param yaw the yaw value to set the camera's yaw to
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
     * @param pitch the pitch value to set the camera's pitch to
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
     * @param fieldOfVision the FOV value to set the camera's field of vision to
     */
    public void setFOV(float fieldOfVision) {
        this.fieldOfVision = fieldOfVision;
    }
}