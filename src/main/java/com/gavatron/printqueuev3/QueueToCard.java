package com.gavatron.printqueuev3;

import java.nio.file.Path;
import java.util.Date;

import static com.gavatron.printqueuev3.QueueV3.queue;

public class QueueToCard {
    static void queueToCard() {
        int nameLength = queue.stream().map(entry -> entry.firstName() + " " + entry.lastName()).mapToInt(String::length).filter(l -> l > 4).max().orElse(4);
        int pathLength = queue.stream().map(QueueEntry::path).map(Path::toString).mapToInt(String::length).filter(l -> l > 8).max().orElse(8);
        int timeLength = queue.stream().map(QueueEntry::time).mapToInt(String::length).filter(l -> l > 4).max().orElse(4);
        int dateLength = queue.stream().map(QueueEntry::date).map(Date::toString).mapToInt(String::length).filter(l -> l > 4).max().orElse(4);

        System.out.print("|");
        for (int i = 0; i < (nameLength + pathLength + dateLength); i++)
            System.out.print("-");
        System.out.println("|");

        int counter = 1;
        for (QueueEntry entry : queue) {
            System.out.print("| " + counter++ + " | ");

            String name = entry.firstName() + " " + entry.lastName();
            System.out.print(name);
            System.out.print(" ".repeat(name.length() - nameLength));

            System.out.print(entry.time());
            System.out.print(" ".repeat(entry.time().length() - timeLength));

            System.out.println();
        }
    }
}
