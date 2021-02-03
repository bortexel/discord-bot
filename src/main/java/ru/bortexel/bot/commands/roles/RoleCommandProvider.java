package ru.bortexel.bot.commands.roles;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;
import ru.bortexel.bot.util.Roles;

import java.util.ArrayList;
import java.util.List;

public class RoleCommandProvider implements CommandProvider {
    private final BortexelBot bot;

    public RoleCommandProvider(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "Роли";
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<Command>() {{
            add(new BuilderRoleCommand(bot));
            add(new TDWorkerRoleCommand(bot));
        }};
    }
}
