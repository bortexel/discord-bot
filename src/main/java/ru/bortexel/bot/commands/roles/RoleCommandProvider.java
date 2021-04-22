package ru.bortexel.bot.commands.roles;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;
import ru.bortexel.bot.core.DefaultCommandProvider;
import ru.bortexel.bot.util.Roles;

import java.util.ArrayList;
import java.util.List;

public class RoleCommandProvider extends DefaultCommandProvider {
    public RoleCommandProvider(BortexelBot bot) {
        super(bot, "Роли");
        this.registerCommand(new BuilderRoleCommand(this.getBot()));
        this.registerCommand(new TDWorkerRoleCommand(this.getBot()));
        this.registerCommand(new JudgeRoleCommand(this.getBot()));
    }
}
