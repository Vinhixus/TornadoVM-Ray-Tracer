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

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Represent a cube in a 3D scene using its position, scale and color
 */
public class Cube extends Body {

    private final float scale;
    private final Vector3f min;
    private final Vector3f max;

    /**
     * Constructs a Cube object given its position, scale and color.
     * Reflectivity will be set to the default Body value
     *
     * @param position the position
     * @param scale    the scale
     * @param color    the color
     */
    public Cube(Vector3f position, float scale, Color color) {
        super(position, color);
        this.scale = scale;
        this.max = position.add(scale * 0.5F);
        this.min = position.subtract(scale * 0.5F);
    }

    /**
     * Constructs a Cube object given its position, scale and color and reflectivity
     *
     * @param position     the position
     * @param scale        the scale
     * @param color        the color
     * @param reflectivity the reflectivity
     */
    public Cube(Vector3f position, float scale, Color color, float reflectivity) {
        super(position, color, reflectivity);
        this.scale = scale;
        this.max = position.add(scale * 0.5F);
        this.min = position.subtract(scale * 0.5F);
    }

    /**
     * Get the cube's scale
     *
     * @return the cube's scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Given a ray, return the point where the ray first intersects with the cube
     * https://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms
     *
     * @param ray the ray
     * @return The first point of intersection as Vector3f or null if the ray does
     * not intersect with this cube
     */
    @Override
    public Vector3f getIntersection(Ray ray) {

        float dX = 1.0F / ray.getDirection().getX();
        float dY = 1.0F / ray.getDirection().getY();
        float dZ = 1.0F / ray.getDirection().getZ();

        float t1 = (min.getX() - ray.getOrigin().getX()) * dX;
        float t2 = (max.getX() - ray.getOrigin().getX()) * dX;
        float t3 = (min.getY() - ray.getOrigin().getY()) * dY;
        float t4 = (max.getY() - ray.getOrigin().getY()) * dY;
        float t5 = (min.getZ() - ray.getOrigin().getZ()) * dZ;
        float t6 = (max.getZ() - ray.getOrigin().getZ()) * dZ;

        float tMin = max(max(min(t1, t2), min(t3, t4)), min(t5, t6));
        float tMax = min(min(max(t1, t2), max(t3, t4)), max(t5, t6));

        if (tMax < 0 || tMin > tMax)
            return null;

        if (tMin < 0)
            return ray.getOrigin().add(ray.getDirection().multiply(tMax));
        else
            return ray.getOrigin().add(ray.getDirection().multiply(tMin));
    }

    /**
     * Get the normal vector of the cube at a given point
     * https://stackoverflow.com/questions/16875946/ray-box-intersection-normal
     *
     * @param point the point
     * @return the normal vector at the given point
     */
    @Override
    public Vector3f getNormalAt(Vector3f point) {

        Vector3f normal = new Vector3f(0, 1, 0);
        Vector3f localPoint = point.subtract(position);

        float min = Float.MAX_VALUE;
        float distance = Math.abs(scale - Math.abs(localPoint.getX()));

        if (distance < min) {
            min = distance;
            normal = new Vector3f(1, 0, 0).multiply(Math.signum(localPoint.getX()));
        }

        distance = Math.abs(scale - Math.abs(localPoint.getY()));
        if (distance < min) {
            min = distance;
            normal = new Vector3f(0, 1, 0).multiply(Math.signum(localPoint.getY()));
        }

        distance = Math.abs(scale - Math.abs(localPoint.getZ()));
        if (distance < min)
            normal = new Vector3f(0, 0, 1).multiply(Math.signum(localPoint.getZ()));

        return normal;
    }
}
