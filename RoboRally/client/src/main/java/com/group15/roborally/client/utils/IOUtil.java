/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.group15.roborally.client.utils;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.group15.roborally.client.RoboRally;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class reading strings from resources and arbitrary input streams.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class IOUtil {

    /**
     * Reads a string from some InputStream. The solution is based
     * on google's Guava and a solution from Baeldung:
     * https://www.baeldung.com/convert-input-stream-to-string#guava
     *
     * @param inputStream the input stream
     * @return the string of the input stream
     */
    public static String readString(InputStream inputStream) {

        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return inputStream;
            }
        };

        try {
            return byteSource.asCharSource(Charsets.UTF_8).read();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Returns a string from a resource of the project. This method is implemented
     * in such a way that resource can be read when the project is deployed in
     * a jar file.
     *
     * @param relativeResourcePath the relative path to the resource
     * @return the string contents of the resource
     */
    public static String readResource(String relativeResourcePath) {
        ClassLoader classLoader = IOUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(relativeResourcePath);
        return IOUtil.readString(inputStream);
    }

    public static List<InputStream> loadJsonFilesFromResources(String folderName) throws IOException, URISyntaxException {
        List<InputStream> jsonFiles = new ArrayList<>();

        // Get the URL of the directory
        URL dirURL = RoboRally.class.getClassLoader().getResource(folderName);
        if (dirURL != null) {
            if (dirURL.getProtocol().equals("file")) {
                // Load resources from the file system
                Files.walk(Paths.get(dirURL.toURI()))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            try {
                                jsonFiles.add(Files.newInputStream(path));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } else if (dirURL.getProtocol().equals("jar")) {
                // Load resources from the JAR file
                String[] parts = dirURL.toString().split("!");
                URI jarUri = URI.create(parts[0]);

                Map<String, String> env = new HashMap<>();
                env.put("create", "true");

                try (FileSystem fs = FileSystems.newFileSystem(jarUri, env)) {
                    Path pathInJar = fs.getPath(parts[1]);

                    Files.walk(pathInJar)
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".json"))
                            .forEach(path -> {
                                try {
                                    // Read the file content into a byte array
                                    byte[] fileContent = Files.readAllBytes(path);
                                    // Create an InputStream from the byte array
                                    InputStream inputStream = new ByteArrayInputStream(fileContent);
                                    jsonFiles.add(inputStream);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }
        } else {
            System.err.println("Directory not found: " + folderName);
        }
        return jsonFiles;
    }
}
