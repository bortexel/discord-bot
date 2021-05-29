package ru.bortexel.bot.core;

import ru.bortexel.bot.BortexelBot;

import java.util.ArrayList;
import java.util.List;

public class DefaultCommandGroup implements CommandGroup {
    private final BortexelBot bot;
    private final String name;
    private final List<Command> commands = new ArrayList<>();

    public DefaultCommandGroup(BortexelBot bot, String name) {
        this.bot = bot;
        this.name = name;
    }

    public void registerCommand(Command command) {
        this.commands.add(command);
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final List<Command> getCommands() {
        return this.commands;
    }

    public BortexelBot getBot() {
        return bot;
    }
}
