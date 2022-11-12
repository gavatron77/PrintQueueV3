package com.gavatron;

import com.google.gson.JsonArray;

import java.io.File;
import java.util.Scanner;

import static com.gavatron.QueueV3.settings;

public class Done {
    static void done() {
        File card = new File("");
        JsonArray drives = settings.get("sdcards").getAsJsonArray();

        while (!card.exists()) {
            for (int i = 0; i < drives.size(); i++) {
                if (new File(drives.get(i).getAsString()).exists()) card = new File(drives.get(i).getAsString());
            }
        }

        long date = 0;
        String fileName = "";

        for (String file : card.list()) {
            long d = new File(card + file).lastModified();
            if ((d > date) && (file.contains("gcode"))) {
                date = new File(card + file).lastModified();
                fileName = file;
            }
        }

        try {
            Scanner f = new Scanner(new File(card + fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(card + fileName);
    }
}
