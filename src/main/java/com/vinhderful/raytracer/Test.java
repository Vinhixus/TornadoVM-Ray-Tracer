package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Test {

    // ==============================================================
    public static final Float4 worldBGColor = Color.BLACK;
    // ==============================================================
    public static final int NUM_BODIES = 3;

    public static final VectorFloat4 bodyPositions = new VectorFloat4(NUM_BODIES);
    public static final VectorFloat bodyRadii = new VectorFloat(NUM_BODIES);
    public static final VectorFloat4 bodyColors = new VectorFloat4(NUM_BODIES);
    // ==============================================================
    public static float[] cameraPosition = {0, 0, -4F};
    public static float[] cameraPitch = {0};
    public static float[] cameraFOV = {60};
    public static float[] cameraYaw = {0};
    // ==============================================================

    // ==============================================================
    public static void main(String[] args) {

        bodyPositions.set(0, new Float4(-1F, 0, 2.5F, 0));
        bodyRadii.set(0, 0.4F);
        bodyColors.set(0, Color.RED);

        bodyPositions.set(1, new Float4(0, 0, 2.5F, 0));
        bodyRadii.set(1, 0.4F);
        bodyColors.set(1, Color.GREEN);

        bodyPositions.set(2, new Float4(1F, 0, 2.5F, 0));
        bodyRadii.set(2, 0.4F);
        bodyColors.set(2, Color.BLUE);

        int width = 1920;
        int height = 1080;

        int[] pixels = new int[width * height];

        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(cameraPosition, cameraYaw, cameraPitch, cameraFOV);
        ts.task("t0", Renderer::render, width, height, pixels,
                cameraPosition, cameraYaw, cameraPitch, cameraFOV,
                bodyPositions, bodyRadii, bodyColors, worldBGColor);
        ts.streamOut(pixels);

        // ==============================================================
        // Run computation in parallel
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test with TornadoVM...");
        long startTime = System.nanoTime();
        ts.execute();
        long endTime = System.nanoTime();
        System.out.println("Duration: " + (endTime - startTime) / 1000000.0 + " ms");

        // ==============================================================
        // Run computation sequentially
        // ==============================================================
        System.out.println("-----------------------------------------");
        System.out.println("Running test sequentially...");
        startTime = System.nanoTime();

        Renderer.render(width, height, pixels,
                cameraPosition, cameraYaw, cameraPitch, cameraFOV,
                bodyPositions, bodyRadii, bodyColors, worldBGColor);

        endTime = System.nanoTime();
        System.out.println("Duration: " + (endTime - startTime) / 1000000.0 + " ms");
    }
}