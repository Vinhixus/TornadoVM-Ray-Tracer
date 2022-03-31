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
package com.vinhderful.raytracer.bodies;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Abstract class representing a solid body in a 3D space using its position,
 * color and reflectivity
 */
public abstract class Body {

    protected Vector3f position;
    protected Color color;
    protected float reflectivity;

    /**
     * Construct a body using its position. Color defaults to BLACK, reflectivity
     * defaults to 64F
     *
     * @param position the position
     */
    public Body(Vector3f position) {
        this.position = position;
        this.color = Color.BLACK;
        this.reflectivity = 64F;
    }

    /**
     * Construct a body using its position and color. Reflectivity defaults to 64F
     *
     * @param position the position
     * @param color    the color
     */
    public Body(Vector3f position, Color color) {
        this.position = position;
        this.color = color;
        this.reflectivity = 64F;
    }

    /**
     * Construct a body using its position, color and reflectivity
     *
     * @param position     the position
     * @param color        the color
     * @param reflectivity the reflectivity
     */
    public Body(Vector3f position, Color color, float reflectivity) {
        this.position = position;
        this.color = color;
        this.reflectivity = reflectivity;
    }

    /**
     * Get the position of the body
     *
     * @return the position of the body
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Return the color of the body at a given 3D point
     *
     * @param point the 3D point
     * @return the color of the body at the given 3D point
     */
    public Color getColor(Vector3f point) {
        return color;
    }

    /**
     * Get the reflectivity value of the body
     *
     * @return the reflectivity value of the body
     */
    public float getReflectivity() {
        return reflectivity;
    }

    /**
     * Given a ray, return the point where the ray first intersects with the body
     *
     * @param ray the ray
     * @return The first point of intersection as Vector3f or null if the ray does
     * not intersect with this body
     */
    public abstract Vector3f getIntersection(Ray ray);

    /**
     * Get the normal vector of the body at a given point
     *
     * @param point the point
     * @return the normal vector at the given point
     */
    public abstract Vector3f getNormalAt(Vector3f point);
}
