package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.utils.Color;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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

import java.nio.IntBuffer;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.max;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.min;

/**
 * Initialises JavaFX FXML elements together with GUI.fxml, contains driver code
 */
@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Controller {

    private static final double[] frameRates = new double[100];
    // ==============================================================
    private static float[] camera;
    private static int[] dimensions;
    private static int[] softShadowSampleSize;
    private static GridScheduler grid;
    // ==============================================================
    private static Float4 worldBGColor;
    private static Float4 lightPosition;
    private static float[] lightSize;
    private static Float4 lightColor;
    // ==============================================================
    private static VectorInt bodyTypes;
    private static VectorFloat4 bodyPositions;
    private static VectorFloat bodySizes;
    private static VectorFloat4 bodyColors;
    private static VectorFloat bodyReflectivities;
    // ==============================================================
    private static int[] pixels;
    private static GraphicsContext g;
    private static PixelWriter pixelWriter;
    private static WritablePixelFormat<IntBuffer> format;
    // ==============================================================
    private static int index = 0;
    private static long lastUpdate = 0;
    // ==============================================================

    @FXML
    public Label fps;
    public Label debugOutput;

    public Pane pane;
    public Canvas canvas;

    public Slider camX;
    public Slider camY;
    public Slider camZ;
    public Slider camFOV;

    public Slider ssSample;

    public float mouseSensitivity = 0.5F;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    // ==============================================================
    private static void render(int[] dimensions, int[] pixels,
                               PixelWriter pixelWriter, WritablePixelFormat<IntBuffer> format,
                               TaskSchedule ts) {
        ts.execute(grid);
        pixelWriter.setPixels(0, 0, dimensions[0], dimensions[1], format, pixels, 0, dimensions[0]);
    }

    private static double getFPS() {
        double total = 0.0d;
        for (double frameRate : frameRates) total += frameRate;
        return total / frameRates.length;
    }

    private static void setRenderingProperties() {
        int width = (int) g.getCanvas().getWidth();
        int height = (int) g.getCanvas().getHeight();

        dimensions = new int[]{width, height};
        pixels = new int[dimensions[0] * dimensions[1]];

        format = WritablePixelFormat.getIntArgbInstance();
        pixelWriter = g.getPixelWriter();

        camera = new float[]{0, 0, -4F, 0, 0, 60};
        softShadowSampleSize = new int[]{1};
    }

    private static void setWorldProperties() {

        // Background color
        worldBGColor = Color.BLACK;

        // Light source properties
        lightPosition = new Float4(2F, 1.5F, -1.5F, 0);
        lightSize = new float[]{0.3F};
        lightColor = Color.WHITE;
    }

    private static void populateWorld() {

        // Number of bodies
        final int NUM_BODIES = 4;

        bodyTypes = new VectorInt(NUM_BODIES);
        bodyPositions = new VectorFloat4(NUM_BODIES);
        bodySizes = new VectorFloat(NUM_BODIES);
        bodyColors = new VectorFloat4(NUM_BODIES);
        bodyReflectivities = new VectorFloat(NUM_BODIES);

        // Planes
        bodyTypes.set(0, 0);
        bodyPositions.set(0, new Float4(0, -1F, 0, 0));
        bodySizes.set(0, -1F);
        bodyColors.set(0, Color.BLACK);
        bodyReflectivities.set(0, 8F);

        // Spheres
        bodyTypes.set(1, 2);
        bodyPositions.set(1, new Float4(-1.5F, 0, 0, 0));
        bodySizes.set(1, 0.5F);
        bodyColors.set(1, Color.RED);
        bodyReflectivities.set(1, 8F);

        bodyTypes.set(2, 2);
        bodyPositions.set(2, new Float4(0, 0, 0, 0));
        bodySizes.set(2, 0.5F);
        bodyColors.set(2, Color.GREEN);
        bodyReflectivities.set(2, 16F);

        bodyTypes.set(3, 2);
        bodyPositions.set(3, new Float4(1.5F, 0, 0, 0));
        bodySizes.set(3, 0.5F);
        bodyColors.set(3, Color.BLUE);
        bodyReflectivities.set(3, 32F);
    }

    /**
     * Initialise renderer, world, camera and populate with objects
     */
    @FXML
    public void initialize() {

        // ==============================================================
        g = canvas.getGraphicsContext2D();

        setRenderingProperties();
        setWorldProperties();
        populateWorld();

        // ==============================================================
        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(dimensions, camera, softShadowSampleSize);
        ts.task("t0", Renderer::render, dimensions, pixels, camera,
                bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                worldBGColor, lightPosition, lightSize, lightColor, softShadowSampleSize);
        ts.streamOut(pixels);

        WorkerGrid worker = new WorkerGrid2D(dimensions[0], dimensions[1]);
        worker.setLocalWork(16, 16, 1);
        grid = new GridScheduler();
        grid.setWorkerGrid("s0.t0", worker);

        TornadoDevice device = TornadoRuntime.getTornadoRuntime().getDriver(0).getDevice(1);
        ts.mapAllTo(device);

        // ==============================================================
        camX.valueProperty().addListener((observable, oldValue, newValue) -> camera[0] = newValue.floatValue());
        camY.valueProperty().addListener((observable, oldValue, newValue) -> camera[1] = newValue.floatValue());
        camZ.valueProperty().addListener((observable, oldValue, newValue) -> camera[2] = newValue.floatValue());
        camFOV.valueProperty().addListener((observable, oldValue, newValue) -> camera[5] = newValue.floatValue());

        ssSample.valueProperty().addListener((observable, oldValue, newValue) -> softShadowSampleSize[0] = newValue.intValue());

        // ==============================================================
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                render(dimensions, pixels, pixelWriter, format, ts);

                if (lastUpdate > 0) {
                    double frameRate = 1_000_000_000.0 / (now - lastUpdate);
                    index %= frameRates.length;
                    frameRates[index++] = frameRate;
                }

                lastUpdate = now;
                fps.setText(String.format("FPS: %.2f", getFPS()));
            }
        };

        timer.start();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = mouseEvent.getX();
        mousePosY = mouseEvent.getY();

        camera[3] += (mousePosX - mouseOldX) * mouseSensitivity;
        camera[4] = (float) min(90, max(-90, camera[4] + (mousePosY - mouseOldY) * mouseSensitivity));
    }

    public void mousePressed(MouseEvent mouseEvent) {
        mousePosX = mouseEvent.getX();
        mousePosY = mouseEvent.getY();
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
    }
}
