package ru.bortexel.bot.commands.main;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandProvider;

public class MainCommandProvider extends DefaultCommandProvider {
    public MainCommandProvider(BortexelBot bot) {
        super(bot, "Основные");
        this.registerCommand(new HelpCommand(this.getBot()));
        this.registerCommand(new ProfileCommand(this.getBot()));
        this.registerCommand(new PhotoCommand(this.getBot()));
        this.registerCommand(new PollCommand(this.getBot()));
        this.registerCommand(new PingCommand());
    }
}
