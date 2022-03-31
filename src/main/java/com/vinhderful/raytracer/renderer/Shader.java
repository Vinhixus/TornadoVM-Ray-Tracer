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
package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Body;
import com.vinhderful.raytracer.scene.Light;
import com.vinhderful.raytracer.scene.World;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Hit;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Implementations of the Phong shading techniques
 */
public class Shader {

    /**
     * Constant values to tweak lighting and shadows
     */
    public static final float AMBIENT_STRENGTH = 0.15F;
    public static final float MAX_REFLECTIVITY = 128F;
    public static final float SHADOW_STRENGTH = 0.25F;

    /**
     * PHI value for shadow sampling with sunflower seed arrangement
     */
    public static final float PHI = (float) (Math.PI * (3 - Math.sqrt(5)));

    /**
     * Get the diffuse brightness of a body given a hit event and the world according to
     * the Phong lighting model:
     * https://learnopengl.com/Lighting/Basic-Lighting
     *
     * @param hit   the hit event
     * @param world the world
     * @return the diffuse brightness
     */
    public static float getDiffuse(Hit hit, World world) {
        Light light = world.getLight();
        Vector3f lightDirection = light.getPosition().subtract(hit.getPosition()).normalize();

        // Diffuse lighting from normal and light direction
        return hit.getNormal().dotProduct(lightDirection);
    }

    /**
     * Get the specular brightness of a body given a hit event and the world according to
     * the Blinn-Phong lighting model:
     * https://learnopengl.com/Advanced-Lighting/Advanced-Lighting
     *
     * @param hit   the hit event
     * @param world the world
     * @return the specular brightness
     */
    public static float getSpecular(Hit hit, World world) {
        Light light = world.getLight();
        Vector3f hitPosition = hit.getPosition();
        float bodyReflectivity = hit.getBody().getReflectivity();

        Vector3f lightDirection = light.getPosition().subtract(hitPosition).normalize();
        Vector3f rayDirection = hit.getRay().getOrigin().subtract(hitPosition).normalize();

        Vector3f halfwayDirection = lightDirection.add(rayDirection).normalize();
        float specularFactor = Math.max(0F, hit.getNormal().dotProduct(halfwayDirection));

        // Specular energy conservation
        // https://www.rorydriscoll.com/2009/01/25/energy-conservation-in-games/
        float k = (float) ((8.0 + bodyReflectivity) / (8.0 * Math.PI));


        return (float) (k * Math.pow(specularFactor, bodyReflectivity) * (bodyReflectivity / MAX_REFLECTIVITY));
    }

    /**
     * Get the factor that defines if a spot should be in shadow
     * Light is sampled using the sunflower seed arrangement/vogel spiral phenomenon
     * https://www.codeproject.com/Articles/1221341/The-Vogel-Spiral-Phenomenon
     *
     * @param hit   the hit event
     * @param world the world
     * @return the shadow factor
     */
    public static float getShadowFactor(Hit hit, World world, int sampleSize) {

        Light light = world.getLight();
        float lightScale = light.getScale();
        Vector3f lightPosition = light.getPosition();
        Vector3f hitPosition = hit.getPosition();

        // As the light is bounded by a sphere light model, we uniformly sample the great circle
        // with a normal parallel to our light direction

        // We obtain a plane perpendicular to the light direction, generate two perpendicular vectors
        // that form a new coordinate system together with the light direction vector
        Vector3f n = hitPosition.subtract(lightPosition).normalize();
        Vector3f u = n.perpendicularVector();
        Vector3f v = n.crossProduct(u);

        int raysHit = 0;

        // Uniformly sample the great circle using the sunflower seed arrangement
        for (int i = 0; i < sampleSize; i++) {

            float t = PHI * i;
            float r = (float) Math.sqrt((float) i / sampleSize);

            float x = (float) (2 * lightScale * r * Math.cos(t));
            float y = (float) (2 * lightScale * r * Math.sin(t));

            // Translate points to plane
            Vector3f samplePoint = lightPosition.add(u.multiply(x)).add(v.multiply(y));
            Vector3f rayDirection = samplePoint.subtract(hitPosition).normalize();
            Vector3f rayOrigin = hitPosition.add(rayDirection.multiply(0.001F));
            Ray sampleRay = new Ray(rayOrigin, rayDirection);

            Hit sampleHit = Renderer.getClosestHit(sampleRay, world);
            if (sampleHit != null && sampleHit.getBody() != light)
                raysHit++;
        }

        if (raysHit == 0) return 1;
        else return 1 - (float) raysHit / (sampleSize * (1 + SHADOW_STRENGTH));
    }

    /**
     * Recursively bounce ray in the given world and compute colors according to the
     * reflectivities of the hit objects until the recursion limit is reached
     *
     * @param hit                   the hit event
     * @param world                 the world
     * @param shadowSampleSize      number of samples for soft shadow sampling
     * @param reflectionBounceLimit the limit of how many times the ray is bounced
     * @return the resulting final pixel color
     */
    public static Color getPixelColor(Hit hit, World world, int shadowSampleSize, int reflectionBounceLimit) {

        Vector3f hitPosition = hit.getPosition();
        Vector3f rayDirection = hit.getRay().getDirection();
        Body hitBody = hit.getBody();

        Color hitColor = hitBody.getColor(hitPosition);

        // Generate Blinn-Phong model variables
        float diffuse = Math.max(AMBIENT_STRENGTH, getDiffuse(hit, world));
        float specular = getSpecular(hit, world);
        float reflectivity = hitBody.getReflectivity() / MAX_REFLECTIVITY;
        float shadow = getShadowFactor(hit, world, shadowSampleSize);

        // Recursively bounce ray around the scene and calculate reflections
        Color reflection;
        Vector3f reflectionDirection = rayDirection.subtract(hit.getNormal().multiply(2 * rayDirection.dotProduct(hit.getNormal())));
        Vector3f reflectionOrigin = hitPosition.add(reflectionDirection.multiply(0.001F));
        Hit reflectionHit = reflectionBounceLimit > 0 ? Renderer.getClosestHit(new Ray(reflectionOrigin, reflectionDirection), world) : null;

        if (reflectionHit != null)
            if (reflectionHit.getBody() == world.getLight())
                reflection = world.getLight().getColor();
            else
                reflection = getPixelColor(reflectionHit, world, shadowSampleSize, reflectionBounceLimit - 1);
        else
            reflection = world.getSkybox().getColor(reflectionDirection);

        if (hitBody == world.getPlane())
            return hitColor.mix(reflection, reflectivity).add(specular).multiply(shadow);
        else
            return hitColor.mix(reflection, reflectivity).multiply(diffuse).add(specular).multiply(shadow);
    }
}
