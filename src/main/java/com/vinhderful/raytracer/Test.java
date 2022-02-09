package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Test {

    public static final int TEST_LOOP_ITERATIONS = 200;

    // ==============================================================
    public static final Float4 worldBGColor = Color.BLACK;
    public static final Float4 lightPosition = new Float4(-1F, 0.8F, -1F, 0);
    public static final Float4 lightColor = new Float4(1F, 1F, 1F, 0);
    // ==============================================================
    public static final int NUM_BODIES = 6;

    public static final VectorFloat4 bodyPositions = new VectorFloat4(NUM_BODIES);
    public static final VectorFloat bodyRadii = new VectorFloat(NUM_BODIES);
    public static final VectorFloat4 bodyColors = new VectorFloat4(NUM_BODIES);
    public static final VectorFloat bodyReflectivities = new VectorFloat(NUM_BODIES);
    // ==============================================================
    public static float[] cameraPosition = {0, 0, -4F};
    public static float[] cameraPitch = {0};
    public static float[] cameraFOV = {60};
    public static float[] cameraYaw = {0};

    // ==============================================================
    public static void main(String[] args) {

        // Plane
        bodyPositions.set(0, new Float4(0, -1F, 0, 0));
        bodyRadii.set(0, -1F);
        bodyColors.set(0, Color.BLACK);
        bodyReflectivities.set(0, 8F);

        // Spheres
        bodyPositions.set(1, new Float4(-2F, 0, 0, 0));
        bodyRadii.set(1, 0.3F);
        bodyColors.set(1, Color.WHITE);
        bodyReflectivities.set(1, 4F);

        bodyPositions.set(2, new Float4(-1F, 0, 0, 0));
        bodyRadii.set(2, 0.3F);
        bodyColors.set(2, Color.RED);
        bodyReflectivities.set(2, 8F);

        bodyPositions.set(3, new Float4(0, 0, 0, 0));
        bodyRadii.set(3, 0.3F);
        bodyColors.set(3, Color.GREEN);
        bodyReflectivities.set(3, 16F);

        bodyPositions.set(4, new Float4(1F, 0, 0, 0));
        bodyRadii.set(4, 0.3F);
        bodyColors.set(4, Color.BLUE);
        bodyReflectivities.set(4, 32F);

        bodyPositions.set(5, new Float4(2F, 0, 0, 0));
        bodyRadii.set(5, 0.3F);
        bodyColors.set(5, Color.BLACK);
        bodyReflectivities.set(5, 64F);

        int width = 1920;
        int height = 1080;

        int[] pixels = new int[width * height];
        int[] softShadowSampleSize = {12};

        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(cameraPosition, cameraYaw, cameraPitch, cameraFOV, softShadowSampleSize);
        ts.task("t0", Renderer::render, width, height, pixels,
                cameraPosition, cameraYaw, cameraPitch, cameraFOV,
                bodyPositions, bodyRadii, bodyColors, bodyReflectivities,
                worldBGColor, lightPosition, lightColor, softShadowSampleSize);
        ts.streamOut(pixels);

        // ==============================================================
        // Run computation in parallel
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test with TornadoVM...");
        long startTime = System.nanoTime();

        for (int i = 0; i < TEST_LOOP_ITERATIONS; i++)
            ts.execute();

        long endTime = System.nanoTime();
        System.out.println("Duration: " + (endTime - startTime) / 1000000.0 + " ms");

        // ==============================================================
        // Run computation sequentially
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test sequentially...");
        startTime = System.nanoTime();

        for (int i = 0; i < TEST_LOOP_ITERATIONS; i++)
            Renderer.render(width, height, pixels,
                    cameraPosition, cameraYaw, cameraPitch, cameraFOV,
                    bodyPositions, bodyRadii, bodyColors, bodyReflectivities,
                    worldBGColor, lightPosition, lightColor, softShadowSampleSize);

        endTime = System.nanoTime();
        System.out.println("Duration: " + (endTime - startTime) / 1000000.0 + " ms");
    }
}