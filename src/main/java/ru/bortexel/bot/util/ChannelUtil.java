package ru.bortexel.bot.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class ChannelUtil {
    public static void clearMessages(TextChannel textChannel, int amount) {
        List<Message> messages = textChannel.getHistory().retrievePast(amount).complete();
        messages.forEach(message -> message.delete().queue());
    }
}
