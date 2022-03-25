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

import com.vinhderful.raytracer.bodies.Cube;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Represents a light source using its position and color
 */
public class Light extends Cube {

    /**
     * Construct a Light object using its position and color
     *
     * @param position the position
     * @param color    the color
     */
    public Light(Vector3f position, Color color) {
        super(position, 0.5F, color, 0);
    }

    /**
     * Get the color of the light source
     *
     * @return the color of the light source
     */
    public Color getColor() {
        return color;
    }
}