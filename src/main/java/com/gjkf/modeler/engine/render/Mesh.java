/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.engine.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Class representing a mesh.
 * <p>A mesh is an object in the space represented by the vertex positions and a texture</p>
 */

public class Mesh {

    /**
     * The ID of the VAO used.
     */
    private final int vaoId;
    /**
     * The list of IDs used to render the object.
     */
    private final List<Integer> vboIdList;
    /**
     * The number of vertices to draw.
     */
    private final int vertexCount;
    /**
     * The material.
     */
    private Material material;

    /**
     * The mesh constructor.
     *
     * @param positions The array containing the positions of the vertices on the 3 axis.
     * @param textCoords The array containing the texture coordinates for each vertex.
     * @param normals The array containing the normals.
     * @param indices The array containing the order of the vertices for the triangles used in VBOs.
     */

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        vertexCount = indices.length;
        vboIdList = new ArrayList<>();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Position VBO
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer posBuffer = BufferUtils.createFloatBuffer(positions.length);
        posBuffer.put(positions).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // Texture coordinates VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
        textCoordsBuffer.put(textCoords).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        // Vertex normals VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer vecNormalsBuffer = BufferUtils.createFloatBuffer(normals.length);
        vecNormalsBuffer.put(normals).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        // Index VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /**
     * Getter for property 'vaoId'.
     *
     * @return Value for property 'vaoId'.
     */

    public int getVaoId() {
        return vaoId;
    }


    /**
     * Getter for property 'vertexCount'.
     *
     * @return Value for property 'vertexCount'.
     */

    public int getVertexCount() {
        return vertexCount;
    }


    /**
     * Getter for property 'material'.
     *
     * @return Value for property 'material'.
     */

    public Material getMaterial(){
        return material;
    }


    /**
     * Setter for property 'material'.
     *
     * @param material Value to set for property 'material'.
     */

    public void setMaterial(Material material){
        this.material = material;
    }

    /**
     * Renders the mesh.
     */

    public void render() {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Cleans up the resources used for the rendering.
     */

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vboIdList.forEach(GL15::glDeleteBuffers);

        // Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}