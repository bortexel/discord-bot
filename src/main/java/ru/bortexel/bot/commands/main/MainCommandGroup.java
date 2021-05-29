package ru.bortexel.bot.commands.main;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandGroup;

public class MainCommandGroup extends DefaultCommandGroup {
    public MainCommandGroup(BortexelBot bot) {
        super(bot, "Основные");
        this.registerCommand(new HelpCommand(this.getBot()));
        this.registerCommand(new ProfileCommand(this.getBot()));
        this.registerCommand(new PhotoCommand(this.getBot()));
        this.registerCommand(new PollCommand(this.getBot()));
        this.registerCommand(new PingCommand());
    }
}
