package com.group15.roborally.client.coursecreator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.geometry.Point2D;
import java.io.IOException;
import java.util.List;

public class CC_CourseDataAdapter extends TypeAdapter<CC_CourseData> {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Point2D.class, new Point2DAdapter())
            .registerTypeAdapter(CC_SpaceView.class, new CC_SpaceViewSerializer())
            .registerTypeAdapter(CC_SpaceView.class, new CC_SpaceViewDeserializer())
            .registerTypeAdapter(CC_SubBoard.class, new CC_SubBoardAdapter())
            .create();

    @Override
    public void write(JsonWriter out, CC_CourseData courseData) throws IOException {
        out.beginObject();
        out.name("courseName").value(courseData.getCourseName());
        out.name("subBoards").jsonValue(gson.toJson(courseData.getSubBoards()));
        out.name("snapshotAsBase64").value(courseData.getSnapshotAsBase64());
        out.endObject();
    }

    @Override
    public CC_CourseData read(JsonReader in) {
        JsonObject jsonObject = gson.fromJson(in, JsonObject.class);
        String courseName = jsonObject.get("courseName").getAsString();
        List<CC_SubBoard> subBoards = gson.fromJson(jsonObject.get("subBoards"), new TypeToken<List<CC_SubBoard>>(){}.getType());
        String snapshotAsBase64 = jsonObject.get("snapshotAsBase64").getAsString();

        return new CC_CourseData(courseName, subBoards, snapshotAsBase64);
    }
}
