/*
 * Created by Davide Cossu (gjkf), 11/13/2016
 */

package com.gjkf.modeler.game;

import com.gjkf.modeler.engine.IHud;
import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.TextItem;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.render.*;
import org.joml.Vector3f;

import java.awt.*;

public class Hud implements IHud{

    private static final Font FONT = new Font("Arial", Font.ITALIC, 40);

    private static final String CHARSET = "ISO-8859-1";

    private final Item[] items;

    private final TextItem statusTextItem;

    private final Item compassItem;


    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setColour(Colors.PURPLE.toVector());

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