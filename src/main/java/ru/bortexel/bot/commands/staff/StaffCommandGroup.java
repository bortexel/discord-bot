package ru.bortexel.bot.commands.staff;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandGroup;

public class StaffCommandGroup extends DefaultCommandGroup {
    public StaffCommandGroup(BortexelBot bot) {
        super(bot, "Модерация");
        this.registerCommand(new BanCommand(this.getBot()));
        this.registerCommand(new WarningCommand(this.getBot()));
    }
}
