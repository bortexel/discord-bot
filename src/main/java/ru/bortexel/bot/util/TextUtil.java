package ru.bortexel.bot.util;

import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;

public class TextUtil {
    public static String removeDoubleSpaces(String string) {
        while (string.contains("  ")) string = string.replace("  ", " ");
        return string;
    }

    public static String getFullCommandUsage(Command command) {
        return BortexelBot.COMMAND_PREFIX + command.getName() + (command.getUsage() == null ? "" : " " + command.getUsage());
    }

    public static String[] getCommandArgs(Message message) {
        String text = TextUtil.removeDoubleSpaces(message.getContentRaw());
        return text.substring(1).split(" ");
    }
}
