package com.vinhderful.pathtracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import static com.vinhderful.pathtracer.misc.World.PLANE_INDEX;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.*;

/**
 * Operations on the objects in the scene
 */
public class BodyOps {

    /**
     * Given a hit object's position, size and a ray's origin and direction, return the intersection point of the ray
     * and the object.
     * -------------------------------------------------------------------------------------------------------------
     * - The first three elements of the returned Float4 represents the X, Y and Z values of the intersection point
     * respectively, (-1, -1, -1) is returned if the ray does not intersect with the object.
     * - The fourth element of the returned Float4 represents a boolean in the form of a float: 0 if there was an
     * intersection, and -1 if the ray does not intersect with the object.
     *
     * @param hitIndex     the index of the object
     * @param position     the position of the object
     * @param size         the size of the object
     * @param rayOrigin    the origin of the ray
     * @param rayDirection the direction of the ray
     * @return the intersection point alongside a variable indicating whether there was an intersection
     */
    public static Float4 getIntersection(int hitIndex, Float4 position, float size,
                                         Float4 rayOrigin, Float4 rayDirection) {

        // Define Float4 to return if there is no intersection
        final Float4 NO_INTERSECTION = new Float4(-1F, -1F, -1F, -1F);

        // Plane
        if (hitIndex == PLANE_INDEX) {
            float t = -(rayOrigin.getY() - position.getY()) / rayDirection.getY();
            if (t > 0 && Float.isFinite(t))
                return Float4.add(rayOrigin, Float4.mult(rayDirection, t));

            return NO_INTERSECTION;
        }

        // Sphere
        else {
            float t = Float4.dot(Float4.sub(position, rayOrigin), rayDirection);
            Float4 p = Float4.add(rayOrigin, Float4.mult(rayDirection, t));

            float y = Float4.length(Float4.sub(position, p));

            if (y < size) {
                float t1 = t - floatSqrt(size * size - y * y);
                if (t1 > 0) return Float4.add(rayOrigin, Float4.mult(rayDirection, t1));
                else return NO_INTERSECTION;
            } else
                return NO_INTERSECTION;
        }
    }

    /**
     * Given a position of an object and a point on it's surface, return the normal vector of the object's surface at
     * the given point
     *
     * @param hitIndex the index of the object
     * @param position the position of the object
     * @param point    the point of hte surface
     * @return the normal vector of the surface
     */
    public static Float4 getNormal(int hitIndex, Float4 position, Float4 point) {

        // Plane normal is an up vector in the y direction
        if (hitIndex == PLANE_INDEX) {
            return new Float4(0, 1F, 0, 0);
        }

        // Sphere normal will be the direction from sphere origin to the surface point
        else return Float4.normalise(Float4.sub(point, position));
    }

    /**
     * Given ab object's index, a point on the surface and the set of body colors of the objects in the scene,
     * return the color of the object at the given point on the object's surface
     *
     * @param hitIndex   the index of the object
     * @param point      the point of hte surface
     * @param bodyColors the structure containing the colors of the objects in the scene
     * @return the color of the object at the given point on the surface
     */
    public static Float4 getColor(int hitIndex, Float4 point, VectorFloat4 bodyColors) {

        // Get checkerboard pattern for plane
        if (hitIndex == PLANE_INDEX) {
            if ((int) (floor(point.getX()) + floor(point.getZ())) % 2 == 0)
                // GRAY
                return new Float4(0.5F, 0.5F, 0.5F, 0);
            else
                // DARK GRAY
                return new Float4(0.2F, 0.2F, 0.2F, 0);
        }

        // Else simply return body color
        else return bodyColors.get(hitIndex);
    }

    /**
     * Get the color of the skybox at a certain point on the surface
     * given by the direction pointing from the origin of the sphere to the surface point
     *
     * @param skybox           the structure representing the pixel colors of the skybox image
     * @param skyBoxDimensions the int array containing the width and the height of the skybox image
     * @param direction        the direction from the sphere origin to the surface
     * @return the UV-mapped color of the skybox at in the specified direction
     */
    public static Float4 getSkyboxColor(VectorFloat4 skybox, int[] skyBoxDimensions, Float4 direction) {

        // Convert unit vector to texture coordinates
        // https://en.wikipedia.org/wiki/UV_mapping#Finding_UV_on_a_sphere
        float u = 0.5F + floatAtan2(direction.getZ(), direction.getX()) / (2 * floatPI());
        float v = 0.5F - floatAsin(direction.getY()) / floatPI();

        // Get color from the skybox VectorFloat4
        int x = (int) (u * (skyBoxDimensions[0] - 1));
        int y = (int) (v * (skyBoxDimensions[1] - 1));
        return skybox.get(x + y * skyBoxDimensions[0]);
    }
}
