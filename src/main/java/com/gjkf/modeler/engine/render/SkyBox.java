/*
 * Created by Davide Cossu (gjkf), 11/15/2016
 */

package com.gjkf.modeler.engine.render;

import com.gjkf.modeler.engine.Item;

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