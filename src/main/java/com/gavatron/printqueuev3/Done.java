package com.gavatron.printqueuev3;

import com.google.gson.JsonArray;

import java.io.File;
import java.util.Scanner;

import static com.gavatron.printqueuev3.QueueV3.settings;

public class Done {
    static void done() {
        File card = new File("");
        String[] drives = settings.sdCards();

        while (!card.exists()) {
            for (String drive : drives) {
                if (new File(drive).exists()) card = new File(drive);
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
