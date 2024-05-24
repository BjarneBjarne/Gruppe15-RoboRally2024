package gruppe15.roborally.coursecreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.fileaccess.IOUtil;
import javafx.geometry.Point2D;

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

    public static List<CC_CourseData> getCoursesInFolder(String folderName) {
        List<File> courseFiles;
        try {
            courseFiles = IOUtil.loadJsonFilesFromResources("courses");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        List<CC_CourseData> courses = new ArrayList<>();
        for (File courseFile : courseFiles) {
            CC_CourseData courseData = CC_JsonUtil.loadCourseDataFromFile(courseFile);
            courses.add(courseData);
        }
        return courses;
    }
}
