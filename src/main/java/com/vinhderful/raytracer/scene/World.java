package com.vinhderful.raytracer.scene;

import com.vinhderful.raytracer.bodies.Body;
import com.vinhderful.raytracer.bodies.Plane;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

import java.util.ArrayList;

/**
 * Represents the entire scene with a list of bodies, a camera, a light source,
 * a plane and the world's background color
 */
public class World {

    private final ArrayList<Body> bodies;
    private final Camera camera;
    private final Light light;
    private final Plane plane;
    private final Color backgroundColor;

    /**
     * Construct a World object. Camera defaults to position {0, 0, 0} with FOV of
     * 60, plane defaults to height of -1, light source defaults to a WHITE light at
     * {-1, 0.8, -1}, background color defaults to BLACK
     */
    public World() {
        this.bodies = new ArrayList<>();
        this.camera = new Camera();
        this.plane = new Plane(-1F);
        this.light = new Light(new Vector3f(2F, 1.5F, -1.5F), Color.WHITE);
        this.bodies.add(light);
        this.bodies.add(plane);
        this.backgroundColor = Color.BLACK;
    }

    /**
     * Get the list of bodies in the world
     *
     * @return the list of bodies in the world
     */
    public ArrayList<Body> getBodies() {
        return bodies;
    }

    /**
     * Add a body to the list of bodies in the world
     *
     * @param body the body to add to the list of bodies
     */
    public void addBody(Body body) {
        bodies.add(body);
    }

    /**
     * Get the camera
     *
     * @return the camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Get the plane
     *
     * @return the plane
     */
    public Plane getPlane() {
        return plane;
    }

    /**
     * Get the light source
     *
     * @return the light source
     */
    public Light getLight() {
        return light;
    }

    /**
     * Set the x attribute of the position of the light source to an x value
     *
     * @param x the x value to set the light's position.x to
     */
    public void setLightX(float x) {
        light.setX(x);
    }

    /**
     * Set the z attribute of the position of the light source to an z value
     *
     * @param z the z value to set the light's position.z to
     */
    public void setLightZ(float z) {
        light.setZ(z);
    }

    /**
     * Get the world's background color
     *
     * @return the world's background color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }
}
