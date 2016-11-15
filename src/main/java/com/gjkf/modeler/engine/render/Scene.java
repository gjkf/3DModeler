/*
 * Created by Davide Cossu (gjkf), 11/15/2016
 */

package com.gjkf.modeler.engine.render;

import com.gjkf.modeler.engine.Item;
import com.gjkf.modeler.engine.render.lights.SceneLight;

public class Scene{

    private Item[] items;

    private SkyBox skyBox;

    private SceneLight sceneLight;

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }


}