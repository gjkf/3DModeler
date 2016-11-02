/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.engine;

/**
 * Interface representing the game logic.
 * <p>Implement this to create your own game.</p>
 */

public interface IGameLogic {

    /**
     * Initializes the game logic
     *
     * @param window The window
     *
     * @throws Exception If anything went wrong.
     */

    void init(Window window) throws Exception;

    /**
     * Retrieves the inputs from the window.
     *
     * @param window The current window.
     */

    void input(Window window);

    /**
     * Updates the game logic. Used for calculations.
     *
     * @param interval The frames passed.
     */

    void update(float interval);

    /**
     * Renders the game logic.
     *
     * @param window The window to update.
     */

    void render(Window window);

    /**
     * Cleans up the resources.
     */

    void cleanup();
}