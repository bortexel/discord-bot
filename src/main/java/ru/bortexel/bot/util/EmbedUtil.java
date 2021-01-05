package ru.bortexel.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.ruscalworld.bortexel4j.models.economy.Item;

public class EmbedUtil {
    public static EmbedBuilder makeError(@Nullable String title, String text) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(0xE74C3C);
        builder.setAuthor(title == null ? "Ошибка" : title, null, BortexelCDN.getIconUrl("cross"));
        builder.setDescription(text);
        return builder;
    }

    public static EmbedBuilder makeCommandUsage(Command command) {
        EmbedBuilder builder = EmbedUtil.makeError("Некорректное использование",
                "Проверьте, что все необходимые аргументы указаны.");
        builder.addField("Использование", "`" + TextUtil.getFullCommandUsage(command) + "`", false);
        if (command.getUsageExample() != null) {
            builder.addField("Пример использования", command.getUsageExample(), false);
        }

        return builder;
    }

    public static EmbedBuilder makeItemPriceInfo(Item.ItemPrices itemPrices) {
        EmbedBuilder builder = makeDefaultEmbed();
        Item item = itemPrices.getItem();
        Item.ItemPrice price = itemPrices.getPrices().get(itemPrices.getPrices().size() - 1);
        float fPrice = price.getPrice();

        builder.setTitle("Средняя цена на " + PriceUtil.formatName(item.getName(), item.getCategory()));
        builder.setThumbnail(item.getIconURL());
        builder.addField("За 1", PriceUtil.formatPrice(fPrice) + " " + Emojis.GOLD_ORE, true);
        builder.addField("За 32", PriceUtil.formatPrice(fPrice * 32) + " " + Emojis.GOLD_ORE, true);
        builder.addField("За 64", PriceUtil.formatPrice(fPrice * 64) + " " + Emojis.GOLD_ORE, true);
        builder.setTimestamp(price.getTime().toLocalDateTime());
        builder.setFooter("Последнее обновление");

        return builder;
    }

    public static EmbedBuilder makeDefaultEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(BortexelBot.EMBED_COLOR);
        return builder;
    }
}
