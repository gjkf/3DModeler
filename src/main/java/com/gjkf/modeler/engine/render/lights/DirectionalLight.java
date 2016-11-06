/*
 * Created by Davide Cossu (gjkf), 11/6/2016
 */
package com.gjkf.modeler.engine.render.lights;

import org.joml.Vector3f;

/**
 * Defines a directional light, like the sun.
 */

public class DirectionalLight {

    /**
     * The color.
     */
    private Vector3f color;
    /**
     * The direction.
     */
    private Vector3f direction;
    /**
     * The intensity.
     */
    private float intensity;

    /**
     * Creates a new directional light.
     *
     * @param color The color.
     * @param direction The direction.
     * @param intensity The intensity.
     */

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    /**
     * Copies a directional light.
     *
     * @param light The light.
     */

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
    }

    /**
     * Getter for property 'color'.
     *
     * @return Value for property 'color'.
     */
    public Vector3f getColor() {
        return color;
    }

    /**
     * Setter for property 'color'.
     *
     * @param color Value to set for property 'color'.
     */
    public void setColor(Vector3f color) {
        this.color = color;
    }

    /**
     * Getter for property 'direction'.
     *
     * @return Value for property 'direction'.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * Setter for property 'direction'.
     *
     * @param direction Value to set for property 'direction'.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /**
     * Getter for property 'intensity'.
     *
     * @return Value for property 'intensity'.
     */
    public float getIntensity() {
        return intensity;
    }

    /**
     * Setter for property 'intensity'.
     *
     * @param intensity Value to set for property 'intensity'.
     */
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}