/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.test.game;

import com.gjkf.modeler.engine.ILogic;
import com.gjkf.modeler.engine.MouseInput;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.items.Item;
import com.gjkf.modeler.engine.items.Terrain;
import com.gjkf.modeler.engine.render.*;
import com.gjkf.modeler.engine.render.lights.DirectionalLight;
import com.gjkf.modeler.engine.render.lights.SceneLight;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements ILogic{

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private Hud hud;

    private Terrain terrain;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init();

        scene = new Scene();

        // Setup Items
        float reflectance = 0.35f;
        Texture normalMap = new Texture("/textures/rock_normals.png");

        Mesh quadMesh1 = OBJLoader.loadMesh("/models/quad.obj");
        Texture texture = new Texture("/textures/rock.png");
        Material quadMaterial1 = new Material(texture, reflectance);
        quadMesh1.setMaterial(quadMaterial1);
        Item quadGameItem1 = new Item(quadMesh1);
        quadGameItem1.setPosition(-3f, 0, 0);
        quadGameItem1.setScale(2.0f);
        quadGameItem1.setRotation(90, 0, 0);

        Mesh quadMesh2 = OBJLoader.loadMesh("/models/quad.obj");
        Material quadMaterial2 = new Material(texture, reflectance);
        quadMaterial2.setNormalMap(normalMap);
        quadMesh2.setMaterial(quadMaterial2);
        Item quadGameItem2 = new Item(quadMesh2);
        quadGameItem2.setPosition(3f, 0, 0);
        quadGameItem2.setScale(2.0f);
        quadGameItem2.setRotation(90, 0, 0);

        scene.setItems(new Item[]{quadGameItem1, quadGameItem2});

        // Setup Lights
        setupLights();

        camera.getPosition().y = 5.0f;
        camera.getRotation().x = 90;
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(-1, 0, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(Colors.WHITE.toVector(), lightPosition, lightIntensity));
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if(window.isKeyPressed(GLFW_KEY_W)){
            cameraInc.z = -1;
        }else if(window.isKeyPressed(GLFW_KEY_S)){
            cameraInc.z = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_A)){
            cameraInc.x = -1;
        }else if (window.isKeyPressed(GLFW_KEY_D)){
            cameraInc.x = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
            cameraInc.y = -1;
        }else if(window.isKeyPressed(GLFW_KEY_SPACE)){
            cameraInc.y = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_LEFT)){
            lightAngle -= 2.5f;
            if(lightAngle < -90){
                lightAngle = -90;
            }
        }else if(window.isKeyPressed(GLFW_KEY_RIGHT)){
            lightAngle += 2.5f;
            if(lightAngle > 90){
                lightAngle = 90;
            }
        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height
        float height = terrain != null ? terrain.getHeight(camera.getPosition()) : -Float.MAX_VALUE;
        if (camera.getPosition().y <= height) {
            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }

        // Update directional light direction, intensity and colour
        SceneLight sceneLight = scene.getSceneLight();
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window){
        if(hud != null){
            hud.updateSize(window);
        }
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        if(hud != null){
            hud.cleanup();
        }
    }
}