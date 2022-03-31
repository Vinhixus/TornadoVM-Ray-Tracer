/*
 * This file is part of Tornado-Ray-Tracer: A Java-based ray tracer running on TornadoVM.
 * URL: https://github.com/Vinhixus/TornadoVM-Ray-Tracer
 *
 * Copyright (c) 2021-2022, Vinh Pham Van
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
package com.vinhderful.raytracer.utils;

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
     * @param other the other vector
     * @return the resulting vector
     */
    public Vector3f add(Vector3f other) {
        return new Vector3f(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Subtract another given vector from this vector
     *
     * @param other the other vector
     * @return the resulting vector
     */
    public Vector3f subtract(Vector3f other) {
        return new Vector3f(x - other.x, y - other.y, z - other.z);
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
     * @param other the other vector
     * @return the resulting dot product
     */
    public float dotProduct(Vector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Get the cross product of this vector and another given vector
     *
     * @param other the other vector
     * @return the resulting cross product
     */
    public Vector3f crossProduct(Vector3f other) {
        return new Vector3f(
                y * other.getZ() - z * other.getY(),
                z * other.getX() - x * other.getZ(),
                x * other.getY() - y * other.getX());
    }

    /**
     * Get the distance between this vector and another given vector
     *
     * @param other the other vector
     * @return the distance between the two vectors
     */
    public float distanceFrom(Vector3f other) {
        return (float) Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
    }

    /**
     * Get an arbitrary perpendicular vector to this vector
     *
     * @return an arbitrary perpendicular vector
     */
    public Vector3f perpendicularVector() {

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
