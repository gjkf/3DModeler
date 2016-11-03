/*
 * Created by Davide Cossu (gjkf), 11/2/2016
 */
package com.gjkf.modeler.engine.render;

import com.gjkf.modeler.engine.Item;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Utility class that provides methods to create <tt>projection matrices</tt> and <tt>view matrices</tt>.
 */

public class Transformation {

    /**
     * The projection matrix.
     */
    private final Matrix4f projectionMatrix;
    /**
     * The model view matrix.
     */
    private final Matrix4f modelViewMatrix;
    /**
     * The view matrix.
     */
    private final Matrix4f viewMatrix;

    /**
     * Creates the new empty matrices.
     */

    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    /**
     * Creates a new projection matrix.
     *
     * @param fov The Field Of View.
     * @param width The width of the frustum.
     * @param height The height of the frustum.
     * @param zNear The near coordinate of the frustum.
     * @param zFar The far coordinate of the frustum.
     *
     * @return The new matrix.
     */

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    /**
     * Creates a new view matrix.
     *
     * @param camera The camera object.
     *
     * @return The new matrix.
     */

    public Matrix4f getViewMatrix(Camera camera) {
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
     * Creates a new matrix for the model.
     *
     * @param item The item to be drawn.
     * @param viewMatrix The view matrix.
     *
     * @return The new matrix.
     */

    public Matrix4f getModelViewMatrix(Item item, Matrix4f viewMatrix) {
        Vector3f rotation = item.getRotation();
        modelViewMatrix.identity().translate(item.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(item.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

}