package ru.bortexel.bot.commands.economy;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandProvider;

public class EconomyCommandProvider extends DefaultCommandProvider {
    public EconomyCommandProvider(BortexelBot bot) {
        super(bot, "Экономика");
        this.registerCommand(new GetPriceCommand(this.getBot()));
    }
}
