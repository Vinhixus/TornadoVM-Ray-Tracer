package com.vinhderful.pathtracer.misc;

import com.vinhderful.pathtracer.Settings;
import uk.ac.manchester.tornado.api.collections.types.Float3;

import static com.vinhderful.pathtracer.utils.Angle.TO_RADIANS;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

public class Camera {

    private static final float MOUSE_SENSITIVITY = 0.5F;
    private static final float MOVE_SPEED = 0.1F;

    private Float3 position;
    private float yaw;
    private float pitch;
    private float fov;

    private float[] buffer;

    private float moveSpeed;

    // Up direction
    private final Float3 upVector;
    private final float planeHeight;

    public Camera(World world) {
        position = Settings.INITIAL_CAMERA_POSITION;
        yaw = Settings.INITIAL_CAMERA_YAW;
        pitch = Settings.INITIAL_CAMERA_PITCH;
        fov = Settings.INITIAL_CAMERA_FOV;

        upVector = new Float3(0, 1F, 0);
        moveSpeed = MOVE_SPEED;
        planeHeight = world.getPlane().getPosition().getY();

        allocateAndInitializeBuffer();
    }

    private void allocateAndInitializeBuffer() {
        buffer = new float[]{position.getX(), position.getY(), position.getZ(), yaw, pitch, fov};
    }

    public float[] getBuffer() {
        return buffer;
    }

    public void updateBuffer() {
        buffer[0] = position.getX();
        buffer[1] = position.getY();
        buffer[2] = position.getZ();
        buffer[3] = yaw;
        buffer[4] = pitch;
        buffer[5] = fov;
    }

    /**
     * Update camera position on movement
     */
    public void updatePositionOnMovement(boolean fwd, boolean back, boolean strafeL, boolean strafeR, boolean up, boolean down) {

        // Get yaw and pitch in radians
        float _yaw = yaw * TO_RADIANS;
        float _pitch = -pitch * TO_RADIANS;

        // Calculate forward pointing vector from yaw and pitch
        Float3 fwdVector = Float3.normalise(new Float3(
                floatSin(_yaw) * floatCos(_pitch),
                floatSin(_pitch),
                floatCos(_yaw) * floatCos(_pitch)));

        // Calculate left and right pointing vector from forward and up vectors
        Float3 leftVector = Float3.normalise(Float3.cross(fwdVector, upVector));
        Float3 rightVector = Float3.normalise(Float3.cross(upVector, fwdVector));

        // Depending on key pressed, update camera position
        if (fwd) position = Float3.add(position, Float3.mult(fwdVector, moveSpeed));
        if (back) position = Float3.sub(position, Float3.mult(fwdVector, moveSpeed));
        if (strafeL) position = Float3.add(position, Float3.mult(leftVector, moveSpeed));
        if (strafeR) position = Float3.add(position, Float3.mult(rightVector, moveSpeed));
        if (up) position = Float3.add(position, Float3.mult(upVector, moveSpeed));
        if (down) position = Float3.sub(position, Float3.mult(upVector, moveSpeed));

        // Limit camera to above plane
        if (position.getY() < planeHeight) position.setY(planeHeight);
    }

    /**
     * Update camera position on mouse dragged
     */
    public void updatePositionOnMouseDragged(double mousePosX, double mousePosY, double mouseOldX, double mouseOldY) {

        // Add mouse displacement in x direction to camera yaw
        yaw += (mousePosX - mouseOldX) * MOUSE_SENSITIVITY;

        // Add mouse displacement in y direction to camera pitch
        // Limit y direction lookaround to a 180-degree angle
        pitch = (float) min(89.99, max(-89.99, pitch + (mousePosY - mouseOldY) * MOUSE_SENSITIVITY));
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public void run() {
        moveSpeed = MOVE_SPEED * 2;
    }

    public void walk() {
        moveSpeed = MOVE_SPEED;
    }
}
