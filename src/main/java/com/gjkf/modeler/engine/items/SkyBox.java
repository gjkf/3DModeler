/*
 * Created by Davide Cossu (gjkf), 11/15/2016
 */

package com.gjkf.modeler.engine.items;

import com.gjkf.modeler.engine.render.Material;
import com.gjkf.modeler.engine.render.Mesh;
import com.gjkf.modeler.engine.render.OBJLoader;
import com.gjkf.modeler.engine.render.Texture;

/**
 * Object representing a sky box.
 * <p>Extends {@link Item} so it's easy to access utility methods.</p>
 */

public class SkyBox extends Item{

    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxTexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }

}