package com.vinhderful.raytracer.misc;

import com.vinhderful.raytracer.misc.bodies.Body;
import com.vinhderful.raytracer.misc.bodies.Light;
import com.vinhderful.raytracer.misc.bodies.Plane;
import com.vinhderful.raytracer.misc.bodies.Sphere;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.collections.types.VectorInt;

import java.util.ArrayList;

public class World {

    private Float4 backgroundColor;
    private final ArrayList<Body> bodies;

    private VectorInt bodyTypes;
    private VectorFloat4 bodyPositions;
    private VectorFloat bodySizes;
    private VectorFloat4 bodyColors;
    private VectorFloat bodyReflectivities;

    public World() {

        // Populate world
        bodies = new ArrayList<>();
        generateDefaultWorld();

        // Make sure we have 1 light and 1 plane at the first two indexes
        assert bodies.size() >= 2;
        assert bodies.get(0).getType() == 0;
        assert bodies.get(1).getType() == 1;

        // Generate the tornado compatible vector representations of the bodies
        generateTornadoCompatibleData();
    }

    public void generateDefaultWorld() {

        // Background Color
        backgroundColor = Color.BLACK;

        // Sphere light
        Light light = new Light(new Float4(1F, 3F, -1.5F, 0), 0.4F, Color.WHITE);
        addBody(light);

        // Checkerboard plane
        Plane plane = new Plane(0, 16F);
        addBody(plane);

        // Spheres in the scene
        addBody(new Sphere(new Float4(0, 1F, 0, 0), 1F, Color.GRAY, 32F));
        addBody(new Sphere(new Float4(3F, 0.5F, 0, 0), 0.5F, Color.RED, 8F));
        addBody(new Sphere(new Float4(4.5F, 0.5F, 0, 0), 0.5F, Color.GREEN, 16F));
        addBody(new Sphere(new Float4(6F, 0.5F, 0, 0), 0.5F, Color.BLUE, 32F));
    }

    private void generateTornadoCompatibleData() {
        int numBodies = bodies.size();

        bodyTypes = new VectorInt(numBodies);
        bodyPositions = new VectorFloat4(numBodies);
        bodySizes = new VectorFloat(numBodies);
        bodyColors = new VectorFloat4(numBodies);
        bodyReflectivities = new VectorFloat(numBodies);

        for (int i = 0; i < numBodies; i++) {
            Body body = bodies.get(i);

            bodyTypes.set(i, body.getType());
            bodyPositions.set(i, body.getPosition());
            bodySizes.set(i, body.getSize());
            bodyColors.set(i, body.getColor());
            bodyReflectivities.set(i, body.getReflectivity());
        }
    }

    private void addBody(Body body) {
        bodies.add(body);
    }

    public Float4 getBackgroundColor() {
        return backgroundColor;
    }

    public VectorInt getBodyTypes() {
        return bodyTypes;
    }

    public VectorFloat4 getBodyPositions() {
        return bodyPositions;
    }

    public VectorFloat getBodySizes() {
        return bodySizes;
    }

    public VectorFloat4 getBodyColors() {
        return bodyColors;
    }

    public VectorFloat getBodyReflectivities() {
        return bodyReflectivities;
    }
}
