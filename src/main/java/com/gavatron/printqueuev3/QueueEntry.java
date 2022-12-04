package com.gavatron.printqueuev3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.StreamSupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public record QueueEntry(String catcard, String firstName, String lastName, Path path, String time, Date date) {
    public QueueEntry(String catcard, UserDatabase userList, Path path, String time, Date date) {
        this(catcard, userList.getUser(catcard).firstName(), userList.getUser(catcard).lastName(), path, time, date);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("catcard", this.catcard);
        object.addProperty("firstname", this.firstName);
        object.addProperty("lastname", this.lastName);
        object.addProperty("path", this.path.toString());
        object.addProperty("time", this.time);
        object.addProperty("date", DATE_FORMAT.format(this.date));

        return object;
    }

    public static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static QueueEntry parseJson(JsonObject object) {
        Date date = null;
        try {
            date = DATE_FORMAT.parse(object.get("date").getAsString());
        } catch (ParseException e) {
            e.printStackTrace(); // TODO: make this send an error?, but this should really not be possible
        }

        return new QueueEntry(
                object.get("catcard").getAsString(),
                object.get("firstname").getAsString(),
                object.get("lastname").getAsString(),
                Path.of(object.get("path").getAsString()),
                object.get("time").getAsString(),
                date);
    }

    public static List<QueueEntry> readFromQueueFile(Path path) {
        try {
            return StreamSupport.stream(JsonParser.parseReader(Files.newBufferedReader(path)).getAsJsonArray().spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .map(QueueEntry::parseJson)
                    .toList();
        } catch (Exception e) {
            try {
                Files.createFile(path);
                return new ArrayList<>();
            } catch (Exception ee) {
                QueueV3.sendError(e, "Failed to create queue.json");
            }
        }
        // Not reachable?
        return null;
    }
}
