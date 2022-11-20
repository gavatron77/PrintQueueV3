package com.gavatron.webhook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Webhook {
    private URL url;
    private String content;
    private String name = "Gavatron77's Java Webhook Library";
    private URL icon;
    private ArrayList<String> embeds = new ArrayList<>();
    private boolean iconPresent = false;
    private boolean embedsPresent = false;

    public Webhook(String u) {
        try {
            this.url = new URL(u);
        } catch (Exception exception) {
            System.err.println("Your webhook URL is invalid for some reason.");
        }

    }

    public Webhook setURL(String u) {
        try {
            this.url = new URL(u);
        } catch (Exception exception) {
            System.err.println("Your webhook URL is invalid for some reason.");
        }

        return this;
    }

    public Webhook setName(String n) {
        this.name = n;
        return this;
    }

    public Webhook setContent(String c) {
        this.content = c;
        return this;
    }

    public Webhook setIcon(String a) {
        try {
            this.icon = new URL(a);
        } catch (Exception exception) {
            System.err.println("Your webhook icon URL is invalid for some reason.");
        }

        this.iconPresent = true;
        return this;
    }

    public Webhook addEmbed(Embed e) {
        this.embeds.add(e.getData());
        this.embedsPresent = true;
        return this;
    }

    public Webhook addJsonEmbed(String e) {
        this.embeds.add(e);
        this.embedsPresent = true;
        return this;
    }

    private JsonArray getEmbeds() {
        JsonArray jsonarray = new JsonArray();

        for(String s : this.embeds) {
            jsonarray.add(JsonParser.parseString(s));
        }

        return jsonarray;
    }

    private JsonObject getData() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("content", this.content);
        if (this.iconPresent) {
            jsonobject.addProperty("avatar_url", this.icon.toString());
        }

        if (this.embedsPresent) {
            jsonobject.add("embeds", this.getEmbeds());
        }

        jsonobject.addProperty("username", this.name);
        return jsonobject;
    }

    public boolean send() {
        try {
            HttpURLConnection httpurlconnection = (HttpURLConnection)this.url.openConnection();
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setDoOutput(true);
            httpurlconnection.setRequestProperty("Content-Type", "application/json");
            byte[] abyte = this.getData().toString().getBytes(StandardCharsets.UTF_8);
            OutputStream outputstream = httpurlconnection.getOutputStream();
            outputstream.write(abyte);
            httpurlconnection.getResponseCode();
            httpurlconnection.disconnect();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean send(boolean reset) {
        try {
            this.send();
            if (reset) {
                this.reset();
            }

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public void reset() {
        this.content = null;
        this.name = "Gavatron77's Java Webhook Library";
        this.icon = null;
        this.embeds = new ArrayList<>();
        this.iconPresent = false;
        this.embedsPresent = false;
    }

    public String getString() {
        return this.getData().toString();
    }
}