package gruppe15.roborally.coursecreator;

import com.google.gson.*;
import gruppe15.roborally.model.Heading;
import javafx.scene.image.Image;

import java.lang.reflect.Type;

class CC_SpaceViewSerializer implements JsonSerializer<CC_SpaceView> {
    @Override
    public JsonElement serialize(CC_SpaceView src, Type typeOfSrc, JsonSerializationContext context) {
        // If placedBoardElement is -1 and all placedWalls are null, don't add the space view to the json.
        if (src.getPlacedBoardElement() == -1 && areAllWallsNull(src.getPlacedWalls())) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("placedBoardElement", src.getPlacedBoardElement());
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
        CC_SpaceView spaceView = new CC_SpaceView();

        int placedBoardElement = jsonObject.get("placedBoardElement").getAsInt();
        Heading direction = context.deserialize(jsonObject.get("direction"), Heading.class);
        Image boardElementImage = null;
        if (placedBoardElement != -1) {
            boardElementImage = CC_Items.values()[placedBoardElement].image;
        }
        spaceView.CC_setBoardElement(boardElementImage, direction, placedBoardElement);

        Heading[] walls = context.deserialize(jsonObject.get("placedWalls"), Heading[].class);
        Image wallImage = CC_Items.Wall.image;
        for (Heading wallDirection : walls) {
            if (wallDirection != null) {
                spaceView.CC_setWall(wallImage, wallDirection);
            }
        }

        return spaceView;
    }
}
