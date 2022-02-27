package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.utils.Color;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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

    private static final float MOUSE_SENSITIVITY = 0.5F;
    // ==============================================================
    private static long lastUpdate = 0;
    // ==============================================================
    private static float[] camera;
    private static int[] dimensions;
    private static int[] softShadowSampleSize;
    private static int[] rayBounceLimit;
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
    // ==============================================================
    @FXML
    public Label fps;
    public Label debugOutput;
    public Pane pane;
    public Canvas canvas;

    public Slider lightX;
    public Slider lightY;
    public Slider lightZ;

    public Slider camFOV;
    public Slider ssSample;
    public Slider rBounce;
    public ComboBox<String> deviceDropdown;
    // ==============================================================
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private boolean fwd, strafeL, strafeR, back, up, down;
    private Float3 upVector;
    private float moveSpeed = 0.2F;
    // ==============================================================
    private TornadoDriver driver;
    private int selectedDeviceIndex;

    private TaskSchedule ts;
    private GridScheduler grid;
    private GraphicsContext g;
    private PixelWriter pixelWriter;
    private WritablePixelFormat<IntBuffer> format;

    // ==============================================================
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
        bodyPositions.set(1, new Float4(0, 0, 0, 0));
        bodySizes.set(1, -1F);
        bodyColors.set(1, Color.BLACK);
        bodyReflectivities.set(1, 16F);

        // Spheres
        bodyTypes.set(2, 2);
        bodyPositions.set(2, new Float4(-3F, 0.5F, 0, 0));
        bodySizes.set(2, 0.5F);
        bodyColors.set(2, Color.WHITE);
        bodyReflectivities.set(2, 4F);

        bodyTypes.set(3, 2);
        bodyPositions.set(3, new Float4(-1.5F, 0.5F, 0, 0));
        bodySizes.set(3, 0.5F);
        bodyColors.set(3, Color.RED);
        bodyReflectivities.set(3, 8F);

        bodyTypes.set(4, 2);
        bodyPositions.set(4, new Float4(0, 0.5F, 0, 0));
        bodySizes.set(4, 0.5F);
        bodyColors.set(4, Color.GREEN);
        bodyReflectivities.set(4, 16F);

        bodyTypes.set(5, 2);
        bodyPositions.set(5, new Float4(1.5F, 0.5F, 0, 0));
        bodySizes.set(5, 0.5F);
        bodyColors.set(5, Color.BLUE);
        bodyReflectivities.set(5, 32F);

        bodyTypes.set(6, 2);
        bodyPositions.set(6, new Float4(3F, 1F, 1F, 0));
        bodySizes.set(6, 1F);
        bodyColors.set(6, Color.DARK_GRAY);
        bodyReflectivities.set(6, 64F);
    }

    // ==============================================================
    private void setRenderingProperties() {
        int width = (int) g.getCanvas().getWidth();
        int height = (int) g.getCanvas().getHeight();
        upVector = new Float3(0, 1F, 0);

        dimensions = new int[]{width, height};
        pixels = new int[dimensions[0] * dimensions[1]];

        format = WritablePixelFormat.getIntArgbInstance();
        pixelWriter = g.getPixelWriter();

        camera = new float[]{0, 0, -4F, 0, 0, 60};
        softShadowSampleSize = new int[]{1};
        rayBounceLimit = new int[]{1};
    }

    // ==============================================================
    private void initTornadoSettings() {
        driver = TornadoRuntime.getTornadoRuntime().getDriver(0);

        ts = new TaskSchedule("s0");
        ts.streamIn(bodyPositions, camera, softShadowSampleSize, rayBounceLimit);
        ts.task("t0", Renderer::render, dimensions, pixels, camera,
                bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                worldBGColor, softShadowSampleSize, rayBounceLimit);
        ts.streamOut(pixels);

        WorkerGrid worker = new WorkerGrid2D(dimensions[0], dimensions[1]);
        worker.setLocalWork(16, 16, 1);
        grid = new GridScheduler();
        grid.setWorkerGrid("s0.t0", worker);
    }

    // ==============================================================
    private void render(boolean renderWithTornado) {

        if (renderWithTornado)
            ts.execute(grid);
        else
            Renderer.render(dimensions, pixels, camera,
                    bodyTypes, bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                    worldBGColor, softShadowSampleSize, rayBounceLimit);

        pixelWriter.setPixels(0, 0, dimensions[0], dimensions[1], format, pixels, 0, dimensions[0]);
    }

    /**
     * Initialise renderer, world, camera and populate with objects
     */
    @FXML
    public void initialize() {

        // ==============================================================
        g = canvas.getGraphicsContext2D();

        setWorldProperties();
        populateWorld();

        setRenderingProperties();
        initTornadoSettings();

        // ==============================================================
        int numDevices = driver.getDeviceCount();

        deviceDropdown.getItems().add("CPU - Pure Java Sequential");
        for (int i = 0; i < numDevices; i++) {
            deviceDropdown.getItems().add(driver.getDevice(i).getPhysicalDevice().getDeviceName());
            ts.mapAllTo(driver.getDevice(i));
            ts.execute(grid);
        }

        selectedDeviceIndex = 0;
        deviceDropdown.getSelectionModel().selectFirst();

        // ==============================================================
        lightX.valueProperty().addListener((observable, oldValue, newValue) -> bodyPositions.set(0, new Float4(newValue.floatValue(), bodyPositions.get(0).getY(), bodyPositions.get(0).getZ(), 0)));
        lightY.valueProperty().addListener((observable, oldValue, newValue) -> bodyPositions.set(0, new Float4(bodyPositions.get(0).getX(), newValue.floatValue(), bodyPositions.get(0).getZ(), 0)));
        lightZ.valueProperty().addListener((observable, oldValue, newValue) -> bodyPositions.set(0, new Float4(bodyPositions.get(0).getX(), bodyPositions.get(0).getY(), newValue.floatValue(), 0)));

        camFOV.valueProperty().addListener((observable, oldValue, newValue) -> camera[5] = newValue.floatValue());
        rBounce.valueProperty().addListener((observable, oldValue, newValue) -> rayBounceLimit[0] = newValue.intValue());
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

        camera[3] += (mousePosX - mouseOldX) * MOUSE_SENSITIVITY;
        camera[4] = (float) min(90, max(-90, camera[4] + (mousePosY - mouseOldY) * MOUSE_SENSITIVITY));
    }

    public void mousePressed(MouseEvent mouseEvent) {
        mousePosX = mouseEvent.getX();
        mousePosY = mouseEvent.getY();
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;

        canvas.setCursor(Cursor.NONE);
    }

    public void mouseReleased() {
        canvas.setCursor(Cursor.DEFAULT);
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
