package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandGroup;

import java.util.ArrayList;
import java.util.List;

public class GuildListener extends BotListener {
    private static final Logger logger = LoggerFactory.getLogger("GuildListener");

    public GuildListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        if (!guild.getId().equals(this.getBot().getMainGuildID())) return;
        List<CommandData> slashCommands = new ArrayList<>();

        if (this.getBot().shouldRegisterCommands()) {
            logger.info("Registering commands for guild \"{}\"", guild.getName());

            for (CommandGroup commandGroup : this.getBot().getCommandGroups()) {
                for (Command command : commandGroup.getCommands()) {
                    if (command.getSlashCommandData() == null || command.isGlobal()) continue;
                    slashCommands.add(command.getSlashCommandData());

                    for (String alias : command.getSlashAliases()) {
                        slashCommands.add(command.getSlashCommandData().setName(alias));
                    }
                }
            }

            CommandListUpdateAction commands = guild.updateCommands();
            commands.addCommands(slashCommands).queue();
        } else logger.info("Slash commands were disabled with DISABLE_SLASH_COMMANDS variable, skipping setup for guild \"{}\"", guild.getName());
    }
}
