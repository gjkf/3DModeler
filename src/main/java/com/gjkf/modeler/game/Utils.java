/*
 * Created by Davide Cossu (gjkf), 11/1/2016
 */
package com.gjkf.modeler.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Misc util class
 */

public class Utils {

    /**
     * Loads and returns a resource at the given path.
     *
     * @param fileName The path of the resource.
     *
     * @return The read resource.
     */

    public static String loadResource(String fileName){
        StringBuilder builder = new StringBuilder();
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load a file" + System.lineSeparator() + ex.getMessage());
        }

        return builder.toString();
    }

}