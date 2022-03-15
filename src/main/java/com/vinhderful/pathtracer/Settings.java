package com.vinhderful.pathtracer;

import uk.ac.manchester.tornado.api.collections.types.Float3;

/**
 * Settings class contains variables users can tweak to change the rendering environment and the GUI
 */
public class Settings {

    /**
     * Width of the viewport resolution
     */
    public static final int WIDTH = 1280;

    /**
     * Height of the viewport resolution
     */
    public static final int HEIGHT = 720;

    /**
     * Initial shadow sample size the program opens up with
     */
    public static final int INITIAL_SHADOW_SAMPLE_SIZE = 1;

    /**
     * Maximum shadow sample size the user can set the slider to
     */
    public static final int MAX_SHADOW_SAMPLE_SIZE = 400;

    /**
     * Initial reflection bounces the program opens up with
     */
    public static final int INITIAL_REFLECTION_BOUNCES = 1;

    /**
     * Maximum reflection bounces the user can set the slider to
     */
    public static final int MAX_REFLECTION_BOUNCES = 6;

    /**
     * Initial position of the camera
     */
    public static final Float3 INITIAL_CAMERA_POSITION = new Float3(0, 1, -4);

    /**
     * Initial yaw of the camera (horizontal rotation)
     */
    public static final float INITIAL_CAMERA_YAW = 0;

    /**
     * Initial pitch of the camera (vertical rotation)
     */
    public static final float INITIAL_CAMERA_PITCH = 0;

    /**
     * Initial field of view of the camera (horizontal angle determining the part of the world we can see)
     */
    public static final float INITIAL_CAMERA_FOV = 60;
}
