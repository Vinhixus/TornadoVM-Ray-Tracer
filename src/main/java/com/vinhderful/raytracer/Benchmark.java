package com.vinhderful.raytracer;

import com.vinhderful.raytracer.misc.World;
import com.vinhderful.raytracer.renderer.Renderer;
import uk.ac.manchester.tornado.api.*;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntime;

import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Benchmark {

    public static final int FRAMES_TO_GENERATE = 5;

    public static final int WIDTH = 2048;
    public static final int HEIGHT = 1024;
    public static final int SHADOW_SAMPLE_SIZE = 200;
    public static final int REFLECTION_BOUNCES = 4;

    private static int[] pixels;
    private static float[] camera;
    private static int[] dimensions;
    private static int[] pathTracingProperties;
    // ==============================================================

    private static void setRenderingProperties() {
        dimensions = new int[]{WIDTH, HEIGHT};
        pixels = new int[WIDTH * HEIGHT];

        camera = new float[]{0, 0, -4F, 0, 0, 60};
        pathTracingProperties = new int[]{SHADOW_SAMPLE_SIZE, REFLECTION_BOUNCES};
    }

    // ==============================================================
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

        // ==============================================================
        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(bodyPositions, camera, pathTracingProperties);
        ts.task("t0", Renderer::render, pixels, dimensions, camera,
                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                skybox, skyboxDimensions, pathTracingProperties);
        ts.streamOut(pixels);

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

            for (int j = 0; j < numDevices; j++) {
                TornadoDevice device = driver.getDevice(j);
                devices.add(device);

                String listingName = "(" + driver.getName() + ") " + device.getPhysicalDevice().getDeviceName();
                System.out.println(deviceCount + ": " + listingName);

                ts.mapAllTo(device);
                ts.execute(grid);
                deviceCount++;
            }
        }

        // Choose device
        System.out.println("-----------------------------------------");
        Scanner scanner = new Scanner(System.in);
        String input;

        do {
            System.out.print("Select device index: ");
            input = scanner.nextLine();

            if (!input.matches("^-?\\d+$"))
                System.out.println("Please enter an integer!");
            else if (Integer.parseInt(input) < 0 || Integer.parseInt(input) > deviceCount)
                System.out.println("Please enter a valid index listed above!");
            else
                break;
        } while (true);

        ts.mapAllTo(devices.get(Integer.parseInt(input)));

        // ==============================================================
        // Run computation in parallel
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Generating " + FRAMES_TO_GENERATE + " frames with TornadoVM...");
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
            Renderer.render(pixels, dimensions, camera,
                    bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                    skybox, skyboxDimensions, pathTracingProperties);

        endTime = System.nanoTime();
        double sequentialTime = (endTime - startTime) / 1000000.0;
        System.out.println("Duration: " + sequentialTime + " ms");

        // ==============================================================
        // Performance increase
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Performance increase: " + sequentialTime / tornadoTime + "x");
        System.out.println("-----------------------------------------");
    }
}