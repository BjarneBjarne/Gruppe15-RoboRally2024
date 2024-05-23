package gruppe15.roborally.coursecreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
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

    public static void saveCourseDataToFile(CC_CourseData courseData, File file) {
        try (FileWriter writer = new FileWriter(file, false)) { // Ensure the second parameter is false to overwrite
            gson.toJson(courseData, writer);
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
}
