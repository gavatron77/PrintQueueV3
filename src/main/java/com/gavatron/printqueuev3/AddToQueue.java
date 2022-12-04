package com.gavatron.printqueuev3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

import static com.gavatron.printqueuev3.QueueV3.settings;
import static com.gavatron.printqueuev3.QueueV3.users;
import static com.gavatron.printqueuev3.QueueV3.queue;

public class AddToQueue {
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");

    static void addToQueue(Path p) throws IOException {
        List<String> file = Files.readAllLines(p);

        String timeString = "";
        Map<String, Double> pricingValues = new HashMap<>();
        for (String s : file) {
            if (s.contains("total filament used [g] ")) {
                pricingValues.put("filament", (Double.parseDouble(s.split("=")[1].trim()) / 1000.0) * settings.kgPrice());
            } else if (s.contains("(normal mode)")) {
                timeString = s.split("=")[1].trim();
                for (String t : timeString.split(" ")) {
                    double value = Double.parseDouble(t.substring(0, t.length() - 1));
                    if (t.contains("d")) pricingValues.put("days", value);
                    else if (t.contains("h")) pricingValues.put("hours", value);
                    else if (t.contains("m")) pricingValues.put("minutes", value);
                    else if (t.contains("s")) pricingValues.put("seconds", value);
                }
            }
        }

        double time = 0.0;
        double price = 0.0;

        for (Map.Entry<String, Double> o : pricingValues.entrySet()) {
            switch (o.getKey()) {
                case "filament" -> price += o.getValue();
                case "days" -> time += o.getValue() * 24.0;
                case "hours" -> time += o.getValue();
                case "minutes" -> time += (o.getValue() / 60.0);
                case "seconds" -> time += (o.getValue() / 60.0 / 60.0);
            }
        }

        if (time < 12) {
            price += time * settings.hr12Price();
        } else if (time < 24) {
            price += time * settings.hr24Price();
        } else if (time < 48) {
            price += time * settings.hr48Price();
        } else {
            price += time * settings.moreHrs();
        }

        price = Double.parseDouble(PRICE_FORMAT.format(price));

        String catcard = p.getFileName().toString();
        catcard = catcard.substring(0, catcard.indexOf(" "));

        User user = users.getUser(catcard);

        Print latestPrint = user.prints().get(user.prints().size() - 1);
        Print updatedPrint = new Print(
                latestPrint.material(),
                latestPrint.purpose(),
                Print.Status.QUEUED,
                p.getFileName().toString(),
                price);

        user.prints().set(user.prints().size() - 1, updatedPrint);

        QueueEntry entry = new QueueEntry(catcard, users, p, timeString, new Date());
        queue.add(entry);
    }
}
