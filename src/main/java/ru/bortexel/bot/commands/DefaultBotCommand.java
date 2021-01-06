package ru.bortexel.bot.commands;

import ru.bortexel.bot.BortexelBot;

public abstract class DefaultBotCommand extends DefaultCommand {
    private final BortexelBot bot;

    protected DefaultBotCommand(String name, BortexelBot bot) {
        super(name);
        this.bot = bot;
    }

    public BortexelBot getBot() {
        return bot;
    }
}
