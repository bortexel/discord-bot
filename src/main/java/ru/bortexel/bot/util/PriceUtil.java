package ru.bortexel.bot.util;

public class PriceUtil {
    public static String formatPrice(double price) {
        if (price > 10) price = Math.round(price);

        if (price > 64) {
            int count = (int) Math.floor(price / 64);
            int integer = count * 64;
            double other = price - integer;
            return other == 0 ? count + " ст." : count + " ст. + " + Math.round(other);
        }

        return "" + price;
    }

    public static String formatName(String name, String category) {
        if (category.contains("Зачарования")) {
            return "книгу \"" + name + "\"";
        } else return name.toLowerCase();
    }
}
