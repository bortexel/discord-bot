package ru.bortexel.bot.commands.info;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandProvider;

public class InfoCommandProvider extends DefaultCommandProvider {
    public InfoCommandProvider(BortexelBot bot) {
        super(bot, "Информация");
        this.registerCommand(new UpdateRulesCommand(this.getBot()));
        this.registerCommand(new RoleInfoCommand(this.getBot()));
    }
}
