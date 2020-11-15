package ru.bortexel.bot.util.rules;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import ru.bortexel.bot.BortexelBot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RuleParser {
    public static void parseToChannel(String input, TextChannel channel, Color color) {
        try {
            List<String> messages = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            String[] lines = input.split(" {2}");

            for (String line : lines) {
                if (line.length() > 0) buffer.append("\n").append(line);
                if (line.length() == 0) {
                    messages.add(buffer.toString());
                    buffer = new StringBuilder();
                }
            }

            messages.add(buffer.toString());

            for (String message : messages) {
                int index = messages.indexOf(message);
                EmbedBuilder builder = new EmbedBuilder();

                String title = message.split("\n")[1];
                if (title.startsWith("#")) {
                    message = message.replace(title + "\n", "");
                    while (title.startsWith("#") || title.startsWith(" ")) {
                        title = title.substring(1);
                    }

                    builder.setTitle(title);
                }

                message = message.replace("&nbsp; ", "   ");
                builder.setDescription(message);
                builder.setColor(color);

                if (index == messages.size() - 2) {
                    String next = messages.get(messages.size() - 1);
                    if (next.contains("Актуально")) {
                        messages.remove(next);
                        builder.setFooter(next.replace("*", ""));
                    }
                }

                channel.sendMessage(builder.build()).queue();
            }
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }
}
