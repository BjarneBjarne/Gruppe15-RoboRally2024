package com.gruppe15.coursecreator;

import com.google.gson.*;
import com.gruppe15.model.Heading;
import javafx.scene.image.Image;

import java.lang.reflect.Type;

class CC_SpaceViewSerializer implements JsonSerializer<CC_SpaceView> {
    @Override
    public JsonElement serialize(CC_SpaceView src, Type typeOfSrc, JsonSerializationContext context) {
        // If placedBoardElement is -1 and all placedWalls are null, don't add the space view to the json.
        if (src.getPlacedBoardElement() == -1 && src.getCheckpoint() == -1 && areAllWallsNull(src.getPlacedWalls())) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("boardX", src.getBoardX());
        jsonObject.addProperty("boardY", src.getBoardY());
        jsonObject.addProperty("placedBoardElement", src.getPlacedBoardElement());
        jsonObject.addProperty("checkpoint", src.getCheckpoint());
        jsonObject.add("direction", context.serialize(src.getDirection()));
        jsonObject.add("placedWalls", context.serialize(src.getPlacedWalls()));
        return jsonObject;
    }

    private boolean areAllWallsNull(Heading[] walls) {
        for (Heading wall : walls) {
            if (wall != null) {
                return false;
            }
        }
        return true;
    }
}

class CC_SpaceViewDeserializer implements JsonDeserializer<CC_SpaceView> {
    @Override
    public CC_SpaceView deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();

        CC_SpaceView spaceView;
        try {
            int boardX = jsonObject.get("boardX").getAsInt();
            int boardY = jsonObject.get("boardY").getAsInt();
            spaceView = new CC_SpaceView(boardX, boardY);
        } catch (NullPointerException e) {
            spaceView = new CC_SpaceView();
        }

        int checkpoint = -1;

        int placedBoardElement = jsonObject.get("placedBoardElement").getAsInt();
        try {
            checkpoint = jsonObject.get("checkpoint").getAsInt();
        } catch (NullPointerException ignored) {
        }
        Heading direction = context.deserialize(jsonObject.get("direction"), Heading.class);
        Heading[] walls = context.deserialize(jsonObject.get("placedWalls"), Heading[].class);

        Image boardElementImage = null;
        if (placedBoardElement != -1) {
            boardElementImage = CC_Items.values()[placedBoardElement].image;
        }

        Image checkpointImage = null;
        if (checkpoint != -1) {
            checkpointImage = CC_Items.values()[checkpoint].image;
        }

        spaceView.CC_setBoardElement(boardElementImage, direction, placedBoardElement);
        spaceView.CC_setCheckpoint(checkpointImage, checkpoint);

        Image wallImage = CC_Items.Wall.image;
        for (Heading wallDirection : walls) {
            if (wallDirection != null) {
                spaceView.CC_setWall(wallImage, wallDirection);
            }
        }

        return spaceView;
    }
}
