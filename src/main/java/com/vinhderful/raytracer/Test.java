package com.vinhderful.raytracer;

import com.vinhderful.raytracer.misc.World;
import com.vinhderful.raytracer.renderer.Renderer;
import uk.ac.manchester.tornado.api.GridScheduler;
import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.WorkerGrid;
import uk.ac.manchester.tornado.api.WorkerGrid2D;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.collections.types.VectorInt;

@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Test {

    public static final int TEST_LOOP_ITERATIONS = 10;

    private static int[] pixels;
    private static float[] camera;
    private static int[] dimensions;
    private static int[] pathTracingProperties;
    // ==============================================================

    private static void setRenderingProperties() {
        int width = 1920;
        int height = 960;

        dimensions = new int[]{width, height};
        pixels = new int[width * height];

        camera = new float[]{0, 0, -4F, 0, 0, 60};
        pathTracingProperties = new int[]{18, 4};
    }

    // ==============================================================
    public static void main(String[] args) {

        setRenderingProperties();

        World world = new World();
        Float4 worldBGColor = world.getBackgroundColor();
        VectorInt bodyTypes = world.getBodyTypes();
        VectorFloat4 bodyPositions = world.getBodyPositions();
        VectorFloat bodySizes = world.getBodySizes();
        VectorFloat4 bodyColors = world.getBodyColors();
        VectorFloat bodyReflectivities = world.getBodyReflectivities();

        // ==============================================================
        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(bodyPositions, camera, pathTracingProperties);
        ts.task("t0", Renderer::render, pixels, dimensions, camera,
                bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                worldBGColor, pathTracingProperties);
        ts.streamOut(pixels);

        WorkerGrid worker = new WorkerGrid2D(dimensions[0], dimensions[1]);
        worker.setLocalWork(16, 16, 1);
        GridScheduler grid = new GridScheduler();
        grid.setWorkerGrid("s0.t0", worker);
        ts.execute(grid);

        // ==============================================================
        // Run computation in parallel
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test with TornadoVM...");
        long startTime = System.nanoTime();

        for (int i = 0; i < TEST_LOOP_ITERATIONS; i++)
            ts.execute(grid);

        long endTime = System.nanoTime();
        double tornadoTime = (endTime - startTime) / 1000000.0;
        System.out.println("Duration: " + tornadoTime + " ms");

        // ==============================================================
        // Run computation sequentially
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test sequentially...");
        startTime = System.nanoTime();

        for (int i = 0; i < TEST_LOOP_ITERATIONS; i++)
            Renderer.render(pixels, dimensions, camera,
                    bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                    worldBGColor, pathTracingProperties);

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