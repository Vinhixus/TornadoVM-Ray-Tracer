package com.vinhderful.pathtracer.renderer;

import com.vinhderful.pathtracer.utils.BodyOps;
import com.vinhderful.pathtracer.utils.Color;
import com.vinhderful.pathtracer.utils.Float4Ext;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import static com.vinhderful.pathtracer.misc.World.LIGHT_INDEX;
import static com.vinhderful.pathtracer.utils.Angle.TO_RADIANS;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.floatTan;

/**
 * The Renderer class contains the main parallelized render method
 */
public class Renderer {


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
            Float4 intersection = BodyOps.getIntersection(i, bodyPositions.get(i), bodySizes.get(i), rayOrigin, rayDirection);

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
     * Calculate the OpenGL style x coordinate of the canvas, where (0, 0) is the middle of the screen
     *
     * @param width  the width of the canvas
     * @param height the height of the canvas
     * @param x      the pixel's x coordinate
     * @return the normalized OpenGL-style x coordinate
     */
    public static float getNormalizedX(int width, int height, int x) {
        if (width > height)
            return (x - width * 0.5F + height * 0.5F) / height * 2 - 1;
        else
            return x * 2F / width - 1;
    }

    /**
     * Calculate the OpenGL style y coordinate of the canvas, where (0, 0) is the middle of the screen
     *
     * @param width  the width of the canvas
     * @param height the height of the canvas
     * @param y      the pixel's y coordinate
     * @return the normalized OpenGL-style y coordinate
     */
    public static float getNormalizedY(int width, int height, int y) {
        if (width > height)
            return -(y * 2F / height - 1);
        else
            return -((y - height * 0.5F + width * 0.5F) / width * 2 - 1);
    }

    /**
     * The main render function takes information from the INPUT BUFFERS, performs path tracing and calculates an
     * INT_RGB value for each pixel writing to the OUTPUT BUFFER
     *
     * @param pixels                OUTPUT BUFFER - int array of size width * height, where the calculated
     *                              INT_RGB pixel colors are written
     * @param dimensions            INPUT BUFFER - 2 element int array containing dimensions of the canvas
     *                              0 - width; 1 - height
     * @param camera                INPUT BUFFER - 6 element float array containing camera properties
     *                              0, 1, 2 - x, y, z coordinates of position; 3, 4 - yaw, pitch; 5 - fov
     * @param pathTracingProperties INPUT BUFFER - 2 element int array containing:
     *                              0 - shadow sample size; 1 - reflection bounce limit
     * @param bodyPositions         INPUT BUFFER - VectorFloat4 containing positions of the objects in the scene
     * @param bodySizes             INPUT BUFFER - VectorFloat4 containing sizes of the objects in the scene
     * @param bodyColors            INPUT BUFFER - VectorFloat4 containing colors of the objects in the scene
     * @param bodyReflectivities    INPUT BUFFER - VectorFloat4 containing reflectivities of the objects in the scene
     * @param skybox                INPUT BUFFER - VectorFloat4 representing the skybox colors
     * @param skyboxDimensions      INPUT BUFFER - 2 element int array containing the dimensions of the skybox image
     *                              0 - skybox image width; 1 - skybox image height
     */
    public static void render(int[] pixels, int[] dimensions, float[] camera, int[] pathTracingProperties,
                              VectorFloat4 bodyPositions, VectorFloat bodySizes, VectorFloat4 bodyColors, VectorFloat bodyReflectivities,
                              VectorFloat4 skybox, int[] skyboxDimensions) {

        // Relatively to the viewport, the camera will be placed in the middle, with exactly one unit of distance to
        // the viewport calculated by the field of view (camera[5] = fov)
        // https://docs.microsoft.com/en-us/windows/win32/direct3d9/viewports-and-clipping
        Float4 relativeCameraPosition = new Float4(0, 0, -1 / floatTan(camera[5] * 0.5F * TO_RADIANS), 0);
        Float4 cameraPosition = new Float4(camera[0], camera[1], camera[2], 0);

        // Get dimensions of the viewport
        int width = dimensions[0];
        int height = dimensions[1];

        // Get path tracing properties
        int shadowSampleSize = pathTracingProperties[0];
        int reflectionBounceLimit = pathTracingProperties[1];

        // The main parallel loop - each pixel color can be calculated independently of one another
        for (@Parallel int x = 0; x < width; x++)
            for (@Parallel int y = 0; y < height; y++) {

                // Acquire the OpenGL-style coordinates of the pixel, where 0, 0 is the middle
                Float4 normalizedCoords = new Float4(getNormalizedX(width, height, x), getNormalizedY(width, height, y), 0, 0);

                // Acquire direction to each pixel by rotating it around the camera yaw and pitch
                Float4 rayDirection = Float4Ext.rotate(Float4.normalise(Float4.sub(normalizedCoords, relativeCameraPosition)), camera[3], camera[4]);

                // Shoot ray into the scene to get the closest hit
                Float4 hit = getClosestHit(bodyPositions, bodySizes, cameraPosition, rayDirection);
                int hitIndex = (int) hit.getW();

                // If the ray hits an object
                if (hitIndex != -1) {

                    // If the hit object is the light source, then simply paint the light source's color
                    // This will give a flat white circle is a white sphere light
                    if (hitIndex == LIGHT_INDEX) {
                        pixels[x + y * width] = Color.toInt(bodyColors.get(LIGHT_INDEX));
                    }

                    // If the hit object is not a light source, then compute the pixel color after calculating
                    // shading reflections and shadows
                    else {
                        Float4 hitPosition = new Float4(hit.getX(), hit.getY(), hit.getZ(), 0);
                        Float4 pixelColor = Shader.getPixelColor(
                                hitIndex, hitPosition, cameraPosition, rayDirection,
                                bodyPositions, bodySizes, bodyColors, bodyReflectivities,
                                skybox, skyboxDimensions,
                                shadowSampleSize, reflectionBounceLimit);

                        pixels[x + y * width] = Color.toInt(pixelColor);
                    }
                }

                // If the ray doesn't hit anny objects, then draw the background skybox
                else {
                    pixels[x + y * width] = Color.toInt(BodyOps.getSkyboxColor(skybox, skyboxDimensions, rayDirection));
                }
            }
    }
}
