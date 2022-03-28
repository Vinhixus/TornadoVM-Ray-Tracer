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
package com.vinhderful.raytracer.misc;

import com.vinhderful.raytracer.misc.bodies.Body;
import uk.ac.manchester.tornado.api.collections.types.Float4;

import java.util.ArrayList;

import static com.vinhderful.raytracer.misc.World.SPHERES_START_INDEX;

/**
 * The physics class includes logic for gravity and collision detection using the Verlet Integration.
 * https://en.wikipedia.org/wiki/Verlet_integration
 */
public class Physics {

    /**
     * Gravity represented by a direction vector
     */
    public static final Float4 GRAVITY = new Float4(0, -16F, 0, 0);

    /**
     * Elasticity defines how bouncy the collisions are
     */
    public static final float ELASTICITY = 0.5F;

    /**
     * The world and the bodies within it
     */
    private final World world;
    private final ArrayList<Body> bodies;

    /**
     * Construct a physics service given a world to operate on
     *
     * @param world the world containing the spheres to perform physics calculations on
     */
    public Physics(World world) {
        this.world = world;
        this.bodies = world.getBodies();
    }

    /**
     * Define position updates from frame to frame according to the Verlet integration
     */
    public void update() {

        // Update body positions according to gravitational pull
        for (int i = SPHERES_START_INDEX; i < bodies.size(); i++) {
            Body b = bodies.get(i);

            Float4 tmp = b.getPosition().duplicate();
            float t = 1F / 60;
            b.setPosition(Float4.add(b.getPosition(), Float4.add(Float4.sub(b.getPosition(), b.getPrevPosition()), Float4.mult(GRAVITY, t * t))));
            b.setPrevPosition(tmp);
        }

        // Perform collision detection between spheres
        collide();

        // Perform collision detection between spheres and scene borders
        borderCollide();
    }

    /**
     * Perform collision detection between spheres
     */
    private void collide() {
        for (int i = SPHERES_START_INDEX; i < bodies.size(); i++) {
            Body b1 = bodies.get(i);

            for (int j = i + 1; j < bodies.size(); j++) {
                Body b2 = bodies.get(j);

                Float4 diff = Float4.sub(b1.getPosition(), b2.getPosition());
                float length = Float4.length(diff);
                float target = b1.getSize() + b2.getSize();

                if (length < target) {
                    float factor = (length - target) / length;

                    b1.setPosition(Float4.sub(b1.getPosition(), Float4.mult(diff, factor * ELASTICITY)));
                    b2.setPosition(Float4.add(b2.getPosition(), Float4.mult(diff, factor * ELASTICITY)));
                }
            }
        }
    }

    /**
     * Perform collision detection between spheres and scene borders
     */
    private void borderCollide() {
        for (int i = SPHERES_START_INDEX; i < bodies.size(); i++) {
            Body b = bodies.get(i);
            float size = b.getSize();

            float x = b.getPosition().getX();
            float y = b.getPosition().getY();
            float z = b.getPosition().getZ();

            float boxSize = world.getPlane().getSize() * 0.5F;

            // Boundaries in X direction
            if (x - size < -boxSize) {
                float vx = (b.getPrevPosition().getX() - b.getPosition().getX()) * ELASTICITY;
                b.setPosX(size - boxSize);
                b.setPrevPosX(b.getPosition().getX() - vx);
            } else if (x + size > boxSize) {
                float vx = (b.getPrevPosition().getX() - b.getPosition().getX()) * ELASTICITY;
                b.setPosX(boxSize - size);
                b.setPrevPosX(b.getPosition().getX() - vx);
            }

            // Boundaries in Y direction
            if (y - size < -boxSize) {
                float vy = (b.getPrevPosition().getY() - b.getPosition().getY()) * ELASTICITY;
                b.setPosY(size - boxSize);
                b.setPrevPosY(b.getPosition().getY() - vy);
            } else if (y + size > boxSize) {
                float vy = (b.getPrevPosition().getY() - b.getPosition().getY()) * ELASTICITY;
                b.setPosY(boxSize - size);
                b.setPrevPosY(b.getPosition().getY() - vy);
            }

            // Boundaries in Z direction
            if (z - size < -boxSize) {
                float vz = (b.getPrevPosition().getZ() - b.getPosition().getZ()) * ELASTICITY;
                b.setPosZ(size - boxSize);
                b.setPrevPosZ(b.getPosition().getZ() - vz);
            } else if (z + size > boxSize) {
                float vz = (b.getPrevPosition().getZ() - b.getPosition().getZ()) * ELASTICITY;
                b.setPosZ(boxSize - size);
                b.setPrevPosZ(b.getPosition().getZ() - vz);
            }
        }
    }
}
