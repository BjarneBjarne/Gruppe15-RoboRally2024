package com.group15.coursecreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.group15.model.Heading;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import java.io.IOException;

public class CC_SubBoardAdapter extends TypeAdapter<CC_SubBoard> {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Point2D.class, new Point2DAdapter())
            .registerTypeAdapter(CC_SpaceView.class, new CC_SpaceViewSerializer())
            .registerTypeAdapter(CC_SpaceView.class, new CC_SpaceViewDeserializer())
            .create();

    @Override
    public void write(JsonWriter out, CC_SubBoard subBoard) throws IOException {
        out.beginObject();
        out.name("position").jsonValue(gson.toJson(subBoard.getPosition()));
        out.name("spaceViews").jsonValue(gson.toJson(subBoard.getSpaceViews()));
        out.name("isStartSubBoard").value(subBoard.isStartSubBoard());
        out.name("direction").value(subBoard.getDirection().name());
        out.endObject();
    }

    @Override
    public CC_SubBoard read(JsonReader in) {
        JsonObject jsonObject = gson.fromJson(in, JsonObject.class);
        Point2D position = gson.fromJson(jsonObject.get("position"), Point2D.class);
        CC_SpaceView[][] spaceViews = gson.fromJson(jsonObject.get("spaceViews"), CC_SpaceView[][].class);
        boolean isStartSubBoard = jsonObject.get("isStartSubBoard").getAsBoolean();
        Heading direction = Heading.valueOf(jsonObject.get("direction").getAsString());

        GridPane subBoardGridPane = CC_Controller.getNewGridPane(position, direction, spaceViews);
        return new CC_SubBoard(position, spaceViews, subBoardGridPane, isStartSubBoard, direction);
    }
}
