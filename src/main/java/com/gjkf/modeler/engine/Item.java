/*
 * Created by Davide Cossu (gjkf), 11/2/2016
 */
package com.gjkf.modeler.engine;

import com.gjkf.modeler.engine.render.Mesh;
import org.joml.Vector3f;

/**
 * Object representing an item in the world.
 */

public class Item {

    /**
     * The mesh of this item.
     */
    private Mesh mesh;
    /**
     * The position in the world.
     */
    private final Vector3f position;
    /**
     * The scale.
     */
    private float scale;
    /**
     * The rotation.
     */
    private final Vector3f rotation;

    /** Constructs a new Item. */

    public Item(){
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    /**
     * Creates a new item with position and rotation like <tt>new Vector3f(0,0,0)</tt>
     *
     * @param mesh The mesh of the item.
     */

    public Item(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    /**
     * Returns the position.
     *
     * @return The position vector.
     */

    public Vector3f getPosition() {
        return position;
    }

    /**
     * Sets the position of this item.
     *
     * @param x The position in the X axis.
     * @param y The position in the Y axis.
     * @param z The position in the Z axis.
     */

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    /**
     * Gets the scale of this item.
     *
     * @return The scale.
     */

    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale of the item.
     *
     * @param scale The new scale.
     */

    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Gets the rotation vector.
     *
     * @return The rotation.
     */

    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation.
     *
     * @param x The rotation on the X axis.
     * @param y The rotation on the Y axis.
     * @param z The rotation on the Z axis.
     */

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    /**
     * Gets the mesh.
     *
     * @return The mesh of this item.
     */

    public Mesh getMesh() {
        return mesh;
    }

    /**
     * Setter for property 'mesh'.
     *
     * @param newMesh Value to set for property 'mesh'.
     */

    public void setMesh(Mesh newMesh){
        mesh = newMesh;
    }
}
