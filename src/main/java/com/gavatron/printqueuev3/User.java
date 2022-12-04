package com.gavatron.printqueuev3;

import java.util.List;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public record User(String firstName, String lastName, String email, List<Print> prints) {
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("firstname", this.firstName);
        object.addProperty("lastname", this.lastName);
        object.addProperty("email", this.email);
        object.add("prints", this.prints.stream()
                .map(Print::toJson)
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
        return object;
    }

    public static User parseJson(JsonObject object) {
        return new User(
                object.get("firstname").getAsString(),
                object.get("lastname").getAsString(),
                object.get("email").getAsString(),
                StreamSupport.stream(object.get("prints").getAsJsonArray().spliterator(), false)
                        .map(Print::parseJson)
                        .toList());
    }
}
