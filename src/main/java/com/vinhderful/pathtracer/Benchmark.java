package com.vinhderful.pathtracer;

import com.vinhderful.pathtracer.misc.World;
import com.vinhderful.pathtracer.renderer.Renderer;
import uk.ac.manchester.tornado.api.*;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntime;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Benchmark class provides a no-GUI application to test the performance of the path tracer when using hardware
 * acceleration with TornadoVM vs running sequentially.
 * The benchmark simply generates a set number of frames and records the time it took.
 */
@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Benchmark {

    // The number of frames to generate
    private static final int FRAMES_TO_GENERATE = 5;

    // Dimensions of the viewport
    private static final int WIDTH = 2048;
    private static final int HEIGHT = 1024;

    // Path tracing properties
    private static final int SHADOW_SAMPLE_SIZE = 200;
    private static final int REFLECTION_BOUNCES = 4;

    // Output and input buffers
    private static int[] pixels;
    private static float[] camera;
    private static int[] dimensions;
    private static int[] pathTracingProperties;

    /**
     * Initialise rendering environment
     */
    private static void setRenderingProperties() {
        dimensions = new int[]{WIDTH, HEIGHT};
        pixels = new int[WIDTH * HEIGHT];

        camera = new float[]{0, 0, -4F, 0, 0, 60};
        pathTracingProperties = new int[]{SHADOW_SAMPLE_SIZE, REFLECTION_BOUNCES};
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
        ts.streamIn(camera, pathTracingProperties, bodyPositions);
        ts.task("t0", Renderer::render, pixels,
                dimensions, camera, pathTracingProperties,
                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                skybox, skyboxDimensions);
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
            Renderer.render(pixels, dimensions, camera, pathTracingProperties,
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