package ru.bortexel.bot.tasks;

import ru.bortexel.bot.BortexelBot;

import java.util.TimerTask;

public class InteractionCleanup extends TimerTask {
    @Override
    public void run() {
        BortexelBot bot = BortexelBot.getInstance();
        bot.getInteractions().forEach((uuid, context) -> {
            if (context.isActual()) return;
            bot.getInteractions().remove(uuid);
        });
    }
}
