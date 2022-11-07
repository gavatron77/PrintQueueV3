package com.gavatron;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

import static com.gavatron.AddToQueue.addToQueue;
import static com.gavatron.NewFile.newFile;

public class QueueV3 {
    static Scanner s = new Scanner(System.in);
    static JsonObject settings = readJsonFile(".\\settings.json");
    static JsonObject users = new JsonObject();
    static JsonObject prints = new JsonObject();

    public static void main(String[] args) throws IOException {
        if (settings == null) {
            try {
                JsonObject template = new JsonObject();
                template.addProperty("webhook", "Discord Webhook URL here");
                template.addProperty("kgprice", 60.0);
                template.addProperty("hrprice", 0.5);
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
            users = new JsonObject();
        }

//        try {
//            users = JsonParser.parseString(new Scanner(new File(".\\queue.json")).nextLine()).getAsJsonObject();
//        } catch (Exception e) {
//            try {
//                new File(".\\queue.json").createNewFile();
//            } catch (Exception ee) {
//                new Webhook(settings.get("webhook").getAsString()).setName("QueueV3 Error").setContent("Failed to create queue.json").send();
//            }
//            users = new JsonObject();
//        }

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
        } else if (args.length == 2) {
            switch (args[0]) {
                case "new" -> newFile(Path.of(args[1]));
                case "queue" -> addToQueue(Path.of(args[1]));
            }
        }

        FileWriter f = new FileWriter(".\\users.json");
        f.write(users.toString());
        f.close();
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

    static void sendError(Exception e) {
        sendError(e, "Unnamed errror");
    }
}
