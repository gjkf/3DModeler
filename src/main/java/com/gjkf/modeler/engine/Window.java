/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.engine;

import com.gjkf.modeler.engine.render.Color4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Object representing the current window.
 */

public class Window {

    /**
     * The title of the window.
     */
    private final String title;
    /**
     * The width of the window.
     */
    private int width;
    /**
     * The height of the window.
     */
    private int height;
    /**
     * The long ID of the current window.
     */
    private long windowHandle;
    /**
     * The error callback.
     */
    private GLFWErrorCallback errorCallback;
    /**
     * The key callback.
     */
    private GLFWKeyCallback keyCallback;
    /**
     * The window resize callback.
     */
    private GLFWWindowSizeCallback windowSizeCallback;
    /**
     * Whether or not the window has been resized.
     */
    private boolean resized;
    /**
     * Whether or not this window should use vSync.
     */
    private boolean vSync;

    /**
     * The window constructor.
     *
     * @param title The title of the window.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param vSync Whether or not should use vSync.
     */

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resized = false;
    }

    /**
     * Initializes the GL context, sets up the callbacks, defines the hints and displays the window.
     */

    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetWindowSizeCallback(windowHandle, windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.width = width;
                Window.this.height = height;
                Window.this.setResized(true);
            }
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL11.GL_TRUE);
                }
            }
        });

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(windowHandle, (GLFWvidmode.width(vidmode) - width) / 2, (GLFWvidmode.height(vidmode) - height) / 2);

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        GLContext.createFromCurrent();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);
    }

    /**
     * Sets the background color.
     *
     * @param color The color.
     */

    public void setClearColor(Color4f color) {
        glClearColor(color.r, color.g, color.b, color.a);
    }

    /**
     * Returns TRUE if the given key is pressed.
     *
     * @param keyCode The key code.
     *
     * @return The key status.
     */

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    /**
     * Whether or not the window should close.
     *
     * @return Whether or not the window should close.
     */

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle) == GL11.GL_TRUE;
    }

    /**
     * Returns the title of this window.
     *
     * @return The title.
     */

    public String getTitle() {
        return title;
    }

    /**
     * Returns the width of this window.
     *
     * @return The width.
     */

    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of this window.
     *
     * @return The height.
     */

    public int getHeight() {
        return height;
    }

    /**
     * Returns whether or not the window has been resized.
     *
     * @return TRUE if resized.
     */

    public boolean isResized() {
        return resized;
    }

    /**
     * Sets the resized variable.
     *
     * @param resized The new status.
     */

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    /**
     * Whether or not the window has vSync enabled.
     *
     * @return {@link #vSync}
     */

    public boolean isvSync() {
        return vSync;
    }

    /**
     * Sets vSync
     *
     * @param vSync The new status.
     */

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    /**
     * Updates the window swapping the buffers.
     */

    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }
}