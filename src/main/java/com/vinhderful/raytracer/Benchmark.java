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

import java.util.ArrayList;

import com.vinhderful.raytracer.misc.World;
import com.vinhderful.raytracer.renderer.Renderer;

import uk.ac.manchester.tornado.api.GridScheduler;
import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.TornadoDriver;
import uk.ac.manchester.tornado.api.TornadoRuntimeCI;
import uk.ac.manchester.tornado.api.WorkerGrid;
import uk.ac.manchester.tornado.api.WorkerGrid2D;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntime;

/**
 * Benchmark class provides a no-GUI application to test the performance of the ray tracer when using hardware
 * acceleration with TornadoVM vs running sequentially.
 * The benchmark simply generates a set number of frames and records the time it took.
 */
@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Benchmark {

    // The number of frames to generate
    private static final int FRAMES_TO_GENERATE = 5;

    // Dimensions of the viewport
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    // Ray tracing properties
    private static final int SHADOW_SAMPLE_SIZE = 200;
    private static final int REFLECTION_BOUNCES = 4;

    // Output and input buffers
    private static int[] pixels;
    private static float[] camera;
    private static int[] dimensions;
    private static int[] rayTracingProperties;

    /**
     * Initialise rendering environment
     */
    private static void setRenderingProperties() {
        dimensions = new int[]{WIDTH, HEIGHT};
        pixels = new int[WIDTH * HEIGHT];

        camera = new float[]{0, 0, -4F, 0, 0, 60};
        rayTracingProperties = new int[]{SHADOW_SAMPLE_SIZE, REFLECTION_BOUNCES};
    }

    /**
     * Main program
     *
     * @param args program arguments
     */
    public static void main(String[] args) throws Exception {

        setRenderingProperties();

        System.out.println("-----------------------------------------");
        System.out.println("Building world...");
        World world = new World();
        VectorFloat4 skybox = world.getSkyboxBuffer();
        int[] skyboxDimensions = world.getSkyboxDimensionsBuffer();
        VectorFloat4 bodyPositions = world.getBodyPositionsBuffer();
        VectorFloat bodySizes = world.getBodySizesBuffer();
        VectorFloat4 bodyColors = world.getBodyColorsBuffer();
        VectorFloat bodyReflectivities = world.getBodyReflectivitiesBuffer();

        // Set up Tornado Task Schedule
        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(camera, rayTracingProperties, bodyPositions);
        ts.task("t0", Renderer::render, pixels,
                dimensions, camera, rayTracingProperties,
                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                skybox, skyboxDimensions);
        ts.lockObjectsInMemory(dimensions, bodySizes, bodyColors, bodyReflectivities, skybox, skyboxDimensions);
        ts.streamOut(pixels);

        // Set up worker grid
        WorkerGrid worker = new WorkerGrid2D(WIDTH, HEIGHT);
        worker.setLocalWork(16, 16, 1);
        GridScheduler grid = new GridScheduler();
        grid.setWorkerGrid("s0.t0", worker);
        ts.execute(grid);

        // Get Tornado devices
        System.out.println("-----------------------------------------");
        System.out.println("Getting Tornado Devices...");
        ArrayList<TornadoDevice> devices = new ArrayList<>();
        TornadoRuntimeCI runtimeCI = TornadoRuntime.getTornadoRuntime();
        int numTornadoDrivers = runtimeCI.getNumDrivers();
        int deviceCount = 0;

        for (int i = 0; i < numTornadoDrivers; i++) {

            TornadoDriver driver = runtimeCI.getDriver(i);
            int numDevices = driver.getDeviceCount();

            // Exclude PTX due to unsupported intrinsic (atan2)
            if (driver.getName().toLowerCase().contains("ptx")) {
                continue;
            }

            for (int j = 0; j < numDevices; j++) {
                TornadoDevice device = driver.getDevice(j);
                devices.add(device);

                String listingName = "(" + driver.getName() + ") " + device.getPhysicalDevice().getDeviceName();
                System.out.println(deviceCount + ": " + listingName);

                deviceCount++;
            }
        }

        // Choose device
        System.out.println("-----------------------------------------");

        for (int deviceId=0; deviceId<devices.size(); deviceId++) {
            TornadoDevice device = devices.get(deviceId);

            ts.mapAllTo(device);
            ts.execute(grid);

            // ==============================================================
            // Run computation in parallel
            // ==============================================================
            System.out.println("-----------------------------------------");
            System.out.println("Generating " + FRAMES_TO_GENERATE + " frames with TornadoVM for device: " + device);
            long startTime = System.nanoTime();

            for (int i = 0; i < FRAMES_TO_GENERATE; i++)
                ts.execute(grid);

            long endTime = System.nanoTime();
            double tornadoTime = (endTime - startTime) / 1000000.0;
            System.out.println("Duration: " + tornadoTime + " ms");

            // ==============================================================
            // Run computation sequentially
            // ==============================================================
            System.out.println("-----------------------------------------");
            System.out.println("Generating " + FRAMES_TO_GENERATE + " frames sequentially...");
            startTime = System.nanoTime();

            for (int i = 0; i < FRAMES_TO_GENERATE; i++)
                Renderer.render(pixels, dimensions, camera, rayTracingProperties,
                        bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                        skybox, skyboxDimensions);

            endTime = System.nanoTime();
            double sequentialTime = (endTime - startTime) / 1000000.0;
            System.out.println("Duration: " + sequentialTime + " ms");

            // ==============================================================
            // Calculate performance increase
            // ==============================================================
            System.out.println("-----------------------------------------");
            System.out.println("Performance increase: " + sequentialTime / tornadoTime + "x");
            System.out.println("-----------------------------------------");
        }
    }
}