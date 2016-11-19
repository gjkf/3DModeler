/*
 * Created by Davide Cossu (gjkf), 11/2/2016
 */
package com.gjkf.modeler.engine.render;

import com.gjkf.modeler.engine.items.Item;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Utility class that provides methods to create matrices used in the rendering process.
 */

public class Transformation {

    /**
     * The projection matrix.
     */
    private final Matrix4f projectionMatrix;
    /**
     * The model matrix.
     */
    private final Matrix4f modelMatrix;
    /**
     * The model view matrix.
     */
    private final Matrix4f modelViewMatrix;
    /**
     * The view matrix.
     */
    private final Matrix4f viewMatrix;
    /**
     * The orthographic matrix.
     */
    private final Matrix4f orthoMatrix;
    /**
     * The orthomodel matrix.
     */
    private final Matrix4f orthoModelMatrix;


    /**
     * Creates the new empty matrices.
     */

    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
        orthoModelMatrix = new Matrix4f();
    }

    /**
     * Getter for property 'projectionMatrix'.
     *
     * @return Value for property 'projectionMatrix'.
     */

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Updates the {@link #projectionMatrix}.
     *
     * @param fov The Field Of View in radians.
     * @param width The width of the frustum.
     * @param height The height of the frustum.
     * @param zNear The near coordinate of the frustum.
     * @param zFar The far coordinate of the frustum.
     *
     * @return The updated matrix.
     */

    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    /**
     * Getter for property 'viewMatrix'.
     *
     * @return Value for property 'viewMatrix'.
     */

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    /**
     * Updates the {@link #viewMatrix}.
     *
     * @param camera The camera.
     *
     * @return The updated matrix.
     */

    public Matrix4f updateViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    /**
     * Returns a orthographic matrix.
     *
     * @param left The left coordinate.
     * @param right The right coordinate.
     * @param bottom The bottom coordinate.
     * @param top The top coordinate.
     *
     * @return The new matrix.
     */

    public final Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top) {
        orthoMatrix.identity();
        orthoMatrix.setOrtho2D(left, right, bottom, top);
        return orthoMatrix;
    }

    /**
     * Builds a new model matrix multiplied by a view matrix.
     *
     * @param item The item.
     * @param viewMatrix The view matrix.
     *
     * @return The new matrix.
     */

    public Matrix4f buildModelViewMatrix(Item item, Matrix4f viewMatrix) {
        Vector3f rotation = item.getRotation();
        modelMatrix.identity().translate(item.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(item.getScale());
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }

    /**
     * Builds a orthographic matrix multiplied by the model matrix.
     *
     * @param item The item.
     * @param orthoMatrix The orthographic matrix.
     *
     * @return The new matrix.
     */

    public Matrix4f buildOrtoProjModelMatrix(Item item, Matrix4f orthoMatrix) {
        Vector3f rotation = item.getRotation();
        modelMatrix.identity().translate(item.getPosition()).
                rotateX((float) Math.toRadians(-rotation.x)).
                rotateY((float) Math.toRadians(-rotation.y)).
                rotateZ((float) Math.toRadians(-rotation.z)).
                scale(item.getScale());
        orthoModelMatrix.set(orthoMatrix);
        orthoModelMatrix.mul(modelMatrix);
        return orthoModelMatrix;
    }

}