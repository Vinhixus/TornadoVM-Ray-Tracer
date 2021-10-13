package com.vinhderful.raytracer.shapes;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Represents a horizontal checkerboard plane using its height
 */
public class Plane extends Body {

    /**
     * Construct a Plane object given its height
     * 
     * @param height
     *            the height of the plane
     */
    public Plane(float height) {
        super(new Vector3f(0, height, 0));
    }

    /**
     * Get the color of the plane at a given 3D point
     * 
     * @param point
     *            the 3D point
     * @return the color of the plane at the given point
     */
    @Override
    public Color getColor(Vector3f point) {
        if ((point.getX() > 0 & point.getZ() > 0) || (point.getX() < 0 & point.getZ() < 0)) {
            if ((int) point.getX() % 2 == 0 ^ (int) point.getZ() % 2 != 0)
                return Color.GRAY;
            else
                return Color.DARK_GRAY;
        } else {
            if ((int) point.getX() % 2 == 0 ^ (int) point.getZ() % 2 != 0)
                return Color.DARK_GRAY;
            else
                return Color.GRAY;
        }
    }

    /**
     * Given a ray, return the point where the ray first intersects with the plane
     *
     * @param ray
     *            the ray
     * @return The first point of intersection as Vector3f or null if the ray does
     *         not intersect with this plane
     */
    @Override
    public Vector3f getIntersection(Ray ray) {
        float t = -(ray.getOrigin().getY() - position.getY()) / ray.getDirection().getY();
        if (t > 0 && Float.isFinite(t))
            return ray.getOrigin().add(ray.getDirection().multiply(t));

        return null;
    }

    /**
     * Get the normal vector of the plane at a given point (always a normal vector
     * pointing up)
     *
     * @param point
     *            the point
     * @return the normal vector at the given point
     */
    @Override
    public Vector3f getNormalAt(Vector3f point) {
        return new Vector3f(0F, 1F, 0F);
    }
}
