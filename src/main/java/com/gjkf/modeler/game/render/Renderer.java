/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.game.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * The renderer class.
 * <p>Here lay all the useful functions regarding rendering.</p>
 */

public class Renderer {

    public Renderer() {
    }

    public void init() throws Exception {
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}