package ru.bortexel.bot.commands.info;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandGroup;

public class InfoCommandGroup extends DefaultCommandGroup {
    public InfoCommandGroup(BortexelBot bot) {
        super(bot, "Информация");
        this.registerCommand(new UpdateRulesCommand(this.getBot()));
        this.registerCommand(new RoleInfoCommand(this.getBot()));
    }
}
