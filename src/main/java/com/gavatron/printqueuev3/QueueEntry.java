package com.gavatron.printqueuev3;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

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
}
