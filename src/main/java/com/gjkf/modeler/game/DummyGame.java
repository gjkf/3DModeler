/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.game;

import com.gjkf.modeler.engine.ILogic;
import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.MouseInput;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.render.*;
import com.gjkf.modeler.engine.render.lights.DirectionalLight;
import com.gjkf.modeler.engine.render.lights.PointLight;
import com.gjkf.modeler.engine.render.lights.SceneLight;
import com.gjkf.modeler.engine.render.lights.SpotLight;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements ILogic{

    private static final float MOUSE_SENSITIVITY = 0.3f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private Item[] items;

    private final Camera camera;

    private SceneLight sceneLight;

    private float lightAngle;

    private float spotAngle = 0;

    private float spotInc = 1;

    private static final float CAMERA_POS_STEP = 0.05f;

    private Hud hud;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init();

        float reflectance = 1f;
        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        //Material material = new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance);

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);

        mesh.setMaterial(material);
        Item item = new Item(mesh);
        item.setScale(0.5f);
        item.setPosition(0, 0, -2);
        //gameItem.setPosition(0, 0, -2);
        //gameItem.setScale(0.1f);
        //gameItem.setPosition(0, 0, -2);
        //gameItem.setPosition(0, 0, -0.2f);
        items = new Item[]{item};

        sceneLight = new SceneLight();

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

        // Point Light
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(Colors.WHITE.toVector(), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        sceneLight.setPointLightList(new PointLight[]{pointLight});

        // Spot Light
        lightPosition = new Vector3f(0, 0.0f, 10f);
        pointLight = new PointLight(Colors.GREEN.toVector(), lightPosition, lightIntensity);
        att = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        sceneLight.setSpotLightList(new SpotLight[]{spotLight, new SpotLight(spotLight)});

        lightPosition = new Vector3f(-1, 0, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(Colors.BLUE.toVector(), lightPosition, lightIntensity));

        // Create HUD
        hud = new Hud("DEMO");
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
        float lightPos = spotLightList[0].getPointLight().getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            spotLightList[0].getPointLight().getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            spotLightList[0].getPointLight().getPosition().z = lightPos - 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if(mouseInput.isLeftButtonPressed()){
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            hud.rotateCompass(camera.getRotation().y);
        }

        // Update spot light direction
        spotAngle += spotInc * 0.05f;
        if(spotAngle > 2){
            spotInc = -1;
        }else if (spotAngle < -2){
            spotInc = 1;
        }

        double spotAngleRad = Math.toRadians(spotAngle);
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
        Vector3f coneDir = spotLightList[0].getConeDirection();
        coneDir.y = (float) Math.sin(spotAngleRad);

        // Update directional light direction, intensity and colour
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        lightAngle += 1.1f;
        if(lightAngle > 90){
            directionalLight.setIntensity(0);
            if(lightAngle >= 360){
                lightAngle = -90;
            }
        }else if(lightAngle <= -80 || lightAngle >= 80){
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        }else{
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);

        for(Item i : items){
            float rotation = i.getRotation().z + 1.5f;
            if(rotation > 360){
                rotation = 0;
            }
            i.setRotation(rotation, rotation, rotation);
        }
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, camera, items, sceneLight, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(Item item : items){
            item.getMesh().cleanUp();
        }
    }
}