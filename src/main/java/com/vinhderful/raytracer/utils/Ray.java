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
package com.vinhderful.raytracer.utils;

/**
 * Represents a ray using its origin and direction
 */
public class Ray {

    private final Vector3f origin;
    private final Vector3f direction;

    /**
     * Construct a Ray object using origin and direction
     *
     * @param origin    the origin point of the ray
     * @param direction the direction of the ray
     */
    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * Get the origin of the ray
     *
     * @return the origin of the ray
     */
    public Vector3f getOrigin() {
        return origin;
    }

    /**
     * Get the direction of the ray
     *
     * @return the direction of the ray
     */
    public Vector3f getDirection() {
        return direction;
    }
}