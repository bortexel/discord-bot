package ru.bortexel.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.models.BotRole;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.city.City;
import ru.ruscalworld.bortexel4j.models.economy.Item;
import ru.ruscalworld.bortexel4j.models.forms.Question;
import ru.ruscalworld.bortexel4j.models.forms.WhitelistForm;
import ru.ruscalworld.bortexel4j.models.profile.Profile;
import ru.ruscalworld.bortexel4j.models.shop.Shop;
import ru.ruscalworld.bortexel4j.models.user.User;
import ru.ruscalworld.bortexel4j.rules.RulePart;
import ru.ruscalworld.bortexel4j.util.BortexelSkins;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        builder.addField("За 1", PriceUtil.formatPrice(fPrice) + " " + Emojis.DIAMOND_ORE, true);
        builder.addField("За 32", PriceUtil.formatPrice(fPrice * 32) + " " + Emojis.DIAMOND_ORE, true);
        builder.addField("За 64", PriceUtil.formatPrice(fPrice * 64) + " " + Emojis.DIAMOND_ORE, true);
        builder.setTimestamp(price.getTime().toLocalDateTime());
        builder.setFooter("Последнее обновление");

        return builder;
    }

    public static EmbedBuilder makeCommandInfo(Command command) {
        EmbedBuilder builder = makeDefaultEmbed();
        builder.setTitle("Команда **`" + CommandUtil.getPreferredPrefix(command) + command.getName() + "`**");
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

    public static EmbedBuilder makeShortRoleInfo(BotRole role) {
        Role discordRole = role.getDiscordRole();
        List<Member> members = discordRole.getGuild().getMembersWithRoles(discordRole);

        String more = "";
        if (role.getInfoMessage() != null) more = "[Подробнее...](" + role.getInfoMessage().getJumpUrl() + ")";

        EmbedBuilder builder = makeDefaultEmbed();
        builder.setTitle(role.getTitle() != null ? role.getTitle() : discordRole.getName());
        builder.setDescription(role.getDescription() + " " + more);
        builder.setColor(discordRole.getColor());
        builder.setFooter("Эта роль есть у " + members.size() + " " +
                TextUtil.getPlural(members.size(), "участника", "участников", "участников")
        );

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

        if (role.shouldShowMembers()) {
            List<Member> members = discordRole.getGuild().getMembersWithRoles(discordRole);
            List<String> mentions = new ArrayList<>();
            for (Member member : members) mentions.add(member.getAsMention());
            builder.addField("Участники (" + members.size() + ")", String.join(", ", mentions), false);
        }

        return builder;
    }

    public static Color getShopColor(Shop shop) {
        if (shop.getPosition() == null || shop.getPosition().getObjectName() == null)
            return defaultShopColor(shop);

        switch (shop.getPosition().getObjectName()) {
            case "Красная линия":
                return Color.decode("#FF5555");
            case "Синяя линия":
                return Color.decode("#5555FF");
            case "Зелёная линия":
                return Color.decode("#00AA00");
            case "Жёлтая линия":
                return Color.decode("#FFFF55");
            case "Розовая линия":
                return Color.decode("#FF55FF");
            case "Бирюзовая линия":
                return Color.decode("#00AAAA");
            case "Лаймовая линия":
                return Color.decode("#55FF55");
            case "Оранжевая линия":
                return Color.decode("#FFAA00");
            default:
                return defaultShopColor(shop);
        }
    }

    private static Color defaultShopColor(Shop shop) {
        int seed = shop.getName().hashCode();
        if (shop.getPosition() != null && shop.getPosition().getObjectName() != null)
            seed = shop.getPosition().getObjectName().hashCode();
        return randomColor(seed).brighter();
    }

    public static EmbedBuilder makeShopInfo(Shop shop, Account ownerAccount, User ownerPlayer) {
        Shop.Position position = shop.getPosition();
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(shop.getName(), "https://bort.su/s/" + shop.getID());
        builder.setDescription(shop.getDescription());
        builder.addField("Категории товаров", shop.getItems(), false);

        String locationString = TextUtil.makeLocation(shop.getLocation());
        if (shop.getPosition() != null) locationString = position.getObjectName() + ", " + position.getLocation();
        builder.addField("Местоположение", locationString, true);

        if (ownerAccount != null && ownerPlayer != null) {
            builder.addField("Владелец", TextUtil.makeUserName(ownerPlayer.getUsername(), ownerAccount.getDiscordID()), true);
        }

        builder.setImage(shop.getImages().getScreenshotURL());
        builder.setColor(getShopColor(shop));
        return builder;
    }

    public static EmbedBuilder makeCityInfo(City city, Account ownerAccount, User ownerPlayer) {
        City.Points points = city.getPoints();
        EmbedBuilder builder = new EmbedBuilder();
        String mapLink = new MapReference(city.getLocation(), "vanilla").getBlueMapLinkMarkdown(100);

        builder.setTitle(city.getName(), "https://bort.su/c/" + city.getID());
        builder.setDescription(city.getDescription());
        builder.addField("Стилистика", city.getStyle(), true);
        builder.addField("Дата основания", "<t:" + (city.getFoundedAt().getTime() / 1000) + ":D>", true);
        builder.addField("Местоположение", TextUtil.makeLocation2D(city.getLocation()) + " " + mapLink, false);
        builder.addField("Точки",
                "Верхний мир: `/где " + points.getOverworld() + "` \n" +
                "Нижний мир: `/где " + points.getNether() + "`", false);

        if (ownerAccount != null && ownerPlayer != null) {
            builder.addField("Представитель", TextUtil.makeUserName(ownerPlayer.getUsername(), ownerAccount.getDiscordID()), true);
        }

        builder.setImage(city.getImages().getScreenshotURL());
        builder.setThumbnail(city.getImages().getEmblemURL());
        builder.setColor(randomColor(city.getName().hashCode()).brighter());
        return builder;
    }

    public static EmbedBuilder makeWhitelistFormInfo(WhitelistForm form, Account account) {
        String flag = "";
        if (form.getAddress().getCountryCode() != null) {
            String flagEmoji = TextUtil.getFlagEmoji(form.getAddress().getCountryCode());
            if (flagEmoji != null) flag = flagEmoji;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Заявка #" + form.getID() + " от " + form.getUsername().replace("_", "\\_"));
        builder.setColor(Color.decode("#2F3136"));
        builder.addField("E-mail", account.getEmail(), true);
        builder.addField("IP", flag + " " + form.getAddress().getAddress().getHostAddress(), true);
        builder.addField("Discord", account.getDiscordID() == null ? "Нет" : "<@" + account.getDiscordID() + ">", true);

        for (Question question : form.getQuestions()) {
            if (question.getAnswer() == null || question.getAnswer().length() < 1) continue;
            builder.addField(question.getQuestion(), question.getAnswer(), false);
        }

        if (form.getAdminID() != null) {
            builder.setFooter((form.getConclusion() == 1 ? "Принял " : "Отклонил ") + form.getAdminName());
            builder.setTimestamp(form.getReviewedAt().toInstant());
        } else builder.setFooter("Не рассмотрена");
        return builder;
    }

    public static EmbedBuilder makeRuleInfo(RulePart.Rule rule) {
        EmbedBuilder builder = makeDefaultEmbed();
        builder.setTitle("Правило " + rule.getName());
        builder.setDescription(rule.getText());

        if (rule.getRules() != null && rule.getRules().size() > 0) {
            List<String> childNames = new ArrayList<>();
            for (RulePart.Rule childRule : rule.getRules()) childNames.add(childRule.getName());
            builder.addField("Дочерние правила", "`" + String.join("`, `", childNames) + "`", false);
        }

        if (rule.getPunishments() != null && rule.getPunishments().size() > 0) {
            List<String> punishments = new ArrayList<>();
            for (String punishment : rule.getPunishments()) punishments.add(" • " + punishment);
            builder.addField("Наказания", String.join("\n", punishments), false);
        }

        return builder;
    }

    public static EmbedBuilder makeProfileEmbed(Profile profile) {
        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setAuthor(profile.getUsername(), null, BortexelSkins.getAvatarURL(profile.getUsername(), true));
        builder.setThumbnail(BortexelSkins.getBodyRenderURL(profile.getUsername(), true));
        return builder;
    }

    public static Color randomColor(int seed) {
        Random random = new Random(seed);
        return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public static EmbedBuilder makeDefaultEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(BortexelBot.EMBED_COLOR);
        return builder;
    }
}
