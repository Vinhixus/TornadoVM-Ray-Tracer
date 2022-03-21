/*
 * This file is part of Tornado-Ray-Tracer: A Java-based ray tracer running on TornadoVM.
 * URL: https://github.com/Vinhixus/TornadoVM-Ray-Tracer
 *
 * Copyright [2022] [Vinh Pham Van]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * A world representing objects in the scene
 */
public class World {

    /**
     * A light is required to be at index 0
     */
    public static final int LIGHT_INDEX = 0;

    /**
     * A plane is required to be at index 1
     */
    public static final int PLANE_INDEX = 1;
    private final ArrayList<Body> bodies;
    /**
     * Variables encapsulating the animation that can be played
     */
    private final ScheduledExecutorService animationService;
    private final Runnable animation;
    AtomicReference<Float> t;
    private Skybox skybox;
    private Light light;
    private Plane plane;
    /**
     * Input buffers
     */
    private VectorFloat4 bodyPositions;
    private VectorFloat bodySizes;
    private VectorFloat4 bodyColors;
    private VectorFloat bodyReflectivities;
    private Future<?> animator;
    private boolean isAnimating;

    /**
     * Instantiate a default world
     *
     * @throws Exception is thrown if light is not found at index 0 or plane is not found at index 1
     */
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

    /**
     * Generate a pre-defined default world
     */
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
        plane = new Plane(0, 40F, 16F);
        addBody(plane);

        // Spheres in the scene
        addBody(new Sphere(new Float4(0, 1F, 0, 0), 1F, Color.GRAY, 32F));
        addBody(new Sphere(new Float4(0, 0.5F, 3F, 0), 0.5F, Color.RED, 8F));
        addBody(new Sphere(new Float4(4.5F, 0.5F, 0, 0), 0.5F, Color.GREEN, 16F));
        addBody(new Sphere(new Float4(6F, 0.5F, 0, 0), 0.5F, Color.BLUE, 32F));
        addBody(new Sphere(new Float4(0, 0.5F, 7.5F, 0), 0.5F, Color.BLACK, 48F));
    }

    /**
     * Return if the world is currently being animated
     *
     * @return if the world is animating
     */
    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * Define the default animation step:
     * The spheres in the scene move around in a circle
     *
     * @return a Runnable defining one animation step
     */
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

    /**
     * Play the defined animation
     */
    public void startAnimation() {
        animator = animationService.scheduleAtFixedRate(animation, 0, 16_666_666, TimeUnit.NANOSECONDS);
        isAnimating = true;
    }

    /**
     * Pause the default animation
     */
    public void stopAnimation() {
        animator.cancel(true);
        isAnimating = false;
    }

    /**
     * Toggle animation state
     */
    public void toggleAnimation() {
        if (isAnimating) stopAnimation();
        else startAnimation();
    }

    /**
     * Allocate and initialise the input buffers for rendering
     */
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

    /**
     * Copy the data to input buffers
     */
    public void updateBodyPositionBuffer() {
        if (isAnimating)
            for (int i = 0; i < bodies.size(); i++)
                bodyPositions.set(i, bodies.get(i).getPosition().duplicate());
        else
            bodyPositions.set(LIGHT_INDEX, light.getPosition().duplicate());
    }

    /**
     * Return the memory address to the input buffer representing body positions
     *
     * @return the VectorFloat4 representing body positions
     */
    public VectorFloat4 getBodyPositionsBuffer() {
        return bodyPositions;
    }

    /**
     * Return the memory address to the input buffer representing body sizes
     *
     * @return the VectorFloat representing body sizes
     */
    public VectorFloat getBodySizesBuffer() {
        return bodySizes;
    }

    /**
     * Return the memory address to the input buffer representing body colors
     *
     * @return the VectorFloat representing body colors
     */
    public VectorFloat4 getBodyColorsBuffer() {
        return bodyColors;
    }

    /**
     * Return the memory address to the input buffer representing body reflectivities
     *
     * @return the VectorFloat representing body reflectivities
     */
    public VectorFloat getBodyReflectivitiesBuffer() {
        return bodyReflectivities;
    }

    /**
     * Return the memory address to the input buffer representing the skybox
     *
     * @return the VectorFloat4 representing skybox
     */
    public VectorFloat4 getSkyboxBuffer() {
        return skybox.getBuffer();
    }

    /**
     * Return the memory address to the input buffer representing skybox dimensions
     *
     * @return the int array containing [0] = width of skybox image, [1] = height of skybox image
     */
    public int[] getSkyboxDimensionsBuffer() {
        return skybox.getDimensionsBuffer();
    }

    /**
     * Return the light source in the world
     *
     * @return the light source
     */
    public Light getLight() {
        return light;
    }

    /**
     * Return the plane
     *
     * @return the plane
     */
    public Plane getPlane() {
        return plane;
    }

    /**
     * Add a body to the world
     *
     * @param body the body to add
     */
    private void addBody(Body body) {
        bodies.add(body);
    }
}
