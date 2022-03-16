package com.vinhderful.pathtracer.utils;

import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
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

            float y = Float4Ext.distance(position, p);

            if (y < size) {
                float t1 = t - floatSqrt(size * size - y * y);
                if (t1 > 0) return Float4.add(rayOrigin, Float4.mult(rayDirection, t1));
                else return NO_INTERSECTION;
            } else
                return NO_INTERSECTION;
        }
    }


    /**
     * Given the positions and sizes of the objects in the scene, and a shadow feeler ray represented by an origin and
     * a direction vector, alongside the position of the light, return whether the shadow feeler hits any objects,
     * thus blocking the light
     *
     * @param bodyPositions the structure representing the positions of the objects in the scene
     * @param bodySizes     the structure representing the sizes of the objects in the scene
     * @param rayOrigin     the origin point of the ray
     * @param rayDirection  the direction of the ray
     * @param lightPosition the position of the light
     * @return if the shadow feeler hits any objects
     */
    public static boolean intersects(VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                     Float4 rayOrigin, Float4 rayDirection,
                                     Float4 lightPosition) {

        // Calculate the distance to the light
        float lightDistance = Float4Ext.distance(rayOrigin, lightPosition);

        // Initialise intersects boolean
        boolean intersects = false;

        // Loop over objects in the scene, excluding light and plane, break out of loop when intersection is found
        for (int i = 2; i < bodyPositions.getLength() && !intersects; i++) {

            // Calculate the intersection of the ray with the current object
            Float4 intersection = BodyOps.getIntersection(i, bodyPositions.get(i), bodySizes.get(i), rayOrigin, rayDirection);

            // If the ray hits the object, and distance to the current object is smaller than the distance to the light,
            // then the object is in the way of the light and blocking it
            if (intersection.getW() == 0 && Float4Ext.distance(intersection, rayOrigin) < lightDistance)
                intersects = true;
        }

        // Return result
        return intersects;
    }

    /**
     * Given the positions and sizes of the objects in the scene, and a ray represented by an origin and a direction
     * vector, return the position of the first point the ray hits, alongside the index of the hit object.
     * -------------------------------------------------------------------------------------------------------------
     * - The first three elements of the returned Float4 represents the X, Y and Z values of the hit position
     * respectively, (-1, -1, -1) is returned if no objects are hit.
     * - The fourth element of the returned Float4 represents the index of the hit object, -1 is returned if no objects
     * are hit
     *
     * @param bodyPositions the structure representing the positions of the objects in the scene
     * @param bodySizes     the structure representing the sizes of the objects in the scene
     * @param rayOrigin     the origin point of the ray
     * @param rayDirection  the direction of the ray
     * @return the closest hit position alongside the hit objects index
     */
    public static Float4 getClosestHit(VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                       Float4 rayOrigin, Float4 rayDirection) {

        // Initialise closes hit as a no-hit
        Float4 closestHit = new Float4(-1F, -1F, -1F, -1F);

        // Loop over objects in the scene
        for (int i = 0; i < bodyPositions.getLength(); i++) {

            // Calculate the intersection of the ray with the current object
            Float4 intersection = getIntersection(i, bodyPositions.get(i), bodySizes.get(i), rayOrigin, rayDirection);

            // If the ray hits the object, and the previous closest hit distance is larger than the distance of
            // the ray origin and the current object, then the current hit is the closest hit
            if (intersection.getW() == 0 && (closestHit.getW() == -1F ||
                    Float4Ext.distance(closestHit, rayOrigin) > Float4Ext.distance(intersection, rayOrigin)))
                closestHit = new Float4(intersection.getX(), intersection.getY(), intersection.getZ(), i);
        }

        // Return the resulting closest hit
        return closestHit;
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
