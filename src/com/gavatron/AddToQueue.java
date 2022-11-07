package com.gavatron;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.gavatron.QueueV3.settings;
import static com.gavatron.QueueV3.users;

public class AddToQueue {
    static void addToQueue(Path p) {
        List<String> file = new ArrayList<>();
        try {
            Scanner s = new Scanner(p.toFile());
            while (s.hasNext()) file.add(s.nextLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject pricing = new JsonObject();
        for (String s : file) {
            if (s.contains("total filament used [g] "))
                pricing.addProperty("filament", (Double.parseDouble(s.split("=")[1].trim()) / 1000) * settings.get("kgprice").getAsDouble());
            else if (s.contains("(normal mode)")) for (String t : s.split("=")[1].trim().split(" ")) {
                double value = Double.parseDouble(t.substring(0, t.length() - 1));
                if (t.contains("d")) pricing.addProperty("days", value);
                else if (t.contains("h")) pricing.addProperty("hours", value);
                else if (t.contains("m")) pricing.addProperty("minutes", value);
                else if (t.contains("s")) pricing.addProperty("seconds", value);
            }
        }

        Double time = 0.0;
        Double price = 0.0;

        for (Map.Entry<String, JsonElement> o : pricing.entrySet()) {
            switch (o.getKey()) {
                case "filament" -> price += o.getValue().getAsDouble();
                case "days" -> time += o.getValue().getAsDouble() * 24.0;
                case "hours" -> time += o.getValue().getAsDouble();
                case "minutes" -> time += (o.getValue().getAsDouble() / 60.0);
                case "seconds" -> time += (o.getValue().getAsDouble() / 60.0 / 60.0);
            }
        }

        if (time < 12) price += time * settings.get("12hrs").getAsDouble();
        else if (time < 24) price += time * settings.get("24hrs").getAsDouble();
        else if (time < 48) price += time * settings.get("48hrs").getAsDouble();
        else price += time * settings.get("morehrs").getAsDouble();

        {
            DecimalFormat format = new DecimalFormat("0.00");
            price = Double.valueOf(format.format(price));

            String name = p.getFileName().toString();
            name = name.substring(0, name.indexOf(" "));

            if (users.has(name)) {
                JsonObject user = users.get(name).getAsJsonObject();
                JsonArray prints = user.get("prints").getAsJsonArray();
                JsonObject print = prints.get(prints.size() - 1).getAsJsonObject();

                JsonArray prices = new JsonArray();
                if (print.has("prices")) prices = print.get("prices").getAsJsonArray();

                JsonObject printData = new JsonObject();
                printData.addProperty("price", price);
                printData.addProperty("fileName", p.getFileName().toString());

                prices.add(printData);
                print.add("prices", prices);

                print.addProperty("status", "sliced");
                prints.set(prints.size() - 1, print);
                user.add("prints", prints);
                users.add(name, user);
            }
        }
    }
}
