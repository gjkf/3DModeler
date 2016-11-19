/*
 * Created by Davide Cossu (gjkf), 11/19/2016
 */

package com.gjkf.modeler.engine.items;

import com.gjkf.modeler.engine.render.HeightMapMesh;

/**
 * Object that constructs items for each <i>block</i> in the terrain.
 */

public class Terrain{

    /**
     * The items.
     */
    private final Item[] items;

    /**
     * Constructs the items starting from the image given.
     *
     * @param blocksPerRow How many blocks are in a row. The size of {@link #items} is determined by <tt>blocksPerRow ^ 2</tt>.
     * @param scale The texture scale.
     * @param minY The minimum value the Y coordinate of the terrain can get.
     * @param maxY The maximum value the Y coordinate of the terrain can get.
     * @param heightMap The file from which read the height map.
     * @param textureFile The file from which read the texture.
     * @param textInc The amount of texture between vertices.
     *
     * @throws Exception If anything went wrong.
     */

    public Terrain(int blocksPerRow, float scale, float minY, float maxY, String heightMap, String textureFile, int textInc) throws Exception {
        items = new Item[blocksPerRow * blocksPerRow];
        HeightMapMesh heightMapMesh = new HeightMapMesh(minY, maxY, heightMap, textureFile, textInc);
        for (int row = 0; row < blocksPerRow; row++) {
            for (int col = 0; col < blocksPerRow; col++) {
                float xDisplacement = (col - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                Item terrainBlock = new Item(heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                items[row * blocksPerRow + col] = terrainBlock;
            }
        }
    }

    /**
     * Getter for property 'items'.
     *
     * @return Value for property 'items'.
     */

    public Item[] getItems() {
        return items;
    }


}