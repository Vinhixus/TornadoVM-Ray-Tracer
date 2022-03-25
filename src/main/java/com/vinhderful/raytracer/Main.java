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
package com.vinhderful.raytracer;

import com.vinhderful.raytracer.bodies.Cube;
import com.vinhderful.raytracer.bodies.Sphere;
import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.scene.World;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

/**
 * Java-based Ray Tracer
 */
public class Main {

    public static void main(String[] args) {

        // Build world
        World world = new World();

        Sphere sphereWhite = new Sphere(new Vector3f(-3F, -0.5F, 0), 0.5F, Color.WHITE, 8F);
        Sphere sphereRed = new Sphere(new Vector3f(-1.5F, -0.5F, 0), 0.5F, Color.RED, 16F);
        Sphere sphereGreen = new Sphere(new Vector3f(0, -0.5F, 0), 0.5F, Color.GREEN, 24F);
        Sphere sphereBlue = new Sphere(new Vector3f(1.5F, -0.5F, 0), 0.5F, Color.BLUE, 32F);
        Sphere sphereBlack = new Sphere(new Vector3f(3F, -0.5F, 0), 0.5F, Color.BLACK, 48F);
        world.addBody(sphereWhite);
        world.addBody(sphereRed);
        world.addBody(sphereGreen);
        world.addBody(sphereBlue);
        world.addBody(sphereBlack);

        Cube cube = new Cube(new Vector3f(1.5F, 0, 2.5F), 2F, new Color(0.35F, 0.35F, 0.35F), 48F);
        world.addBody(cube);

        // Dimensions of output
        int width = 1920;
        int height = 1080;

        // Ray tracing properties
        int shadowSampleSize = 250;
        int reflectionBounceLimit = 5;

        // Render world
        System.out.println("---------------------------------------");
        System.out.println("Rendering scene...");

        Renderer renderer = new Renderer(width, height, world, shadowSampleSize, reflectionBounceLimit);
        int[] pixels = renderer.getPixels();

        System.out.println("Scene rendered.");
        System.out.println("---------------------------------------");

        // Write to image
        ColorModel cm = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
        WritableRaster raster = Raster.createPackedRaster(
                new DataBufferInt(pixels, pixels.length), width, height, width,
                new int[]{0xFF0000, 0xFF00, 0xFF},
                null);
        BufferedImage img = new BufferedImage(cm, raster, false, null);

        String name = "Render.png";
        System.out.println("Writing to image'" + name + "'...");
        try {
            ImageIO.write(img, "png", new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Wrote to " + name);

        // Exit
        System.out.println("---------------------------------------");
    }
}