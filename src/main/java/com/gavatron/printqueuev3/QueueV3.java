package com.gavatron.printqueuev3;

import com.gavatron.webhook.Embed;
import com.gavatron.webhook.Webhook;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.StreamSupport;

import static com.gavatron.printqueuev3.AddToQueue.addToQueue;
import static com.gavatron.printqueuev3.Done.done;
import static com.gavatron.printqueuev3.NewFile.newFile;
import static com.gavatron.printqueuev3.QueueToCard.queueToCard;

public class QueueV3 {
    public static Scanner s = new Scanner(System.in);
    public static Settings settings = Settings.fromFile(Path.of("./settings.json"));
    public static UserDatabase users = new UserDatabase();
    public static List<QueueEntry> queue = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try {
            users.readFromFile(Path.of(".\\users.json"));
        } catch (Exception e) {
            try {
                new File(".\\users.json").createNewFile();
            } catch (Exception ee) {
                sendError(e, "Failed to create users.json");
            }
        }

        try {
            queue = StreamSupport.stream(JsonParser.parseReader(Files.newBufferedReader(Path.of(".\\queue.json"))).getAsJsonArray().spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .map(QueueEntry::parseJson)
                    .toList();
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

        users.write(Path.of(".\\users.json"));
        writeFile(".\\queue.json", queue.stream().map(QueueEntry::toJson).collect(JsonArray::new, JsonArray::add, JsonArray::addAll).toString());
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
            file = JsonParser.parseReader(new FileReader(p)).getAsJsonObject();
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
        Webhook w = new Webhook(settings.webhook()).setName("QueueV3 Error").setContent(text);
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
        sendError(e, "Unnamed error");
    }
}
