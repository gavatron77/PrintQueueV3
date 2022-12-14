package com.gavatron.printqueuev3;

import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record Print(Material material, Purpose purpose, Status status, String fileName, double price) {
    enum Purpose implements InputValue {
        PERSONAL("Personal"), CLASS_103("103"), CLASS_310r("310r"), CLASS_494("494"), CAPSTONE("Capstone"), RESEARCH("Research"), OTHER("Other...");

        private final String inputValue;

        Purpose(String inputValue) {
            this.inputValue = inputValue;
        }

        @Override
        public String getInputValue() {
            return inputValue;
        }
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("material", this.material.name());
        object.addProperty("purpose", this.purpose.inputValue);
        object.addProperty("status", this.status.name().toLowerCase());
        object.addProperty("fileName", this.fileName);
        object.addProperty("price", this.price);

        return object;
    }

    public static Print parseJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        return new Print(
                Arrays.stream(Material.values()).filter(p -> p.inputValue.equals(object.get("material").getAsString())).findFirst().orElse(Material.OTHER),
                Arrays.stream(Purpose.values()).filter(p -> p.inputValue.equals(object.get("purpose").getAsString())).findFirst().orElse(Purpose.OTHER),
                Status.valueOf(object.get("status").getAsString().toUpperCase()),
                object.get("fileName").getAsString(),
                object.get("price").getAsDouble());
    }

    enum Material implements InputValue {
        PLA("PLA"), PETG("PETG"), TPU("TPU"), RESIN("Resin"), LASER("Laser"), MACHINING("Machining"), OTHER("Other...");

        private final String inputValue;

        Material(String inputValue) {
            this.inputValue = inputValue;
        }

        @Override
        public String getInputValue() {
            return inputValue;
        }
    }

    enum Status {
        IMPORTED, QUEUED, PRINTING, DONE, CLAIMED, CHARGED
    }

    interface InputValue {
        String getInputValue();
    }
}
