package ru.bortexel.bot.resources;

public enum ResourceType {
    CITY("city"),
    SHOP("shop")
    ;

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ResourceType fromString(String value) {
        for (ResourceType type : ResourceType.values()) {
            if (type.getValue().equals(value)) return type;
        }

        return null;
    }
}
