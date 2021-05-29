package ru.bortexel.bot.commands.economy;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandGroup;

public class EconomyCommandGroup extends DefaultCommandGroup {
    public EconomyCommandGroup(BortexelBot bot) {
        super(bot, "Экономика");
        this.registerCommand(new GetPriceCommand(this.getBot()));
    }
}
