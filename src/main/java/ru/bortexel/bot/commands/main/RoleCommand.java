package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.models.BotRole;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;

public class RoleCommand extends DefaultBotCommand {
    public RoleCommand(BortexelBot bot) {
        super("role", bot);
        this.setLegacySupported(false);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        OptionMapping roleOption = event.getOption("role");
        assert roleOption != null;
        Role role = roleOption.getAsRole();
        BotRole botRole = BotRole.getByDiscordRole(role, this.getBot());

        if (botRole == null) {
            EmbedBuilder error = EmbedUtil.makeError("Роль не найдена", "Нам не удалось найти подробную информацию об этой роли.");
            hook.sendMessageEmbeds(error.build()).queue();
            return;
        }

        hook.sendMessageEmbeds(EmbedUtil.makeShortRoleInfo(botRole).build()).queue();
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(OptionType.ROLE, "role", "Роль, информацию о которой нужно показать", true);
    }

    @Override
    public String getDescription() {
        return "Показывает информацию об указанной роли";
    }
}
