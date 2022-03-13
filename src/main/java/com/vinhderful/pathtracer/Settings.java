package com.vinhderful.pathtracer;

import uk.ac.manchester.tornado.api.collections.types.Float3;

public class Settings {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final int INITIAL_SHADOW_SAMPLE_SIZE = 1;
    public static final int MAX_SHADOW_SAMPLE_SIZE = 400;

    public static final int INITIAL_REFLECTION_BOUNCES = 1;
    public static final int MAX_REFLECTION_BOUNCES = 6;

    public static final Float3 INITIAL_CAMERA_POSITION = new Float3(0, 1, -4);
    public static final float INITIAL_CAMERA_YAW = 0;
    public static final float INITIAL_CAMERA_PITCH = 0;
    public static final float INITIAL_CAMERA_FOV = 60;
}
