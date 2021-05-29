package ru.bortexel.bot.commands.stuff;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandGroup;

public class StuffCommandGroup extends DefaultCommandGroup {
    public StuffCommandGroup(BortexelBot bot) {
        super(bot, "Разное");
        this.registerCommand(new CowsayCommand(this.getBot()));
        this.registerCommand(new MemeCommand(this.getBot()));
    }
}