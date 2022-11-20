package com.gavatron.webhook;

import com.google.gson.JsonObject;
import java.net.URL;

public class Author {
    JsonObject author = new JsonObject();

    public Author setName(String n) {
        this.author.addProperty("name", n);
        return this;
    }

    public Author setLink(String l) {
        try {
            this.author.addProperty("url", new URL(l).toString());
        } catch (Exception exception) {
            System.err.println("Your author link is invalid for some reason.");
        }

        return this;
    }

    public Author setIcon(String i) {
        try {
            this.author.addProperty("icon_url", new URL(i).toString());
        } catch (Exception exception) {
            System.err.println("Your author icon link is invalid for some reason.");
        }

        return this;
    }

    public String getData() {
        return this.author.toString();
    }
}
