package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.bortexel.bot.BortexelBot;

public abstract class BotListener extends ListenerAdapter {
    private final BortexelBot bot;

    protected BotListener(BortexelBot bot) {
        this.bot = bot;
    }

    public BortexelBot getBot() {
        return bot;
    }
}
