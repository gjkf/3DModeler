/*
 * Created by Davide Cossu (gjkf), 11/2/2016
 */
package com.gjkf.modeler.engine.render;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Object representing a texture.
 */

public class Texture{

    /**
     * The ID of the texture.
     */
    private final int id;

    /**
     * Creates a new texture calling {@link #loadTexture(String)}.
     *
     * @param fileName The path of the file.
     *
     * @throws Exception If the texture could not be read.
     */

    public Texture(String fileName) throws Exception {
        this(loadTexture(fileName));
    }

    /**
     * Creates a texture given the OpenGL ID.
     *
     * @param id The ID.
     */

    public Texture(int id) {
        this.id = id;
    }

    /**
     * Binds this texture.
     */

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Gets the current ID.
     *
     * @return The ID.
     */

    public int getId() {
        return id;
    }

    /**
     * Loads a new texture and returns its ID.
     *
     * @param fileName The path of the file.
     *
     * @return The ID of the new texture.
     *
     * @throws Exception If the texture could not be decoded.
     */

    private static int loadTexture(String fileName) throws Exception {
        // Load Texture file
        PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(fileName));

        // Load texture contents into a byte buffer
        ByteBuffer buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
        buf.flip();

        // Create a new OpenGL texture
        int textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);
        return textureId;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }


}
