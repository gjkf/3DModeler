/*
 * Created by Davide Cossu (gjkf), 11/13/2016
 */

package com.gjkf.modeler.test.game;

import com.gjkf.modeler.engine.IHud;
import com.gjkf.modeler.engine.Window;
import com.gjkf.modeler.engine.items.Item;
import com.gjkf.modeler.engine.items.TextItem;
import com.gjkf.modeler.engine.render.*;

import java.awt.*;

public class Hud implements IHud{

    private static final Font FONT = new Font("Arial", Font.PLAIN, 40);

    private static final String CHARSET = "ISO-8859-1";

    private final Item[] items;

    private final TextItem statusTextItem;

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setColour(Colors.GREEN.toVector());

        // Create list that holds the items that compose the HUD
        items = new Item[]{statusTextItem};
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
    }

}