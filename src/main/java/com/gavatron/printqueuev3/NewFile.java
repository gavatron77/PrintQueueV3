package com.gavatron.printqueuev3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import com.gavatron.webhook.Embed;
import com.gavatron.webhook.Webhook;
import org.apache.commons.lang3.StringUtils;

import static com.gavatron.printqueuev3.QueueV3.*;

public class NewFile {
    static String catcard = "";

    static void newFile(Path p) {
        User user = getData();
        System.out.println(user);

        if (!p.toFile().isDirectory()) {
            nameFile(user, p);
        } else {
            try {
                Files.walk(p).forEach(path -> {
                    if (path.toFile().isFile()) nameFile(user, path);
                });
            } catch (Exception e) {
                sendError(e, "Failed while copying files");
            }
        }
    }

    static void nameFile(User user, Path p) {
        String fileName = catcard;
        fileName += " - " + p.getFileName();
        System.out.println(fileName);

        String date = new SimpleDateFormat("MM.dd").format(new Date());

        File pDest = Paths.get(".\\STLs\\" + format(LocalDate.now().getMonth().toString()) + "\\" + user.lastName() + ", " + user.firstName() + "\\" + date).toFile();

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
            if (new Webhook(settings.webhook()).addEmbed(new Embed().setTitle("Error copying file").setDesc(getErrorText(e))).send())
                System.out.println("Unable to copy " + fileName + ". Details were sent to Gavin");
            else System.out.println("Unable to copy " + fileName + ". Details failed to send to Gavin, they are below");
            e.printStackTrace();
        }
    }

    static User getData() {
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

        User user;
        if (users.hasUser(catcard)) {
            user = users.getUser(catcard);
        } else {
            System.out.print("First name: ");
            String firstName = format(s.nextLine());

            System.out.print("Last name: ");
            String lastName = format(s.nextLine());

            System.out.print("Email address: ");
            String email = s.nextLine().trim().toLowerCase();
            user = new User(firstName, lastName, email, new ArrayList<>());
            users.addUser(catcard, user);
        }

        User.Print.Material material = printAndGetFromArray("Materials", User.Print.Material.values());
        User.Print.Purpose purpose = printAndGetFromArray("Purposes", User.Print.Purpose.values());

        User.Print print = new User.Print(material, purpose, User.Print.Status.IMPORTED, null, -1);
        user.prints().add(print);

        return user;
    }

    private static <T extends User.Print.InputValue> T printAndGetFromArray(String name, T[] values) {
        T value = null;
        System.out.println(name + ": ");
        for (int i = 0; i < values.length; i++)
            System.out.println((i + 1) + ": " + values[i].getInputValue());

        while (value == null) {
            try {
                value = values[(Integer.parseInt(s.nextLine().trim()) - 1)];
            } catch (Exception e) {
                System.out.println("Invalid input. " + name + ": ");
            }
        }

        return value;
    }

    static String format(String f) {
        return StringUtils.capitalize(StringUtils.lowerCase(f));
    }
}