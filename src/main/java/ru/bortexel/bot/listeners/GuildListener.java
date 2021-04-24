package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;

import java.util.ArrayList;
import java.util.List;

public class GuildListener extends BotListener {
    public GuildListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        if (!guild.getId().equals(this.getBot().getMainGuildID())) return;
        List<CommandUpdateAction.CommandData> slashCommands = new ArrayList<>();

        if (this.getBot().isShouldRegisterCommands()) for (CommandProvider commandProvider : this.getBot().getCommandProviders()) {
            for (Command command : commandProvider.getCommands()) {
                if (command.getSlashCommandData() == null || command.isGlobal()) continue;
                slashCommands.add(command.getSlashCommandData());
            }
        }

        LoggerFactory.getLogger(this.getClass()).info("Registering commands for guild \"" + guild.getName() + "\"");
        CommandUpdateAction commands = guild.updateCommands();
        commands.addCommands(slashCommands).queue();
    }
}
