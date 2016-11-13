/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.engine.render;

import com.gjkf.modeler.engine.IHud;
import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.render.lights.DirectionalLight;
import com.gjkf.modeler.engine.render.lights.PointLight;
import com.gjkf.modeler.engine.render.lights.SceneLight;
import com.gjkf.modeler.engine.render.lights.SpotLight;
import com.gjkf.modeler.engine.Utils;
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
     * The shader program for the scene.
     */
    private ShaderProgram sceneShaderProgram;
    /**
     * The shader program for the hud.
     */
    private ShaderProgram hudShaderProgram;
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
     * @throws Exception If the program could not create the shaders.
     */

    public void init() throws Exception {
        setupSceneShader();
        setupHudShader();
    }

    /**
     * Sets up the shaders for the 3D scene.
     *
     * @throws Exception If anything went wrong.
     */

    private void setupSceneShader() throws Exception {
        // Create shader
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.glsl"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("shaders/fragment.glsl"));
        sceneShaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");
        sceneShaderProgram.createUniform("texture_sampler");
        // Create uniform for material
        sceneShaderProgram.createMaterialUniform("material");
        // Create lighting related uniforms
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");
    }

    /**
     * Sets up the shaders for the HUD.
     *
     * @throws Exception If anything went wrong.
     */

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(Utils.loadResource("shaders/hudVertex.glsl"));
        hudShaderProgram.createFragmentShader(Utils.loadResource("shaders/hudFragment.glsl"));
        hudShaderProgram.link();

        // Create uniforms for Ortographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("colour");
        hudShaderProgram.createUniform("hasTexture");
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
     * @param sceneLight The lights.
     * @param hud The hud.
     */

    public void render(Window window, Camera camera, Item[] items, SceneLight sceneLight, IHud hud) {
        clear();

        if(window.isResized()){
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        renderScene(window, camera, items, sceneLight);

        renderHud(window, hud);
    }

    /**
     * Renders the scene.
     *
     * @param window The window.
     * @param camera The camera.
     * @param items The items to be drawn.
     * @param sceneLight The scene lighting.
     */

    public void renderScene(Window window, Camera camera, Item[] items, SceneLight sceneLight) {

        sceneShaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        renderLights(viewMatrix, sceneLight);

        sceneShaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for (Item item : items) {
            Mesh mesh = item.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(item, viewMatrix);
            sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            sceneShaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

        sceneShaderProgram.unbind();
    }

    /**
     * Renders the lights.
     *
     * @param viewMatrix The view matrix.
     * @param sceneLight The lights in the scene.
     */

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {

        sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
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
            sceneShaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
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

            sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShaderProgram.setUniform("directionalLight", currDirLight);
    }

    /**
     * Renders the HUD.
     *
     * @param window The window.
     * @param hud The hud.
     */

    private void renderHud(Window window, IHud hud) {
        hudShaderProgram.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (Item gameItem : hud.getItems()) {
            Mesh mesh = gameItem.getMesh();
            // Set ortohtaphic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrtoProjModelMatrix(gameItem, ortho);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("colour", gameItem.getMesh().getMaterial().getColour());
            hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

            // Render the mesh for this HUD item
            mesh.render();
        }

        hudShaderProgram.unbind();
    }

    /**
     * Cleans up the resources used in the {@link #sceneShaderProgram} and {@link #hudShaderProgram}.
     */

    public void cleanup() {
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanup();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
    }


}