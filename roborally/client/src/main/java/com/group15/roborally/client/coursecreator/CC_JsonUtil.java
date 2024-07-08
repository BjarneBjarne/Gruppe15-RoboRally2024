package com.group15.roborally.client.coursecreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group15.roborally.client.utils.IOUtil;
import javafx.geometry.Point2D;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CC_JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Point2D.class, new Point2DAdapter())
            .registerTypeAdapter(CC_SpaceView.class, new CC_SpaceViewSerializer())
            .registerTypeAdapter(CC_SpaceView.class, new CC_SpaceViewDeserializer())
            .registerTypeAdapter(CC_SubBoard.class, new CC_SubBoardAdapter())
            .registerTypeAdapter(CC_CourseData.class, new CC_CourseDataAdapter())
            .create();

    public static void saveCourseDataToFile(CC_CourseData courseData, File file, boolean saveImageAsPNG) {
        try (FileWriter writer = new FileWriter(file, false)) {
            gson.toJson(courseData, writer);

            if (saveImageAsPNG) courseData.saveImageToFile(file.getParentFile().getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CC_CourseData loadCourseDataFromFile(File file) {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, CC_CourseData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CC_CourseData loadCourseDataFromInputStream(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return gson.fromJson(reader, CC_CourseData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CC_CourseData> getCoursesInFolder(String folderName) {
        List<InputStream> courseFiles;
        try {
            courseFiles = IOUtil.loadJsonFilesFromResources(folderName);
        } catch (IOException | URISyntaxException e) {
            System.out.println("Error loading JSON files from resources. " + e);
            throw new RuntimeException(e);
        }
        List<CC_CourseData> courses = new ArrayList<>();
        for (InputStream courseFile : courseFiles) {
            CC_CourseData courseData = CC_JsonUtil.loadCourseDataFromInputStream(courseFile);
            if (courseData != null) {
                String playableMessage = courseData.getIsPlayable();
                if (playableMessage.equals("playable")) {
                    courses.add(courseData);
                } else {
                    System.out.println("Can't load course: \"" + courseData.getCourseName() + "\". The following conditions need to be met:\n" + playableMessage);
                }
            } else {
                System.out.println("Failed to load course data from input stream");
            }
        }
        return courses;
    }
}
