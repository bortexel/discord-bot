package ru.bortexel.bot.core;

import net.dv8tion.jda.api.entities.Message;

public interface Executable {
    void onCommand(Message message);
}
