package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.GridScheduler;
import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.WorkerGrid;
import uk.ac.manchester.tornado.api.WorkerGrid2D;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.collections.types.VectorInt;
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntime;

@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Test {

    public static final int TEST_LOOP_ITERATIONS = 200;

    // ==============================================================
    private static float[] camera;
    private static int[] dimensions;
    private static int[] softShadowSampleSize;
    // ==============================================================
    private static Float4 worldBGColor;
    // ==============================================================
    private static VectorInt bodyTypes;
    private static VectorFloat4 bodyPositions;
    private static VectorFloat bodySizes;
    private static VectorFloat4 bodyColors;
    private static VectorFloat bodyReflectivities;
    // ==============================================================
    private static int[] pixels;

    private static void setRenderingProperties() {
        int width = 512;
        int height = 1024;

        dimensions = new int[]{width, height};
        pixels = new int[dimensions[0] * dimensions[1]];

        camera = new float[]{0, 0, -4F, 0, 0, 60};
        softShadowSampleSize = new int[]{1};
    }

    private static void setWorldProperties() {

        // Background color
        worldBGColor = Color.BLACK;
    }

    private static void populateWorld() {

        // Number of bodies
        final int NUM_BODIES = 7;

        bodyTypes = new VectorInt(NUM_BODIES);
        bodyPositions = new VectorFloat4(NUM_BODIES);
        bodySizes = new VectorFloat(NUM_BODIES);
        bodyColors = new VectorFloat4(NUM_BODIES);
        bodyReflectivities = new VectorFloat(NUM_BODIES);

        // Light
        bodyTypes.set(0, 0);
        bodyPositions.set(0, new Float4(1F, 1.5F, -1.5F, 0));
        bodySizes.set(0, 0.4F);
        bodyColors.set(0, Color.WHITE);
        bodyReflectivities.set(0, 0);

        // Planes
        bodyTypes.set(1, 1);
        bodyPositions.set(1, new Float4(0, -0.5F, 0, 0));
        bodySizes.set(1, -1F);
        bodyColors.set(1, Color.BLACK);
        bodyReflectivities.set(1, 16F);

        // Spheres
        bodyTypes.set(2, 2);
        bodyPositions.set(2, new Float4(-3F, 0, 0, 0));
        bodySizes.set(2, 0.5F);
        bodyColors.set(2, Color.WHITE);
        bodyReflectivities.set(2, 4F);

        bodyTypes.set(3, 2);
        bodyPositions.set(3, new Float4(-1.5F, 0, 0, 0));
        bodySizes.set(3, 0.5F);
        bodyColors.set(3, Color.RED);
        bodyReflectivities.set(3, 8F);

        bodyTypes.set(4, 2);
        bodyPositions.set(4, new Float4(0, 0, 0, 0));
        bodySizes.set(4, 0.5F);
        bodyColors.set(4, Color.GREEN);
        bodyReflectivities.set(4, 16F);

        bodyTypes.set(5, 2);
        bodyPositions.set(5, new Float4(1.5F, 0, 0, 0));
        bodySizes.set(5, 0.5F);
        bodyColors.set(5, Color.BLUE);
        bodyReflectivities.set(5, 32F);

        bodyTypes.set(6, 2);
        bodyPositions.set(6, new Float4(3F, 0, 0, 0));
        bodySizes.set(6, 0.5F);
        bodyColors.set(6, Color.BLACK);
        bodyReflectivities.set(6, 64F);
    }

    // ==============================================================
    public static void main(String[] args) {

        setRenderingProperties();
        setWorldProperties();
        populateWorld();

        // ==============================================================
        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(dimensions, camera, softShadowSampleSize);
        ts.task("t0", Renderer::render, dimensions, pixels, camera,
                bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                worldBGColor, softShadowSampleSize);
        ts.streamOut(pixels);

        WorkerGrid worker = new WorkerGrid2D(dimensions[0], dimensions[1]);
        worker.setLocalWork(16, 16, 1);
        GridScheduler grid = new GridScheduler();
        grid.setWorkerGrid("s0.t0", worker);

        TornadoDevice device = TornadoRuntime.getTornadoRuntime().getDriver(0).getDevice(1);
        ts.mapAllTo(device);

        // ==============================================================
        // Run computation in parallel
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test with TornadoVM...");
        long startTime = System.nanoTime();

        for (int i = 0; i < TEST_LOOP_ITERATIONS; i++)
            ts.execute(grid);

        long endTime = System.nanoTime();
        System.out.println("Duration: " + (endTime - startTime) / 1000000.0 + " ms");

        // ==============================================================
        // Run computation sequentially
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test sequentially...");
        startTime = System.nanoTime();

        for (int i = 0; i < TEST_LOOP_ITERATIONS; i++)
            Renderer.render(dimensions, pixels, camera,
                    bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                    worldBGColor, softShadowSampleSize);

        endTime = System.nanoTime();
        System.out.println("Duration: " + (endTime - startTime) / 1000000.0 + " ms");
    }
}