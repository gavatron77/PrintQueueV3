package com.gavatron.webhook;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;

public class Embed {
    private JsonObject embed = new JsonObject();

    public Embed setTitle(String t) {
        this.embed.addProperty("title", t);
        return this;
    }

    public Embed setDesc(String d) {
        this.embed.addProperty("description", d);
        return this;
    }

    public Embed setColor(String c) {
        this.embed.addProperty("color", Integer.parseInt(c));
        return this;
    }

    public Embed setColor(int r, int g, int b) {
        int c = (r << 8) + g;
        c = (c << 8) + b;
        this.embed.addProperty("color", c);
        return this;
    }

    public Embed setColor(Color c) {
        this.setColor(c.getRed(), c.getGreen(), c.getBlue());
        return this;
    }

    public Embed setAuthor(Author a) {
        this.embed.add("author", JsonParser.parseString(a.getData()).getAsJsonObject());
        return this;
    }

    public Embed addImage(String i) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("url", i);
        this.embed.add("image", jsonobject);
        return this;
    }

    public Embed addThumbnail(String i) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("url", i);
        this.embed.add("thumbnail", jsonobject);
        return this;
    }

    public Embed addTimestamp(String t) {
        this.embed.addProperty("timestamp", t);
        return this;
    }

    public String getData() {
        return this.embed.toString();
    }

    public Embed json(JsonObject d) {
        this.embed = d;
        return this;
    }
}