package com.vinhderful.raytracer;

import com.vinhderful.raytracer.misc.World;
import com.vinhderful.raytracer.renderer.Renderer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import uk.ac.manchester.tornado.api.*;
import uk.ac.manchester.tornado.api.collections.types.Float3;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntime;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static com.vinhderful.raytracer.misc.World.PLANE_INDEX;
import static com.vinhderful.raytracer.utils.Angle.TO_RADIANS;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

/**
 * Initialises JavaFX FXML elements together with GUI.fxml, contains driver code
 */
@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Controller {

    /**
     * Pixel buffer containing ARGB values of pixel colors
     * Size = width * height of canvas resolution
     **/
    private static int[] pixels;

    /**
     * Resolution of the canvas
     * ------------------------
     * dimensions[0]: width
     * dimensions[1]: height
     **/
    private static int[] dimensions;

    /**
     * Camera properties
     * -----------------
     * camera[0]: x coordinate of position
     * camera[1]: y coordinate of position
     * camera[2]: z coordinate of position
     * camera[3]: yaw of rotation
     * camera[4]: pitch of rotation
     * camera[5]: field of view (FOV)
     **/
    private static float[] camera;

    /**
     * Define up direction (only needed for camera movement controls)
     **/
    private Float3 upVector;

    /**
     * Path tracing properties
     * -----------------------
     * pathTracingProperties[0]: Sample size of soft shadows
     * pathTracingProperties[1]: Bounce limit for reflection rays
     **/
    private static int[] pathTracingProperties;


    /**
     * Skybox
     **/
    private static VectorFloat4 skybox;
    private static int[] skyboxDimensions;

    /**
     * The following 4 vectors define the objects in the scene.
     * Example:
     * bodyPositions.get(2), bodySizes.get(2), bodyColors.get(2), bodyReflectivities.get(2)
     * define the properties of the body at index 2
     * -----------------------------------------
     * bodyPosition: position of the body in 3D space
     * bodySize: size of the body (e.g. radius of sphere)
     * bodyColor: color of the sphere defined by R, G, B float values
     * bodyReflectivity: reflectivity of the object, higher is more reflective
     */
    private static VectorFloat4 bodyPositions;
    private static VectorFloat bodySizes;
    private static VectorFloat4 bodyColors;
    private static VectorFloat bodyReflectivities;


    /**
     * JavaFX GUI elements
     */
    @FXML

    // Main pane and canvas encapsulating the viewport
    public Canvas canvas;

    public VBox settingsPanel;
    public MenuItem settingsPanelToggle;

    // Adjustable light position
    public Slider lightX;
    public Slider lightY;
    public Slider lightZ;

    // Adjustable camera field of view
    public Slider cameraFOV;

    // Adjustable soft shadow sample size and reflection bounce limit
    public Slider shadowSampleSize;
    public Slider reflectionBounceLimit;

    // Frames per second text output and helper variables
    public Label fps;
    private static long fpsLastUpdate = 0;

    // Debug text output
    public Label debugOutput;

    // Device selection dropdown
    public ComboBox<String> deviceDropdown;
    private int selectedDeviceIndex;

    // Button to play predefined animation, helper variables for animation
    public Button animateButton;
    private boolean animating;
    private static float t = 0;

    // Camera control
    private static final float MOUSE_SENSITIVITY = 0.5F;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    // Movement
    private boolean fwd, strafeL, strafeR, back, up, down;
    private float moveSpeed = 0.2F;

    // Tornado elements
    private ArrayList<TornadoDevice> devices;
    private TaskSchedule ts;
    private GridScheduler grid;
    private boolean renderWithTornado;

    // PixelWriter and it's format
    private PixelWriter pixelWriter;
    private WritablePixelFormat<IntBuffer> format;


    /**
     * Initialise renderer, world, camera and populate with objects
     */
    @FXML
    public void initialize() throws Exception {

        // Build world
        World world = new World();
        skybox = world.getSkybox();
        skyboxDimensions = world.getSkyboxDimensions();
        bodyPositions = world.getBodyPositions();
        bodySizes = world.getBodySizes();
        bodyColors = world.getBodyColors();
        bodyReflectivities = world.getBodyReflectivities();

        // Initialise settings
        setRenderingProperties();
        setTornadoTaskSchedule();
        setAvailableDevices();
        setSliderListeners();

        // Start with no animation
        animating = false;

        // Initialise fps last update
        fpsLastUpdate = 0;

        // Define main animation loop - gets called every frame
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                // Update frame
                render();

                // Camera movement
                updateCameraPosition();

                // Play animation if button is pressed
                if (animating) animate();

                // Record and output fps
                fps.setText(String.format("FPS: %.2f", 1_000_000_000.0 / (now - fpsLastUpdate)));
                fpsLastUpdate = now;
            }
        };

        // Start animation loop
        timer.start();
    }


    /**
     * Initialise canvas, up vector, dimensions, pixel buffer, pixel writer, format, camera and path tracing properties
     */
    private void setRenderingProperties() {

        // Get graphics context and dimensions
        GraphicsContext g = canvas.getGraphicsContext2D();
        int width = (int) g.getCanvas().getWidth();
        int height = (int) g.getCanvas().getHeight();

        dimensions = new int[]{width, height};
        pixels = new int[width * height];

        pixelWriter = g.getPixelWriter();
        format = WritablePixelFormat.getIntArgbInstance();

        // camera position: {0, 1, -4}
        // camera yaw: 0
        // camera pitch: 0
        // camera FOV: 60
        camera = new float[]{0, 1F, -4F, 0, 0, 60};

        // shadow sample size = 1
        // reflection bounces = 1
        pathTracingProperties = new int[]{1, 1};

        // Up direction is positive y direction
        upVector = new Float3(0, 1F, 0);
    }


    /**
     * Define Tornado task schedule for Parallel Renderer.render method
     */
    private void setTornadoTaskSchedule() {

        // Define task schedule
        ts = new TaskSchedule("s0");
        ts.streamIn(bodyPositions, camera, pathTracingProperties);
        ts.task("t0", Renderer::render, pixels, dimensions, camera,
                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                skybox, skyboxDimensions, pathTracingProperties);
        ts.streamOut(pixels);

        // Define worker grid
        WorkerGrid worker = new WorkerGrid2D(dimensions[0], dimensions[1]);
        worker.setLocalWork(16, 16, 1);
        grid = new GridScheduler();
        grid.setWorkerGrid("s0.t0", worker);
    }


    /**
     * Populate device dropdown list, perform initial mapping to avoid runtime lag
     */
    private void setAvailableDevices() {

        // Initialise list of devices
        devices = new ArrayList<>();

        // Add sequential execution to devices list
        devices.add(null);
        deviceDropdown.getItems().add("(Pure Java) - CPU");

        // Get Tornado drivers
        TornadoRuntimeCI runtimeCI = TornadoRuntime.getTornadoRuntime();
        int numTornadoDrivers = runtimeCI.getNumDrivers();

        for (int i = 0; i < numTornadoDrivers; i++) {

            TornadoDriver driver = runtimeCI.getDriver(i);
            int numDevices = driver.getDeviceCount();

            // Add Tornado devices, perform initial mapping
            for (int j = 0; j < numDevices; j++) {
                TornadoDevice device = driver.getDevice(j);
                devices.add(device);
                deviceDropdown.getItems().add("(" + driver.getName() + ") " + device.getPhysicalDevice().getDeviceName());

                // Perform an initial mapping to avoid runtime lag
                ts.mapAllTo(device);
                ts.execute(grid);
            }
        }

        // Select first device (Pure Java sequential)
        selectedDeviceIndex = 0;
        deviceDropdown.getSelectionModel().selectFirst();
        renderWithTornado = false;
    }

    /**
     * Set up slider listeners and define actions on change
     */
    private void setSliderListeners() {

        // Adjustable light position
        lightX.valueProperty().addListener((observable, oldValue, newValue)
                -> bodyPositions.set(0, new Float4(newValue.floatValue(), bodyPositions.get(0).getY(), bodyPositions.get(0).getZ(), 0)));
        lightY.valueProperty().addListener((observable, oldValue, newValue)
                -> bodyPositions.set(0, new Float4(bodyPositions.get(0).getX(), newValue.floatValue(), bodyPositions.get(0).getZ(), 0)));
        lightZ.valueProperty().addListener((observable, oldValue, newValue)
                -> bodyPositions.set(0, new Float4(bodyPositions.get(0).getX(), bodyPositions.get(0).getY(), newValue.floatValue(), 0)));

        // Adjustable camera field of view
        cameraFOV.valueProperty().addListener((observable, oldValue, newValue)
                -> camera[5] = newValue.floatValue());

        // Adjustable path tracing rendering properties
        shadowSampleSize.valueProperty().addListener((observable, oldValue, newValue)
                -> pathTracingProperties[0] = newValue.intValue());
        reflectionBounceLimit.valueProperty().addListener((observable, oldValue, newValue)
                -> pathTracingProperties[1] = newValue.intValue());
    }


    /**
     * Render one frame: Populate pixels array and set pixels on the canvas
     */
    private void render() {

        // Populate pixels array depending on selected device
        if (renderWithTornado)
            ts.execute(grid);
        else
            Renderer.render(pixels, dimensions, camera,
                    bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                    skybox, skyboxDimensions, pathTracingProperties);

        // Set the pixels on the canvas
        pixelWriter.setPixels(0, 0, dimensions[0], dimensions[1], format, pixels, 0, dimensions[0]);
    }

    /**
     * Define action on mouse drag
     *
     * @param mouseEvent mouse event
     */
    public void mouseDragged(MouseEvent mouseEvent) {

        // Record mouse position
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = mouseEvent.getX();
        mousePosY = mouseEvent.getY();

        // Add mouse displacement in x direction to camera yaw
        camera[3] += (mousePosX - mouseOldX) * MOUSE_SENSITIVITY;

        // Add mouse displacement in y direction to camera pitch
        // Limit y direction lookaround to a 180-degree angle
        camera[4] = (float) min(90, max(-90, camera[4] + (mousePosY - mouseOldY) * MOUSE_SENSITIVITY));
    }

    /**
     * Define action of mouse press
     *
     * @param mouseEvent mouse event
     */
    public void mousePressed(MouseEvent mouseEvent) {

        // Record mouse position
        mousePosX = mouseEvent.getX();
        mousePosY = mouseEvent.getY();
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;

        // Hide mouse cursor while controlling camera
        canvas.setCursor(Cursor.NONE);
    }

    /**
     * Define action on mouse release
     */
    public void mouseReleased() {
        canvas.setCursor(Cursor.DEFAULT);
    }


    /**
     * Define action on key press
     *
     * @param keyEvent key event
     */
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case Q:
                up = true;
                break;
            case E:
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

    /**
     * Define action on key release
     *
     * @param keyEvent key event
     */
    public void keyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case Q:
                up = false;
                break;
            case E:
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

    /**
     * Update camera position on movement
     */
    public void updateCameraPosition() {

        // Get current camera position from camera properties
        Float3 cameraPosition = new Float3(camera[0], camera[1], camera[2]);

        // Get yaw and pitch in radians
        float yaw = camera[3] * TO_RADIANS;
        float pitch = -camera[4] * TO_RADIANS;

        // Calculate forward pointing vector from yaw and pitch
        Float3 fwdVector = Float3.normalise(new Float3(
                floatSin(yaw) * floatCos(pitch),
                floatSin(pitch),
                floatCos(yaw) * floatCos(pitch)));

        // Calculate left and right pointing vector from forward and up vectors
        Float3 leftVector = Float3.cross(fwdVector, upVector);
        Float3 rightVector = Float3.cross(upVector, fwdVector);

        // Depending on key pressed, update camera position
        if (fwd) cameraPosition = Float3.add(cameraPosition, Float3.mult(fwdVector, moveSpeed));
        if (back) cameraPosition = Float3.sub(cameraPosition, Float3.mult(fwdVector, moveSpeed));
        if (strafeL) cameraPosition = Float3.add(cameraPosition, Float3.mult(leftVector, moveSpeed));
        if (strafeR) cameraPosition = Float3.add(cameraPosition, Float3.mult(rightVector, moveSpeed));
        if (up) cameraPosition = Float3.add(cameraPosition, Float3.mult(upVector, moveSpeed));
        if (down) cameraPosition = Float3.sub(cameraPosition, Float3.mult(upVector, moveSpeed));

        // Limit camera to above plane
        float planeHeight = bodyPositions.get(PLANE_INDEX).getY() + 0.001F;
        if (cameraPosition.getY() < planeHeight) cameraPosition.setY(planeHeight);

        // Write result back to camera properties
        camera[0] = cameraPosition.get(0);
        camera[1] = cameraPosition.get(1);
        camera[2] = cameraPosition.get(2);
    }

    /**
     * Define action on device dropdown selection
     */
    public void selectDevice() {

        // Get selection from dropdown box
        selectedDeviceIndex = deviceDropdown.getSelectionModel().getSelectedIndex();
        TornadoDevice device = devices.get(selectedDeviceIndex);

        // Map task schedule to selected device if selected device is tornado device
        if (selectedDeviceIndex > 0) {
            renderWithTornado = true;
            ts.mapAllTo(device);
        } else
            renderWithTornado = false;
    }

    /**
     * Predefined simple animation
     * ---------------------------
     * Spheres move around in a circle
     */
    private void animate() {
        t = (t + 0.017453292F) % 6.2831855F;
        bodyPositions.set(3, new Float4(3 * floatSin(t), bodyPositions.get(3).getY(), 3 * floatCos(t), 0));
        bodyPositions.set(4, new Float4(4.5F * floatCos(t), bodyPositions.get(4).getY(), 4.5F * floatSin(t), 0));
        bodyPositions.set(5, new Float4(6 * floatCos(-t), bodyPositions.get(5).getY(), 6 * floatSin(-t), 0));
        bodyPositions.set(6, new Float4(7.5F * floatSin(-t), bodyPositions.get(6).getY(), 7.5F * floatCos(-t), 0));
    }

    /**
     * Define action on clicking "Animate" button
     */
    public void toggleAnimation() {
        animating = !animating;
        animateButton.setText(animating ? "Stop" : "Animate");
    }

    /**
     * Toggle right-side setting panel
     */
    public void toggleSettingsPanel() {
        boolean isVisible = settingsPanel.isVisible();
        settingsPanel.setVisible(!isVisible);
        settingsPanel.setManaged(!isVisible);
        settingsPanelToggle.setText(isVisible ? "Show Settings Panel" : "Hide Settings Panel");
        settingsPanel.getParent().getScene().getWindow().sizeToScene();
    }
}
