/*
 * Created by Davide Cossu (gjkf), 11/13/2016
 */

package com.gjkf.modeler.game;

import com.gjkf.modeler.engine.IHud;
import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.TextItem;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.render.Material;
import com.gjkf.modeler.engine.render.Mesh;
import com.gjkf.modeler.engine.render.OBJLoader;
import org.joml.Vector3f;

public class Hud implements IHud{

    private static final int FONT_COLS = 16;

    private static final int FONT_ROWS = 16;

    private static final String FONT_TEXTURE = "/textures/font_texture.png";

    private final Item[] items;

    private final TextItem statusTextItem;

    private final Item compassItem;


    public Hud(String statusText) throws Exception {
        this.statusTextItem = new TextItem(statusText, FONT_TEXTURE, FONT_COLS, FONT_ROWS);
        this.statusTextItem.getMesh().getMaterial().setColour(new Vector3f(1, 1, 1));

        // Create compass
        Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
        Material material = new Material();
        material.setColour(new Vector3f(1, 0, 0));
        mesh.setMaterial(material);
        compassItem = new Item(mesh);
        compassItem.setScale(40.0f);
        // Rotate to transform it to screen coordinates
        compassItem.setRotation(0f, 0f, 180f);

        // Create list that holds the items that compose the HUD
        items = new Item[]{statusTextItem, compassItem};
    }

    public void rotateCompass(float angle) {
        this.compassItem.setRotation(0, 0, 180 + angle);
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    @Override
    public Item[] getItems() {
        return items;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
        this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
    }

}