package com.gruppe15.coursecreator;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import javafx.geometry.Point2D;

public class Point2DAdapter extends TypeAdapter<Point2D> {

    @Override
    public void write(JsonWriter out, Point2D value) throws IOException {
        out.beginObject();
        out.name("x").value(value.getX());
        out.name("y").value(value.getY());
        out.endObject();
    }

    @Override
    public Point2D read(JsonReader in) throws IOException {
        in.beginObject();
        double x = 0;
        double y = 0;
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "x": x = in.nextDouble(); break;
                case "y": y = in.nextDouble(); break;
            }
        }
        in.endObject();
        return new Point2D(x, y);
    }
}
