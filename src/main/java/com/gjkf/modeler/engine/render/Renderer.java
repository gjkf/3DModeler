/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.engine.render;

import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.game.Utils;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * The renderer class.
 * <p>Here lay all the useful functions regarding rendering.</p>
 */

public class Renderer {

    /**
     * Field of View in Radians.
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    /**
     * The near distance of the frustum.
     */
    private static final float Z_NEAR = 0.01f;
    /**
     * The far distance of the frustum
     */
    private static final float Z_FAR = 1000.f;
    /**
     * Instance of {@link Transformation}
     */
    private final Transformation transformation;
    /**
     * Instance of {@link ShaderProgram}
     */
    private ShaderProgram shaderProgram;

    public Renderer() {
        transformation = new Transformation();
    }

    /**
     * Initializes the shader program.
     *
     * @param window The window.
     *
     * @throws Exception If the program could not create the shaders.
     */

    public void init(Window window) throws Exception {
        // Create shader
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.glsl"));
        shaderProgram.createFragmentShader(Utils.loadResource("shaders/fragment.glsl"));
        shaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        // Create uniform for default colour and the flag that controls it
        shaderProgram.createUniform("colour");
        shaderProgram.createUniform("useColour");
    }

    /**
     * Clears the OpenGL buffers.
     */

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Renders the objects considering the camera position.
     *
     * @param window The window.
     * @param camera The camera.
     * @param items The items to draw.
     */

    public void render(Window window, Camera camera, Item[] items) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for(Item item : items) {
            Mesh mesh = item.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(item, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            shaderProgram.setUniform("colour", mesh.getColour());
            shaderProgram.setUniform("useColour", mesh.isTextured() ? 0 : 1);
            mesh.render();
        }

        shaderProgram.unbind();
    }

    /**
     * Cleans up the resources used in the {@link #shaderProgram}
     */

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

}