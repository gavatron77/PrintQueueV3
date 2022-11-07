package com.gavatron;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.gavatron.QueueV3.*;

public class NewFile {
    static String catcard = "";
    static List<String> mats = Arrays.asList("PLA", "PETG", "TPU", "Resin", "Laser", "Machining", "Other...");
    static List<String> purposes = Arrays.asList("Personal", "103", "310r", "494", "Capstone", "Research", "Other...");

    static void newFile(Path p) {
        JsonObject data = getData();
        System.out.println(data);

        if (!p.toFile().isDirectory()) {
            nameFile(data, p);
        } else {
            try {
                Files.walk(p).forEach(path -> {
                    if (path.toFile().isFile()) nameFile(data, path);
                });
            } catch (Exception e) {
                sendError(e, "Failed while copying files");
            }
        }

        users.add(catcard, data);
    }

    static void nameFile(JsonObject data, Path p) {
        String fileName = catcard;
        fileName += " - " + p.getFileName();
        System.out.println(fileName);

        String date = new SimpleDateFormat().format(new Date());
        date = date.split("/")[0] + "/" + date.split("/")[1];
        for (int i = 0; i < date.length(); i++)
            if (date.charAt(i) == '/') date = date.split("/")[0] + "." + date.split("/")[1];

        File pDest = Paths.get(".\\STLs\\" + format(LocalDate.now().getMonth().toString()) + "\\" + data.get("lastname").getAsString() + ", " + data.get("firstname").getAsString() + "\\" + date).toFile();

        if (!pDest.exists()) pDest.mkdirs();

        try {
            Path pFile = Path.of(pDest + "\\" + fileName);
            if (pFile.toFile().exists()) {
                File f = pFile.toFile();
                File newFile = new File(pDest + "\\" + f.getName().substring(0, f.getName().lastIndexOf(".")) + "_OLD" + f.getName().substring(f.getName().lastIndexOf(".")));
                if (newFile.exists()) newFile.delete();
                f.renameTo(newFile);
            }

            Files.copy(p, Path.of(pDest + "\\" + fileName));
        } catch (NoSuchFileException e) {
            System.out.println("Unable to copy " + fileName + ", as it was not found. Is the USB drive still plugged in?");
        } catch (Exception e) {
            if (new Webhook(settings.get("webhook").getAsString()).addEmbed(new Embed().setTitle("Error copying file").setDesc(getErrorText(e))).send())
                System.out.println("Unable to copy " + fileName + ". Details were sent to Gavin");
            else System.out.println("Unable to copy " + fileName + ". Details failed to send to Gavin, they are below");
            e.printStackTrace();
        }
    }

    static JsonObject getData() {
        JsonObject userData = new JsonObject();
        System.out.println("Please press enter after entering the data requested.");

        {
            boolean good = false;
            while (!good) {
                System.out.print("Catcard number: ");
                try {
                    catcard = s.nextLine().trim();
                    good = true;
                } catch (Exception e) {
                    System.out.print("CatCard number was formatted incorrectly. Please try again: ");
                }
            }
        }

        JsonArray prints = new JsonArray();

        if (userData.has(catcard)) userData = users.get(catcard).getAsJsonObject();
        else {
            System.out.print("First name: ");
            userData.addProperty("firstname", format(s.nextLine()));

            System.out.print("Last name: ");
            userData.addProperty("lastname", format(s.nextLine()));

            System.out.print("Email address: ");
            userData.addProperty("email", s.nextLine().trim().toLowerCase());
        }

        if (userData.has("prints")) prints = userData.get("prints").getAsJsonArray();

        JsonObject printData = new JsonObject();

        System.out.println("Material: ");
        for (int i = 0; i < mats.size(); i++)
            System.out.println((i + 1) + ": " + mats.get(i));

        {
            boolean good = false;
            while (!good) try {
                printData.addProperty("material", mats.get(Integer.parseInt(s.nextLine().trim()) - 1));
                good = true;
            } catch (Exception e) {
                System.out.println("Invalid input. Material: ");
            }
        }

        System.out.println("Purpose: ");
        for (int i = 0; i < purposes.size(); i++)
            System.out.println((i + 1) + ": " + purposes.get(i));

        {
            boolean good = false;
            while (!good) try {
                printData.addProperty("purpose", purposes.get(Integer.parseInt(s.nextLine().trim()) - 1));
                good = true;
            } catch (Exception e) {
                System.out.println("Invalid input. Purpose: ");
            }
        }

        printData.addProperty("status", "imported");
        prints.add(printData);

        userData.add("prints", prints);

        return userData;
    }

    static String format(String f) {
        f = StringUtils.lowerCase(f);
        f = StringUtils.capitalize(f);

        return f;
    }
}