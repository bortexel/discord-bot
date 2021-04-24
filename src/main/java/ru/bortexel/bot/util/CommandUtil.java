package ru.bortexel.bot.util;

import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import ru.bortexel.bot.core.Command;

public class CommandUtil {
    public static CommandUpdateAction.CommandData makeSlashCommand(Command command) {
        return new CommandUpdateAction.CommandData(command.getName(), command.getDescription());
    }
}
