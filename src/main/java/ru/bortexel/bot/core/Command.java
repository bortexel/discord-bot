package ru.bortexel.bot.core;

public interface Command extends Executable {
    String getName();
    String getUsage();
    String getUsageExample();
    String getDescription();
    String[] getAliases();
    AccessLevel getAccessLevel();
    int getMinArgumentCount();
}
