package ru.bortexel.bot.util;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.bortexel.bot.core.Command;

public class CommandUtil {
    public static CommandData makeSlashCommand(Command command) {
        return new CommandData(command.getName(), command.getDescription());
    }
}
