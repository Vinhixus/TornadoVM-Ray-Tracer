package com.vinhderful.pathtracer.utils;

/**
 * Represents a vector in 3d space using x, y, z values
 */
public class Vector3f {

    private float x;
    private float y;
    private float z;

    /**
     * Construct a Vector3f object given x, y, z
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Vector3f(float x, float y, float z) {
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
            throw new IllegalArgumentException("One or more parameters are NaN!");

        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Rotate a vector with given yaw and pitch
     *
     * @param yaw   yaw of the camera
     * @param pitch pitch of the camera
     * @return the resulting vector
     */
    public Vector3f rotate(float yaw, float pitch) {

        double _yaw = Math.toRadians(yaw);
        double _pitch = Math.toRadians(pitch);

        // Rotate horizontally (yaw)
        float _y = (float) (y * Math.cos(_pitch) - z * Math.sin(_pitch));
        float _z = (float) (y * Math.sin(_pitch) + z * Math.cos(_pitch));

        // Rotate vertically (pitch)
        float _x = (float) (x * Math.cos(_yaw) + _z * Math.sin(_yaw));
        _z = (float) (-x * Math.sin(_yaw) + _z * Math.cos(_yaw));

        return new Vector3f(_x, _y, _z);
    }

    /**
     * Get x attribute
     *
     * @return x attribute of Vector3f
     */
    public float getX() {
        return x;
    }

    /**
     * Set x attribute
     *
     * @param x x value to set x attribute to
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Get y attribute
     *
     * @return y attribute of Vector3f
     */
    public float getY() {
        return y;
    }

    /**
     * Set y attribute
     *
     * @param y y value to set y attribute to
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Get z attribute
     *
     * @return z attribute of Vector3f
     */
    public float getZ() {
        return z;
    }

    /**
     * Set z attribute
     *
     * @param z z value to set z attribute to
     */
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * Return the length of the vector
     *
     * @return the length of vector
     */
    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }


    /**
     * Add this vector to a scalar value
     *
     * @param scalar the scalar
     * @return the resulting vector
     */
    public Vector3f add(float scalar) {
        return new Vector3f(x + scalar, y + scalar, z + scalar);
    }

    /**
     * Add this vector to another given vector
     *
     * @param vec the other vector
     * @return the resulting vector
     */
    public Vector3f add(Vector3f vec) {
        return new Vector3f(x + vec.x, y + vec.y, z + vec.z);
    }

    /**
     * Subtract another given vector from this vector
     *
     * @param vec the other vector
     * @return the resulting vector
     */
    public Vector3f subtract(Vector3f vec) {
        return new Vector3f(x - vec.x, y - vec.y, z - vec.z);
    }

    /**
     * Subtract this vector with a scalar value
     *
     * @param scalar the scalar
     * @return the resulting vector
     */
    public Vector3f subtract(float scalar) {
        return new Vector3f(x - scalar, y - scalar, z - scalar);
    }

    /**
     * Multiply this vector with a given scalar
     *
     * @param scalar the scalar
     * @return the resulting vector
     */
    public Vector3f multiply(float scalar) {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Normalise this vector, i.e. reduce the length of this vector to 1
     *
     * @return the resulting normal vector
     */
    public Vector3f normalize() {
        float magnitude = magnitude();
        return new Vector3f(x / magnitude, y / magnitude, z / magnitude);
    }

    /**
     * Get the dot product of this vector and another given vector
     *
     * @param vec the other vector
     * @return the resulting dot product
     */
    public float dotProduct(Vector3f vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }

    /**
     * Get the cross product of this vector and another given vector
     *
     * @param vec the other vector
     * @return the resulting cross product
     */
    public Vector3f crossProduct(Vector3f vec) {
        return new Vector3f(
                y * vec.getZ() - z * vec.getY(),
                z * vec.getX() - x * vec.getZ(),
                x * vec.getY() - y * vec.getX());
    }

    /**
     * Get the distance between this vector and another given vector
     *
     * @param vec the other vector
     * @return the distance between the two vectors
     */
    public float distanceFrom(Vector3f vec) {
        return (float) Math.sqrt(Math.pow(x - vec.x, 2) + Math.pow(y - vec.y, 2) + Math.pow(z - vec.z, 2));
    }

    /**
     * Get the array representation of the vector
     *
     * @return the array representation of the vector
     */
    public float[] toArray() {
        return new float[]{x, y, z};
    }

    /**
     * Get an arbitrary perpendicular vector to this vector
     *
     * @return an arbitrary perpendicular vector
     */
    public Vector3f perpVector() {

        // Make sure vector is not 0 vector
        if (x == 0 && y == 0 && z == 0) return null;

        if (y == 0 && z == 0)
            return crossProduct(new Vector3f(0, 1, 0));

        return crossProduct(new Vector3f(1, 0, 0));

    }

    /**
     * Return a stringified format of this vector
     *
     * @return the string format of the vector
     */
    @Override
    public String toString() {
        return "Vector3f {" + "x: " + x + ", y: " + y + ", z: " + z + '}';
    }
}
