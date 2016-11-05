/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.game;

import com.gjkf.modeler.engine.ILogic;
import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.MouseInput;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.render.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements ILogic{

    private static final float MOUSE_SENSITIVITY = 0.3f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private Item[] items;

    private final Camera camera;

    private static final float CAMERA_POS_STEP = 0.05f;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        mesh.setTexture(texture);
        Item item = new Item(mesh);
        item.setScale(0.25f);
        item.setPosition(0, 0, -2);
        items = new Item[]{item};
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

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        for(Item i : items){
            float rotation = i.getRotation().z + 1.5f;
            if ( rotation > 360 ) {
                rotation = 0;
            }
            i.setRotation(rotation, rotation, rotation);
        }

    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, items);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (Item gameItem : items) {
            gameItem.getMesh().cleanUp();
        }
    }
}