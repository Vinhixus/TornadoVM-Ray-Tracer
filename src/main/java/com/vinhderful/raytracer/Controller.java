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
import javafx.scene.layout.Pane;
import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import java.nio.IntBuffer;

/**
 * Initialises JavaFX FXML elements together with GUI.fxml, contains driver code
 */
@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Controller {


    // ==============================================================
    public static final Float4 worldBGColor = Color.BLACK;
    public static final Float4 lightPosition = new Float4(-1F, 0.8F, -1F, 0);
    public static final Float4 lightColor = new Float4(1F, 1F, 1F, 0);
    // ==============================================================
    public static final int NUM_BODIES = 3;

    public static final VectorFloat4 bodyPositions = new VectorFloat4(NUM_BODIES);
    public static final VectorFloat bodyRadii = new VectorFloat(NUM_BODIES);
    public static final VectorFloat4 bodyColors = new VectorFloat4(NUM_BODIES);
    public static final VectorFloat bodyReflectivities = new VectorFloat(NUM_BODIES);
    private static final double[] frameRates = new double[100];
    // ==============================================================
    public static float[] cameraPosition = {0, 0, -4F};
    public static float[] cameraPitch = {0};
    public static float[] cameraFOV = {60};
    public static float[] cameraYaw = {0};
    // ==============================================================
    private static int index = 0;
    private static long lastUpdate = 0;
    // ==============================================================

    @FXML
    public Label fps;
    public Pane pane;
    public Canvas canvas;
    public Slider camX;
    public Slider camY;
    public Slider camZ;
    public Slider camYaw;
    public Slider camPitch;
    public Slider camFOV;

    // ==============================================================
    public static void render(int width, int height, int[] pixels,
                              PixelWriter pixelWriter, WritablePixelFormat<IntBuffer> format,
                              TaskSchedule ts) {
        ts.execute();
        pixelWriter.setPixels(0, 0, width, height, format, pixels, 0, width);
    }

    public static double getFPS() {
        double total = 0.0d;
        for (double frameRate : frameRates) total += frameRate;
        return total / frameRates.length;
    }

    /**
     * Initialise renderer, world, camera and populate with objects
     */
    @FXML
    public void initialize() {

        GraphicsContext g = canvas.getGraphicsContext2D();
        PixelWriter pixelWriter = g.getPixelWriter();
        int width = (int) g.getCanvas().getWidth();
        int height = (int) g.getCanvas().getHeight();

        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
        int[] pixels = new int[width * height];

        // ==============================================================
        bodyPositions.set(0, new Float4(-1.5F, 0, 0, 0));
        bodyRadii.set(0, 0.5F);
        bodyColors.set(0, Color.RED);
        bodyReflectivities.set(0, 8F);

        bodyPositions.set(1, new Float4(0, 0, 0, 0));
        bodyRadii.set(1, 0.5F);
        bodyColors.set(1, Color.GREEN);
        bodyReflectivities.set(1, 16F);

        bodyPositions.set(2, new Float4(1.5F, 0, 0, 0));
        bodyRadii.set(2, 0.5F);
        bodyColors.set(2, Color.BLUE);
        bodyReflectivities.set(2, 32F);

        // ==============================================================
        TaskSchedule ts = new TaskSchedule("s0");
        ts.streamIn(cameraPosition, cameraYaw, cameraPitch, cameraFOV);
        ts.task("t0", Renderer::render, width, height, pixels,
                cameraPosition, cameraYaw, cameraPitch, cameraFOV,
                bodyPositions, bodyRadii, bodyColors, bodyReflectivities,
                worldBGColor, lightPosition, lightColor);
        ts.streamOut(pixels);

        // ==============================================================
        camX.valueProperty().addListener((observable, oldValue, newValue) -> cameraPosition[0] = newValue.floatValue());
        camY.valueProperty().addListener((observable, oldValue, newValue) -> cameraPosition[1] = newValue.floatValue());
        camZ.valueProperty().addListener((observable, oldValue, newValue) -> cameraPosition[2] = newValue.floatValue());
        camYaw.valueProperty().addListener((observable, oldValue, newValue) -> cameraYaw[0] = newValue.floatValue());
        camPitch.valueProperty().addListener((observable, oldValue, newValue) -> cameraPitch[0] = newValue.floatValue());
        camFOV.valueProperty().addListener((observable, oldValue, newValue) -> cameraFOV[0] = newValue.floatValue());

        // ==============================================================
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                render(width, height, pixels, pixelWriter, format, ts);

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
}
