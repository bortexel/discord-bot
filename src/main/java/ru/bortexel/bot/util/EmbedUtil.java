package ru.bortexel.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;

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

    public static EmbedBuilder makeDefaultEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(BortexelBot.EMBED_COLOR);
        return builder;
    }
}
