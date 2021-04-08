package ru.bortexel.bot.listeners.bortexel;

import ru.bortexel.bot.BortexelBot;
import ru.ruscalworld.bortexel4j.listening.events.EventListener;

public abstract class BotListener extends EventListener {
    private final BortexelBot bot;

    protected BotListener(BortexelBot bot) {
        this.bot = bot;
    }

    public BortexelBot getBot() {
        return bot;
    }
}