package ru.bortexel.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.models.BotRole;
import ru.ruscalworld.bortexel4j.models.economy.Item;

import java.util.ArrayList;
import java.util.List;

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

    public static EmbedBuilder makeCommandInfo(Command command) {
        EmbedBuilder builder = makeDefaultEmbed();
        builder.setTitle("Команда **`" + BortexelBot.COMMAND_PREFIX + command.getName() + "`**");
        if (command.getDescription() != null) builder.setDescription(command.getDescription());
        if (command.getUsage() != null)
            builder.addField("Использование", "`" + TextUtil.getFullCommandUsage(command) + "`", false);
        if (command.getUsageExample() != null)
            builder.addField("Пример использования", command.getUsageExample(), false);
        if (command.getAliases().length > 0) builder.addField("Сокращения", "`" + BortexelBot.COMMAND_PREFIX +
                String.join("`, `" + BortexelBot.COMMAND_PREFIX, command.getAliases()) + "`", false);

        StringBuilder access = new StringBuilder("Общедоступна");
        if (command.getAccessLevel() != null) {
            access = new StringBuilder();
            for (Role role : command.getAccessLevel().getRoles()) {
                access.insert(0, role.getAsMention() + " ");
            }
        }
        builder.addField("Доступ", access.toString(), false);
        if (command.getAllowedChannelIds().length > 0)
            builder.addField("Разрешённые каналы", "<#" + String.join(">, <#", command.getAllowedChannelIds()) + ">", false);

        return builder;
    }

    public static EmbedBuilder makeRoleInfo(BotRole role) {
        Role discordRole = role.getDiscordRole();
        EmbedBuilder builder = makeDefaultEmbed();
        builder.setTitle(role.getTitle());
        builder.setDescription(role.getDescription());
        builder.setColor(discordRole.getColor());

        if (role.getJoinInfo() != null) builder.addField("Как вступить?", role.getJoinInfo(), false);

        if (role.getHeadmasterID() != null) {
            Member headmaster = role.getHeadmaster().complete();
            if (headmaster != null) builder.addField("Глава", headmaster.getAsMention(), false);
        }

        if (role.isShowMembers()) {
            List<Member> members = discordRole.getGuild().getMembersWithRoles(discordRole);
            List<String> mentions = new ArrayList<>();
            for (Member member : members) mentions.add(member.getAsMention());
            builder.addField("Участники (" + members.size() + ")", String.join(", ", mentions), false);
        }

        return builder;
    }

    public static EmbedBuilder makeDefaultEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(BortexelBot.EMBED_COLOR);
        return builder;
    }
}
