package com.vinhderful.raytracer.scene;

import com.vinhderful.raytracer.shapes.Sphere;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Represents a light source using its position and color
 */
public class Light extends Sphere {

    /**
     * Construct a Light object using its position and color
     * 
     * @param position
     *            the position
     * @param color
     *            the color
     */
    public Light(Vector3f position, Color color) {
        super(position, 0.1F, color);
    }

    /**
     * Get the color of the light source
     * 
     * @return the color of the light source
     */
    public Color getColor() {
        return color;
    }
}