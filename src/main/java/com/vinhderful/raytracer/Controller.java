package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.utils.Color;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import uk.ac.manchester.tornado.api.*;
import uk.ac.manchester.tornado.api.collections.types.*;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntime;

import java.nio.IntBuffer;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

/**
 * Initialises JavaFX FXML elements together with GUI.fxml, contains driver code
 */
@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Controller {

    private static long lastUpdate = 0;
    // ==============================================================
    private static float[] camera;
    private static int[] dimensions;
    private static int[] softShadowSampleSize;
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

    private static GridScheduler grid;
    private static GraphicsContext g;
    private static PixelWriter pixelWriter;
    private static WritablePixelFormat<IntBuffer> format;
    // ==============================================================
    @FXML
    public Label fps;
    public Label debugOutput;
    public Pane pane;
    public Canvas canvas;
    public Slider camFOV;
    public Slider ssSample;
    public float mouseSensitivity = 0.5F;
    public ComboBox<String> deviceDropdown;
    private int selectedDeviceIndex;
    private TornadoDriver driver;
    private TaskSchedule ts;
    // ==============================================================
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    private float moveSpeed = 0.2F;
    private boolean fwd, strafeL, strafeR, back, up, down;
    private Float3 upVector;

    // ==============================================================
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
        final int NUM_BODIES = 6;

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
        bodyReflectivities.set(0, 32F);

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

        bodyTypes.set(4, 2);
        bodyPositions.set(4, new Float4(-3F, 0, 0, 0));
        bodySizes.set(4, 0.5F);
        bodyColors.set(4, Color.WHITE);
        bodyReflectivities.set(4, 4F);

        bodyTypes.set(5, 2);
        bodyPositions.set(5, new Float4(3F, 0, 0, 0));
        bodySizes.set(5, 0.5F);
        bodyColors.set(5, Color.BLACK);
        bodyReflectivities.set(5, 64F);
    }

    // ==============================================================
    private void render(boolean renderWithTornado) {

        if (renderWithTornado)
            ts.execute(grid);
        else
            Renderer.render(dimensions, pixels, camera,
                    bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                    worldBGColor, lightPosition, lightSize, lightColor, softShadowSampleSize);

        pixelWriter.setPixels(0, 0, dimensions[0], dimensions[1], format, pixels, 0, dimensions[0]);
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

        upVector = new Float3(0, 1F, 0);

        // ==============================================================
        ts = new TaskSchedule("s0");
        ts.streamIn(dimensions, camera, softShadowSampleSize);
        ts.task("t0", Renderer::render, dimensions, pixels, camera,
                bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                worldBGColor, lightPosition, lightSize, lightColor, softShadowSampleSize);
        ts.streamOut(pixels);

        WorkerGrid worker = new WorkerGrid2D(dimensions[0], dimensions[1]);
        worker.setLocalWork(16, 16, 1);
        grid = new GridScheduler();
        grid.setWorkerGrid("s0.t0", worker);

        driver = TornadoRuntime.getTornadoRuntime().getDriver(0);
        int numDevices = driver.getDeviceCount();

        deviceDropdown.getItems().add("CPU - Pure Java Sequential");
        for (int i = 0; i < numDevices; i++)
            deviceDropdown.getItems().add(driver.getDevice(i).getPhysicalDevice().getDeviceName());

        selectedDeviceIndex = 0;
        deviceDropdown.getSelectionModel().selectFirst();

        // ==============================================================
        camFOV.valueProperty().addListener((observable, oldValue, newValue) -> camera[5] = newValue.floatValue());
        ssSample.valueProperty().addListener((observable, oldValue, newValue) -> softShadowSampleSize[0] = newValue.intValue());

        // ==============================================================
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                render(selectedDeviceIndex > 0);
                updatePos();
                fps.setText(String.format("FPS: %.2f", 1_000_000_000.0 / (now - lastUpdate)));
                lastUpdate = now;
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

    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case SPACE:
                up = true;
                break;
            case C:
                down = true;
                break;
            case W:
                fwd = true;
                break;
            case S:
                back = true;
                break;
            case A:
                strafeL = true;
                break;
            case D:
                strafeR = true;
                break;
            case SHIFT:
                moveSpeed = 0.4F;
                break;
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case SPACE:
                up = false;
                break;
            case C:
                down = false;
                break;
            case W:
                fwd = false;
                break;
            case S:
                back = false;
                break;
            case A:
                strafeL = false;
                break;
            case D:
                strafeR = false;
                break;
            case SHIFT:
                moveSpeed = 0.2F;
                break;
        }
    }

    public void updatePos() {

        Float3 eye = new Float3(camera[0], camera[1], camera[2]);

        float yaw = camera[3] * floatPI() / 180;
        float pitch = -camera[4] * floatPI() / 180;

        Float3 fwdVector = Float3.normalise(new Float3(
                floatSin(yaw) * floatCos(pitch),
                floatSin(pitch),
                floatCos(yaw) * floatCos(pitch)));

        Float3 leftVector = Float3.cross(fwdVector, upVector);
        Float3 rightVector = Float3.cross(upVector, fwdVector);

        if (fwd) eye = Float3.add(eye, Float3.mult(fwdVector, moveSpeed));
        if (back) eye = Float3.sub(eye, Float3.mult(fwdVector, moveSpeed));
        if (strafeL) eye = Float3.add(eye, Float3.mult(leftVector, moveSpeed));
        if (strafeR) eye = Float3.add(eye, Float3.mult(rightVector, moveSpeed));
        if (up) eye = Float3.add(eye, Float3.mult(upVector, moveSpeed));
        if (down) eye = Float3.sub(eye, Float3.mult(upVector, moveSpeed));

        camera[0] = eye.get(0);
        camera[1] = eye.get(1);
        camera[2] = eye.get(2);
    }

    public void selectDevice() {
        selectedDeviceIndex = deviceDropdown.getSelectionModel().getSelectedIndex();

        if (selectedDeviceIndex > 0)
            ts.mapAllTo(driver.getDevice(selectedDeviceIndex - 1));
    }
}
