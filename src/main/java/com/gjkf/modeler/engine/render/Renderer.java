/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.engine.render;

import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.render.lights.DirectionalLight;
import com.gjkf.modeler.engine.render.lights.PointLight;
import com.gjkf.modeler.engine.render.lights.SpotLight;
import com.gjkf.modeler.game.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
    /**
     * The specular power.
     */
    private float specularPower;
    /**
     * The maximum number of point lights. See the fragment shader.
     */
    private static final int MAX_POINT_LIGHTS = 5;
    /**
     * The maximum number of spot lights. See the fragment shader.
     */
    private static final int MAX_SPOT_LIGHTS = 5;


    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
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
        // Create uniform for material
        shaderProgram.createMaterialUniform("material");
        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        shaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        shaderProgram.createDirectionalLightUniform("directionalLight");
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
     * @param ambientLight The ambient light.
     * @param pointLightList The point light list.
     * @param spotLightList The spot light list.
     * @param directionalLight The directional light.
     */

    public void render(Window window, Camera camera, Item[] items, Vector3f ambientLight,
                       PointLight[] pointLightList, SpotLight[] spotLightList, DirectionalLight directionalLight) {

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

        // Update Light Uniforms
        renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight);

        shaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for (Item item : items) {
            Mesh mesh = item.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(item, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

        shaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, Vector3f ambientLight, PointLight[] pointLightList, SpotLight[] spotLightList, DirectionalLight directionalLight) {

        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        // Process Point Lights
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();

            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            shaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);

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