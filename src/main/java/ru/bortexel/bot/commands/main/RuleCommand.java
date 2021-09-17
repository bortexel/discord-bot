package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;
import ru.ruscalworld.bortexel4j.rules.RulePart;
import ru.ruscalworld.bortexel4j.rules.Rules;

public class RuleCommand extends DefaultBotCommand {
    public RuleCommand(BortexelBot bot) {
        super("rule", bot);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        OptionMapping ruleOption = event.getOption("rule");
        assert ruleOption != null;

        Rules.getByName("main").executeAsync(rules -> {
            RulePart.Rule rule = rules.findRule(ruleOption.getAsString());
            if (rule == null) {
                EmbedBuilder error = EmbedUtil.makeError(
                        "Правило не найдено",
                        "Правила `" + ruleOption.getAsString() + "` не существует в своде Общих Правил сервера."
                );
                hook.sendMessageEmbeds(error.build()).queue();
                return;
            }

            MessageEmbed embed = EmbedUtil.makeRuleInfo(rule).setTimestamp(rules.getLastUpdateTime().toInstant()).build();
            hook.sendMessageEmbeds(embed).queue();
        });
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(OptionType.STRING, "rule", "Пункт правил", true);
    }

    @Override
    public String getDescription() {
        return "Отображает подробную информацию о данном пункте правил";
    }
}
