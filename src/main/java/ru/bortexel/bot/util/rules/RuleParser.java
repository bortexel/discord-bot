package ru.bortexel.bot.util.rules;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import ru.bortexel.bot.BortexelBot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RuleParser {
    public static void parseToChannel(String input, TextChannel channel, Color color) {
        try {
            Rules rules = new Gson().fromJson(input, Rules.class);

            for (RulePart part : rules.getParts()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(color);
                builder.setTitle(part.getNumber() != 0 ? "" + part.getNumber() + ". " + part.getName() : part.getName());

                List<String> renderedRules = new ArrayList<>();
                if (part.getDescription() != null) renderedRules.add(part.getDescription());
                if (part.getRules() != null) for (RulePart.Rule rule : part.getRules()) renderedRules.add(rule.render(0));

                StringBuilder description = new StringBuilder();
                for (String renderedRule : renderedRules) {
                    if (description.length() + renderedRule.length() > 2000) {
                        builder.setDescription(description);
                        channel.sendMessage(builder.build()).complete();

                        builder = new EmbedBuilder();
                        builder.setColor(color);

                        description = new StringBuilder();
                    }

                    description.append(renderedRule).append("\n");
                }

                builder.setDescription(description);

                if (rules.getParts().get(rules.getParts().size() - 1).getName().equals(part.getName())) {
                    builder.setTimestamp(rules.getLastUpdateTime().toInstant());
                }

                channel.sendMessage(builder.build()).complete();
            }
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }
}
