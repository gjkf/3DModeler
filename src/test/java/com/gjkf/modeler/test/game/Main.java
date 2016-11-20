/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.test.game;

import com.gjkf.modeler.engine.Engine;
import com.gjkf.modeler.engine.ILogic;

public class Main{

    public static void main(String[] args){
        SharedLibraryLoader.load();
        String os = System.getProperty("os.name").toLowerCase();
        /* Mac OS X needs headless mode for AWT */
        if (os.contains("mac")) {
            System.setProperty("java.awt.headless", "true");
        }

        try {
            boolean vSync = true;
            ILogic gameLogic = new DummyGame();
            Engine gameEng = new Engine("Game", 800, 800, vSync, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }

}
