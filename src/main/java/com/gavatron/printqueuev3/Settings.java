package com.gavatron.printqueuev3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public record Settings(String webhook, double kgPrice, double hr12Price, double hr24Price, double hr48Price, double moreHrs, String[] sdCards) {
    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("webhook", this.webhook);
        o.addProperty("kgprice", this.kgPrice);
        o.addProperty("12hrs", this.hr12Price);
        o.addProperty("24hrs", this.hr24Price);
        o.addProperty("48hrs", this.hr48Price);
        o.addProperty("morehrs", this.moreHrs);

        JsonArray sdCards = new JsonArray();
        for (String sdCard : this.sdCards) {
            sdCards.add(sdCard);
        }
        o.add("sdcards", sdCards);

        return o;
    }

    public static Settings fromFile(Path file) {
        try {
            JsonObject o = JsonParser.parseReader(Files.newBufferedReader(file)).getAsJsonObject();

            String webhook = o.get("webhook").getAsString();
            double kgPrice = o.get("kgprice").getAsDouble();
            double hr12Price = o.get("12hrs").getAsDouble();
            double hr24Price = o.get("24hrs").getAsDouble();
            double hr48Price = o.get("48hrs").getAsDouble();
            double moreHrs = o.get("morehrs").getAsDouble();
            String[] sdCards = StreamSupport.stream(o.get("sdcards").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).toArray(String[]::new);

            return new Settings(webhook, kgPrice, hr12Price, hr24Price, hr48Price, moreHrs, sdCards);
        } catch (FileNotFoundException e) {
            Settings defaultSettings = new Settings("Discord Webhook URL here", 60.0, 0.5, 0.6, 0.7, 0.8, new String[]{"A:", "B:"});
            try {
                Files.writeString(file, defaultSettings.toJson().toString());
            } catch (Exception e2) {
                throw new RuntimeException("Failed to write default settings to " + file, e2);
            }

            throw new RuntimeException("No settings file found at: " + file + ". Default file created.");
        } catch (IOException e) {
            throw new RuntimeException("Unknown error: ", e);
        }
    }
}
