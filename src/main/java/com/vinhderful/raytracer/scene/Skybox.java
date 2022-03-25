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

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a spherical skybox initialised by image
 */
public class Skybox {

    private BufferedImage sphereImage;

    /**
     * Read the given resource into a BufferedImage
     *
     * @param resourceName the ray to the resource
     */
    public Skybox(String resourceName) {

        sphereImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        try {
            System.out.println("Loading skybox image '" + resourceName + "'...");
            sphereImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the color of the skybox at a certain point on the surface
     * given by the direction pointing from the origin of the sphere to the surface point
     *
     * @param d the direction
     * @return the color of the skybox at the specified point
     */
    public Color getColor(Vector3f d) {

        // https://en.wikipedia.org/wiki/UV_mapping#Finding_UV_on_a_sphere
        float u = (float) (0.5 + Math.atan2(d.getZ(), d.getX()) / (2 * Math.PI));
        float v = (float) (0.5 - Math.asin(d.getY()) / Math.PI);

        int x = (int) (u * (sphereImage.getWidth() - 1));
        int y = (int) (v * (sphereImage.getHeight() - 1));

        return Color.fromInt(sphereImage.getRGB(x, y));
    }
}
