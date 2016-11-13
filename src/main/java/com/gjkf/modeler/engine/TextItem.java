/*
 * Created by Davide Cossu (gjkf), 11/13/2016
 */

package com.gjkf.modeler.engine;

import com.gjkf.modeler.engine.render.Material;
import com.gjkf.modeler.engine.render.Mesh;
import com.gjkf.modeler.engine.render.Texture;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Extends {@link Item}. Represents a piece of text.
 */

public class TextItem extends Item {

    /**
     * The Z position in the buffer.
     */
    private static final float ZPOS = 0.0f;
    /**
     * The number of vertices per quad.
     */
    private static final int VERTICES_PER_QUAD = 4;
    /**
     * The text.
     */
    private String text;
    /**
     * The number of columns in the image.
     */
    private final int numCols;
    /**
     * The number of rows in the image.
     */
    private final int numRows;

    /**
     * Constructs a new object.
     *
     * @param text The text.
     * @param fontFileName The path to the font image.
     * @param numCols The number of columns.
     * @param numRows The number of rows.
     *
     * @throws Exception If anything went wrong.
     */

    public TextItem(String text, String fontFileName, int numCols, int numRows) throws Exception {
        super();
        this.text = text;
        this.numCols = numCols;
        this.numRows = numRows;
        Texture texture = new Texture(fontFileName);
        this.setMesh(buildMesh(texture, numCols, numRows));
    }

    /**
     * Builds a mesh from the texture.
     *
     * @param texture The texture.
     * @param numCols The number of columns.
     * @param numRows The number of rows.
     *
     * @return The newly created mesh.
     */

    private Mesh buildMesh(Texture texture, int numCols, int numRows) {
        byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
        int numChars = chars.length;

        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        float[] normals   = new float[0];
        List<Integer> indices   = new ArrayList<>();

        float tileWidth = (float)texture.getWidth() / (float)numCols;
        float tileHeight = (float)texture.getHeight() / (float)numRows;

        for(int i = 0; i < numChars; i++) {
            byte currChar = chars[i];
            int col = currChar % numCols;
            int row = currChar / numCols;

            // Build a character tile composed by two triangles

            // Left Top vertex
            positions.add((float)i*tileWidth); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)col / (float)numCols );
            textCoords.add((float)row / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD);

            // Left Bottom vertex
            positions.add((float)i*tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(ZPOS); //z
            textCoords.add((float)col / (float)numCols );
            textCoords.add((float)(row + 1) / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(col + 1)/ (float)numCols );
            textCoords.add((float)(row + 1) / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(col + 1)/ (float)numCols );
            textCoords.add((float)row / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 3);

            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);
        }

        float[] posArr = Utils.listToArray(positions);
        float[] textCoordsArr = Utils.listToArray(textCoords);
        int[] indicesArr = indices.stream().mapToInt(i->i).toArray();
        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
        mesh.setMaterial(new Material(texture));
        return mesh;
    }

    /**
     * Getter for property 'text'.
     *
     * @return Value for property 'text'.
     */
    public String getText() {
        return text;
    }

    /**
     * Setter for property 'text'.
     *
     * @param text Value to set for property 'text'.
     */
    public void setText(String text) {
        this.text = text;
        Texture texture = this.getMesh().getMaterial().getTexture();
        this.getMesh().deleteBuffers();
        this.setMesh(buildMesh(texture, numCols, numRows));
    }
}