package com.gavatron;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.gavatron.QueueV3.queue;

public class QueueToCard {
    static void queueToCard() {
        Map<String, Integer> longs = new HashMap<>();
        longs.put("name", 4);
        longs.put("path", 8);
        longs.put("time", 4);
        longs.put("date", 4);

        for (JsonElement p : queue) {
            JsonObject print = p.getAsJsonObject();

            {
                int l = print.get("firstname").getAsString().length() + print.get("lastname").getAsString().length();
                if (l > longs.get("name")) {
                    longs.put("name", l);
                }
            }

            if (print.get("date").getAsString().length() > longs.get("date")) {
                longs.put("date", print.get("date").getAsString().length());
            }

            if (print.get("time").getAsString().length() > longs.get("time")) {
                longs.put("time", print.get("time").getAsString().length());
            }

            {
                int fileLength = Path.of(print.get("path").getAsString()).getFileName().toString().length();
                if (fileLength > longs.get("path")) {
                    longs.put("path", fileLength);
                }
            }
        }

        System.out.print("|");
        for (int i = 0; i < (longs.get("name") + longs.get("path") + longs.get("date")); i++)
            System.out.print("-");
        System.out.println("|");

        for (Map.Entry<String, Integer> s : longs.entrySet()) {
            System.out.println(s.getKey() + " " + s.getValue());
        }

        int counter = 1;
        for (JsonElement p : queue) {
            JsonObject print = p.getAsJsonObject();
            System.out.print("| " + counter++ + " | ");
            String name = print.get("firstname").getAsString() + " " + print.get("lastname").getAsString();
            System.out.print(name);
            for(int i = 0; i < name.length() - longs.get("name"); i++) System.out.print(" ");

            System.out.print(print.get("time").getAsString());
            for(int i = 0; i < print.get("time").getAsString().length() - longs.get("time"); i++) System.out.print(" ");
            System.out.println();
        }
    }
}
