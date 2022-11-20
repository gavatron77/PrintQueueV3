package com.gavatron.printqueuev3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserDatabase {
    private final Map<String, User> users;

    public UserDatabase() {
        this.users = new HashMap<>();
    }

    public void readFromFile(Path file) throws IOException {
        JsonObject users = JsonParser.parseReader(Files.newBufferedReader(file)).getAsJsonObject();
        for (Map.Entry<String, JsonElement> userEntry : users.entrySet()) {
            User user = User.parseJson(userEntry.getValue().getAsJsonObject());
            this.users.put(userEntry.getKey(), user);
        }
    }

    public boolean hasUser(String catcard) {
        return this.users.containsKey(catcard);
    }

    public User getUser(String catcard) {
        return this.users.get(catcard);
    }

    public void addUser(String catcard, User user) {
        this.users.put(catcard, user);
    }

    public void write(Path path) throws IOException {
        JsonObject users = new JsonObject();
        for (Map.Entry<String, User> userEntry : this.users.entrySet()) {
            JsonObject user = userEntry.getValue().toJson();
            users.add(userEntry.getKey(), user);
        }
        Files.writeString(path, users.toString());
    }
}
