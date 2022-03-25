/*
 * This file is part of Tornado-Ray-Tracer: A Java-based ray tracer running on TornadoVM.
 * URL: https://github.com/Vinhixus/TornadoVM-Ray-Tracer
 *
 * Copyright [2022] [Vinh Pham Van]
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
package com.vinhderful.raytracer.utils;

import com.vinhderful.raytracer.bodies.Body;

/**
 * Represents an event of a ray hitting a body, storing the ray, the body and
 * the hit position
 */
public class Hit {

    private final Body body;
    private final Ray ray;
    private final Vector3f position;

    /**
     * Construct a hit event using a body, ray and hit position
     *
     * @param body     the body being hit
     * @param ray      the ray hitting the body
     * @param position the position of the hit
     */
    public Hit(Body body, Ray ray, Vector3f position) {
        this.body = body;
        this.ray = ray;
        this.position = position;
    }

    /**
     * Get the body being hit
     *
     * @return the body being hit
     */
    public Body getBody() {
        return body;
    }

    /**
     * Get the ray hitting the body
     *
     * @return the ray hitting the body
     */
    public Ray getRay() {
        return ray;
    }

    /**
     * Get the position of the hit
     *
     * @return the position of the hit
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Get the color of the body at the hit position
     *
     * @return the color of the body at the hit position
     */
    public Color getColor() {
        return body.getColor(position);
    }

    /**
     * Get the normal vector from the body at the hit position
     *
     * @return the normal vector from the body at the hit position
     */
    public Vector3f getNormal() {
        return body.getNormalAt(position);
    }
}
