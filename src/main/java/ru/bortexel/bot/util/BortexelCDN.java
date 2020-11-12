package ru.bortexel.bot.util;

public class BortexelCDN {
    public static final String ENDPOINT_URL = "https://cdn.bortexel.ru/";
    public static final String DISCORD_ENDPOINT_URL = ENDPOINT_URL + "discord/";
    public static final String ITEMS_ENDPOINT_URL = ENDPOINT_URL + "images/items/";

    public static String getIconUrl(String icon) {
        return DISCORD_ENDPOINT_URL + icon + ".png";
    }

    public static String getItemIconUrl(String item) {
        if (item.startsWith("e_")) item = "enchanted_book";
        return ITEMS_ENDPOINT_URL + item + ".png";
    }
}
