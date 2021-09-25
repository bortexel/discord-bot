package ru.bortexel.bot.util;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.core.Command;
import ru.ruscalworld.bortexel4j.util.Location;

import java.sql.Timestamp;
import java.util.Locale;

public class TextUtil {
    public static String removeDoubleSpaces(String string) {
        while (string.contains("  ")) string = string.replace("  ", " ");
        return string;
    }

    public static String getFullCommandUsage(Command command) {
        return CommandUtil.getPreferredPrefix(command) + command.getName() + (command.getUsage() == null ? "" : " " + command.getUsage());
    }

    public static String[] getCommandArgs(Message message) {
        return TextUtil.getCommandArgs(message.getContentRaw());
    }

    public static String[] getCommandArgs(String content) {
        String text = TextUtil.removeDoubleSpaces(content);
        return text.substring(1).split(" ");
    }

    public static String getPlural(int count, String one, String two, String five) {
        if (count % 10 == 1 && count % 100 != 11) {
            return one;
        } else if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) return two;
        return five;
    }

    public static String getNumberEmoji(int number) {
        switch (number) {
            default:
            case 0:
                return EmojiParser.parseToUnicode(":zero:");
            case 1:
                return EmojiParser.parseToUnicode(":one:");
            case 2:
                return EmojiParser.parseToUnicode(":two:");
            case 3:
                return EmojiParser.parseToUnicode(":three:");
            case 4:
                return EmojiParser.parseToUnicode(":four:");
            case 5:
                return EmojiParser.parseToUnicode(":five:");
            case 6:
                return EmojiParser.parseToUnicode(":six:");
            case 7:
                return EmojiParser.parseToUnicode(":seven:");
            case 8:
                return EmojiParser.parseToUnicode(":eight:");
            case 9:
                return EmojiParser.parseToUnicode(":nine:");
            case 10:
                return EmojiParser.parseToUnicode(":keycap_ten:");
        }
    }

    public static String makeDefaultProgressBar(int percentage, int length) {
        return makeProgressBar(percentage, length, Emojis.PB_START, Emojis.PB_MIDDLE, Emojis.PB_END, Emojis.PB_SINGLE);
    }

    public static String makeProgressBar(int percentage, int length, String firstChar, String middleChar, String lastChar, String single) {
        StringBuilder progress = new StringBuilder();
        for (int i = 0; i < length; i++) {
            float currentPercentage = (float) i / (float) length * 100;
            float nextPercentage = (float) (i + 1) / (float) length * 100;
            boolean isFirst = i == 0;
            boolean isLast = nextPercentage >= percentage;

            if (isFirst && isLast) return single;

            if (percentage > currentPercentage) {
                if (isFirst) {
                    progress.append(firstChar);
                } else if (isLast) {
                    progress.append(lastChar);
                    return progress.toString();
                } else progress.append(middleChar);
            }
        }
        return progress.toString();
    }

    public static String removeSpacesInStart(String string) {
        while (string.startsWith(" ")) string = string.substring(1);
        return string;
    }

    public static String makeLocation(Location location) {
        return String.format("%s %s %s (%s)", location.getX(), location.getY(), location.getZ(), getLocalizedWorldName(location.getWorld()));
    }

    public static String makeLocation2D(Location location) {
        return String.format("%s %s (%s)", location.getX(), location.getZ(), getLocalizedWorldName(location.getWorld()));
    }

    public static String getLocalizedWorldName(String world) {
        switch (world) {
            case "world":
            case "overworld":
                return "верхний мир";
            case "world_nether":
            case "the_nether":
                return "нижний мир";
            case "world_the_end":
            case "the_end":
                return "Энд";
            default:
                return world;
        }
    }

    public static String makeUserName(@NotNull String username, @Nullable String discordID) {
        String ownerInfo = username.replace("_", "\\_");
        if (discordID != null) ownerInfo += " (<@" + discordID + ">)";
        return ownerInfo;
    }

    public static String getFlagEmoji(String countryCode) {
        String emoji = String.format(":%s:", countryCode.toLowerCase(Locale.ROOT));
        String parsed = EmojiParser.parseToUnicode(emoji);
        if (emoji.equals(parsed)) return null;
        return parsed;
    }

    public static String nullable(String prefix, String value, String suffix, String def) {
        if (value == null) return def;
        return prefix + value + suffix;
    }

    public static String getTemporal(String mode, Timestamp timestamp) {
        return "<t:" + timestamp.getTime() / 1000 + ":" + mode + ">";
    }

    public static String getNullableTemporal(String prefix, String mode, Timestamp timestamp, String def) {
        if (timestamp == null) return def;
        return prefix + getTemporal(mode, timestamp);
    }
}