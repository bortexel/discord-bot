package ru.bortexel.bot.core;

import java.util.List;

public interface CommandGroup {
    String getName();
    List<Command> getCommands();
}
