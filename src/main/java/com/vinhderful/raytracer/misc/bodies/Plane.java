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
package com.vinhderful.raytracer.misc.bodies;

import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;

/**
 * Represents a plane using position, height and reflectivity
 * The plane will have an appearance of a checkerboard
 */
public class Plane extends Body {

    /**
     * Construct a plane given its height, side size and reflectivity
     * Plane parallel to the X-Z plane
     *
     * @param height       height of the plane (Y coordinate)
     * @param size         the size of the side of the plane
     * @param reflectivity reflectivity of the plane
     */
    public Plane(float height, float size, float reflectivity) {
        super(new Float4(0, height, 0, 0), size, Color.GRAY, reflectivity);
    }
}
