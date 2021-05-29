package ru.bortexel.bot.commands.roles;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.DefaultCommandGroup;

public class RoleCommandGroup extends DefaultCommandGroup {
    public RoleCommandGroup(BortexelBot bot) {
        super(bot, "Роли");
        this.registerCommand(new BuilderRoleCommand(this.getBot()));
        this.registerCommand(new TDWorkerRoleCommand(this.getBot()));
        this.registerCommand(new JudgeRoleCommand(this.getBot()));
    }
}
