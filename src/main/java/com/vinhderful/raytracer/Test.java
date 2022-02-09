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
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntime;

@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Test {

    public static final int TEST_LOOP_ITERATIONS = 200;
    // ==============================================================
    public static final Float4 worldBGColor = Color.BLACK;
    public static final Float4 lightPosition = new Float4(-1F, 0.8F, -1F, 0);
    public static final Float4 lightColor = new Float4(1F, 1F, 1F, 0);
    // ==============================================================
    public static final int NUM_BODIES = 4;
    public static final VectorFloat4 bodyPositions = new VectorFloat4(NUM_BODIES);
    public static final VectorFloat bodyRadii = new VectorFloat(NUM_BODIES);
    public static final VectorFloat4 bodyColors = new VectorFloat4(NUM_BODIES);
    public static final VectorFloat bodyReflectivities = new VectorFloat(NUM_BODIES);
    // ==============================================================
    private static final float[] camera = {0, 0, -4F, 0, 0, 60};
    private static final int[] dimensions = new int[2];
    private static final int[] softShadowSampleSize = {1};
    // ==============================================================
    public static void main(String[] args) {

        // Plane
        bodyPositions.set(0, new Float4(0, -1F, 0, 0));
        bodyRadii.set(0, -1F);
        bodyColors.set(0, Color.BLACK);
        bodyReflectivities.set(0, 8F);

        // Spheres
        bodyPositions.set(1, new Float4(-1F, 0, 0, 0));
        bodyRadii.set(1, 0.3F);
        bodyColors.set(1, Color.RED);
        bodyReflectivities.set(1, 8F);

        bodyPositions.set(2, new Float4(0, 0, 0, 0));
        bodyRadii.set(2, 0.3F);
        bodyColors.set(2, Color.GREEN);
        bodyReflectivities.set(2, 16F);

        bodyPositions.set(3, new Float4(1F, 0, 0, 0));
        bodyRadii.set(3, 0.3F);
        bodyColors.set(3, Color.BLUE);
        bodyReflectivities.set(3, 32F);

        // ==============================================================
        dimensions[0] = 1024;
        dimensions[1] = 512;
        int[] pixels = new int[dimensions[0] * dimensions[1]];

        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(dimensions, camera, softShadowSampleSize);
        ts.task("t0", Renderer::render, dimensions, pixels, camera,
                bodyPositions, bodyRadii, bodyColors, bodyReflectivities,
                worldBGColor, lightPosition, lightColor, softShadowSampleSize);
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
                    bodyPositions, bodyRadii, bodyColors, bodyReflectivities,
                    worldBGColor, lightPosition, lightColor, softShadowSampleSize);

        endTime = System.nanoTime();
        System.out.println("Duration: " + (endTime - startTime) / 1000000.0 + " ms");
    }
}