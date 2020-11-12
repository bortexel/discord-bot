package ru.bortexel.bot.core;

import java.util.List;

public interface CommandProvider {
    String getName();
    List<Command> getCommands();
}
