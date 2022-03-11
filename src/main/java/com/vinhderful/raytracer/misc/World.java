package com.vinhderful.raytracer.misc;

import com.vinhderful.raytracer.misc.bodies.Body;
import com.vinhderful.raytracer.misc.bodies.Light;
import com.vinhderful.raytracer.misc.bodies.Plane;
import com.vinhderful.raytracer.misc.bodies.Sphere;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatCos;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatSin;

public class World {

    public static final int LIGHT_INDEX = 0;
    public static final int PLANE_INDEX = 1;

    private Skybox skybox;
    private Light light;
    private Plane plane;
    private final ArrayList<Body> bodies;

    private VectorFloat4 bodyPositions;
    private VectorFloat bodySizes;
    private VectorFloat4 bodyColors;
    private VectorFloat bodyReflectivities;

    private final ScheduledExecutorService animationService;
    private final Runnable animation;
    private Future<?> animator;
    AtomicReference<Float> t;
    private boolean isAnimating;

    public World() throws Exception {

        // Populate world
        bodies = new ArrayList<>();
        generateDefaultWorld();

        // Setup default animation
        animationService = Executors.newScheduledThreadPool(1);
        animation = getDefaultAnimation();
        t = new AtomicReference<>((float) 0);
        isAnimating = false;

        // Make sure we have 1 light and 1 plane at the first two indexes
        if (bodies.size() < 2)
            throw new Exception("A light source and a plane are mandatory!");
        if (!(bodies.get(LIGHT_INDEX) instanceof Light))
            throw new Exception("A light source needs to be at index " + LIGHT_INDEX + "!");
        if (!(bodies.get(PLANE_INDEX) instanceof Plane))
            throw new Exception("A plane needs to be at index " + PLANE_INDEX + "!");

        // Generate the tornado compatible vector representations of the bodies
        allocateAndInitializeBuffers();
    }

    private void generateDefaultWorld() {

        // Skybox
        String skyboxFileName = "Sky.jpg";
        System.out.println("-> Loading Skybox Image '" + skyboxFileName + "'...");
        skybox = new Skybox(skyboxFileName);

        System.out.println("-> Adding object to the scene...");
        // Sphere light
        light = new Light(new Float4(1F, 3F, -1.5F, 0), 0.4F, Color.WHITE);
        addBody(light);

        // Checkerboard plane
        plane = new Plane(0, 16F);
        addBody(plane);

        // Spheres in the scene
        addBody(new Sphere(new Float4(0, 1F, 0, 0), 1F, Color.GRAY, 32F));
        addBody(new Sphere(new Float4(0, 0.5F, 3F, 0), 0.5F, Color.RED, 8F));
        addBody(new Sphere(new Float4(4.5F, 0.5F, 0, 0), 0.5F, Color.GREEN, 16F));
        addBody(new Sphere(new Float4(6F, 0.5F, 0, 0), 0.5F, Color.BLUE, 32F));
        addBody(new Sphere(new Float4(0, 0.5F, 7.5F, 0), 0.5F, Color.BLACK, 48F));
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    private Runnable getDefaultAnimation() {
        return () -> {
            t.set((t.get() + 0.017453292F) % 6.2831855F);

            bodies.get(3).getPosition().setX(3F * floatSin(t.get()));
            bodies.get(3).getPosition().setZ(3F * floatCos(t.get()));

            bodies.get(4).getPosition().setX(4.5F * floatCos(t.get()));
            bodies.get(4).getPosition().setZ(4.5F * floatSin(t.get()));

            bodies.get(5).getPosition().setX(6F * floatCos(-t.get()));
            bodies.get(5).getPosition().setZ(6F * floatSin(-t.get()));

            bodies.get(6).getPosition().setX(7.5F * floatSin(-t.get()));
            bodies.get(6).getPosition().setZ(7.5F * floatCos(-t.get()));
        };
    }

    public void startAnimation() {
        animator = animationService.scheduleAtFixedRate(animation, 0, 16_666_666, TimeUnit.NANOSECONDS);
        isAnimating = true;
    }

    public void stopAnimation() {
        animator.cancel(true);
        isAnimating = false;
    }

    public void toggleAnimation() {
        if (isAnimating) stopAnimation();
        else startAnimation();
    }

    private void allocateAndInitializeBuffers() {
        System.out.println("-> Allocating object representation buffers...");

        int numBodies = bodies.size();

        bodyPositions = new VectorFloat4(numBodies);
        bodySizes = new VectorFloat(numBodies);
        bodyColors = new VectorFloat4(numBodies);
        bodyReflectivities = new VectorFloat(numBodies);

        for (int i = 0; i < numBodies; i++) {
            Body body = bodies.get(i);

            bodyPositions.set(i, body.getPosition());
            bodySizes.set(i, body.getSize());
            bodyColors.set(i, body.getColor());
            bodyReflectivities.set(i, body.getReflectivity());
        }
    }

    public void updateBodyPositionBuffer() {
        if (isAnimating)
            for (int i = 0; i < bodies.size(); i++)
                bodyPositions.set(i, bodies.get(i).getPosition().duplicate());
        else
            bodyPositions.set(LIGHT_INDEX, light.getPosition().duplicate());
    }

    private void addBody(Body body) {
        bodies.add(body);
    }

    public Light getLight() {
        return light;
    }


    public Plane getPlane() {
        return plane;
    }

    public VectorFloat4 getBodyPositionsBuffer() {
        return bodyPositions;
    }

    public VectorFloat getBodySizesBuffer() {
        return bodySizes;
    }

    public VectorFloat4 getBodyColorsBuffer() {
        return bodyColors;
    }

    public VectorFloat getBodyReflectivitiesBuffer() {
        return bodyReflectivities;
    }

    public VectorFloat4 getSkyboxBuffer() {
        return skybox.getVectorFloat4();
    }

    public int[] getSkyboxDimensionsBuffer() {
        return skybox.getDimensions();
    }
}
