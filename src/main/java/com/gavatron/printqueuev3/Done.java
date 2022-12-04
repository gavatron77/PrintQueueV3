package com.gavatron.printqueuev3;


import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import static com.gavatron.printqueuev3.QueueV3.settings;

public class Done {
    public static void findAndCompleteMostRecent() {
        Optional<File> potentialDrive;

        do {
            potentialDrive = Arrays.stream(settings.sdCards())
                    .map(File::new)
                    .filter(File::exists)
                    .findFirst();
        } while (potentialDrive.isEmpty());

        File card = potentialDrive.get();


        Arrays.stream(card.list()).map(s -> card + s)
                .map(File::new)
                .max(Comparator.comparing(File::lastModified, Long::compare))
                // TODO: Set to done in the queue
                .ifPresent(System.out::println);
    }
}
