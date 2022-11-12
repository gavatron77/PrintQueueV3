package com.gavatron;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

import static com.gavatron.AddToQueue.addToQueue;
import static com.gavatron.Done.done;
import static com.gavatron.NewFile.newFile;
import static com.gavatron.QueueToCard.queueToCard;

public class QueueV3 {
    static Scanner s = new Scanner(System.in);
    static JsonObject settings = readJsonFile(".\\settings.json");
    static JsonObject users = new JsonObject();
    static JsonArray queue = new JsonArray();

    public static void main(String[] args) throws IOException {
        if (settings == null) {
            try {
                JsonObject template = new JsonObject();
                template.addProperty("webhook", "Discord Webhook URL here");
                template.addProperty("kgprice", 60.0);
                template.addProperty("12hrs", 0.5);
                template.addProperty("24hrs", 0.6);
                template.addProperty("48hrs", 0.7);
                template.addProperty("morehrs", 0.8);

                JsonArray a = new JsonArray();
                a.add("A:");
                a.add("B:");
                template.add("sdcards", a);
                new File(".\\settings.json").createNewFile();
                FileWriter f = new FileWriter(".\\settings.json");
                f.write(String.valueOf(template));
                f.close();
                System.out.println("Settings file doesn't exist. Template file has been created, please edit it. Exiting.");
            } catch (Exception ee) {
                System.out.println("Settings file doesn't exist. Unable to create template file. Exiting.");
            }
            return;
        }

        try {
            users = readJsonFile(".\\users.json");
        } catch (Exception e) {
            try {
                new File(".\\users.json").createNewFile();
            } catch (Exception ee) {
                sendError(e, "Failed to create users.json");
            }
        }

        try {
            queue = JsonParser.parseString(new Scanner(new File(".\\queue.json")).nextLine()).getAsJsonArray();
        } catch (Exception e) {
            try {
                new File(".\\queue.json").createNewFile();
            } catch (Exception ee) {
                sendError(e, "Failed to create queue.json");
            }
        }

        if (args.length == 0) {
            System.out.println("1: Email\n2: Pickup\n3: Charge");

            int g = Integer.parseInt(s.nextLine().trim());
            switch (g) {
                case 1:
//                    smartDone();
                    break;
                case 2:
//                    pickUp();
                    break;
                case 3:
//                    charge();
                    break;
            }
        } else if (args.length == 1) {
            switch (args[0]) {
                case "done" -> done();
                case "card" -> queueToCard();
            }
        } else if (args.length == 2) {
            switch (args[0]) {
                case "new" -> newFile(Path.of(args[1]));
                case "queue" -> addToQueue(Path.of(args[1]));
            }
        }

        writeFile(".\\users.json", users.toString());
        writeFile(".\\queue.json", queue.toString());
    }

    static String parseFile(String s) {
        String e = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\') e += "\\\\";
            else e += s.charAt(i);
        }

        return e;
    }


    static JsonObject readJsonFile(String p) {
        JsonObject file;

        try {
            String raw = "";
            Scanner s = new Scanner(new File(p));
            while (s.hasNext()) raw += s.nextLine();
            s.close();
            file = JsonParser.parseString(parseFile(raw)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }

        return file;
    }

    static String getErrorText(Exception err) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        err.printStackTrace(pw);
        return sw.toString();
    }

    static void sendError(Exception e, String text) {
        Webhook w = new Webhook(settings.get("webhook").getAsString()).setName("QueueV3 Error").setContent(text);
        Embed em = new Embed();
        em.setDesc(getErrorText(e));
        w.addEmbed(em).send();
    }

    static void writeFile(String path, String data) throws IOException {
        FileWriter f = new FileWriter(path);
        f.write(data);
        f.close();
    }

    static void sendError(Exception e) {
        sendError(e, "Unnamed errror");
    }
}
