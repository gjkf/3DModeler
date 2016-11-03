/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.engine.render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Object representing a <tt>Shader program</tt>.
 * <p>Provides useful methods to create, set and load shaders.</p>
 */

public class ShaderProgram {

    /**
     * The program ID
     */
    private final int programId;
    /**
     * The ID of the vertex shader.
     */
    private int vertexShaderId;
    /**
     * The ID of the fragment shader.
     */
    private int fragmentShaderId;
    /**
     * A map containing all the uniforms that will be passed to the shaders.
     */
    private final Map<String, Integer> uniforms;

    /**
     * Creates an new program.
     *
     * @throws Exception If the {@link #programId} is 0.
     */
    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
    }

    /**
     * Adds the <tt>uniformName</tt> to {@link #uniforms}.
     *
     * @param uniformName The name to add.
     *
     * @throws Exception If the uniform could not be found in the shader.
     */

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception ("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * Sets the valur of the given uniform.
     *
     * @param uniformName The uniform to reference.
     * @param value The value.
     */

    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        value.get(fb);
        glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
    }

    /**
     * Sets the valur of the given uniform.
     *
     * @param uniformName The uniform to reference.
     * @param value The value.
     */

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    /**
     * Creates a vertex shader from a string of text.
     *
     * @param shaderCode The shader's code.
     *
     * @throws Exception If the {@link #vertexShaderId} is 0.
     */

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    /**
     * Creates a fragment shader from a string of text.
     *
     * @param shaderCode The shader's code.
     *
     * @throws Exception If the {@link #fragmentShaderId} is 0.
     */

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    /**
     * Creates a shader of the given type with the given code.
     *
     * @param shaderCode The code of the shader.
     * @param shaderType The type of the shader.
     *
     * @return The ID of the new shader.
     *
     * @throws Exception If the ID is 0 or there has been a problem.
     */

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Code: " + shaderId);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    /**
     * Links the shaders to the program.
     *
     * @throws Exception If there has been any problems.
     */

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    /**
     * Binds the current program.
     */

    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Unbinds the current program.
     */

    public void unbind() {
        glUseProgram(0);
    }

    /**
     * Cleans up the resources used.
     */

    public void cleanup() {
        unbind();
        if (programId != 0) {
            if (vertexShaderId != 0) {
                glDetachShader(programId, vertexShaderId);
            }
            if (fragmentShaderId != 0) {
                glDetachShader(programId, fragmentShaderId);
            }
            glDeleteProgram(programId);
        }
    }

}