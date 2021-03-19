package ru.bortexel.bot.commands.info;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;

import java.util.ArrayList;
import java.util.List;

public class InfoCommandProvider implements CommandProvider {
    private final BortexelBot bot;

    public InfoCommandProvider(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "Информация";
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<Command>() {{
            add(new UpdateRulesCommand(bot));
            add(new RoleInfoCommand(bot));
        }};
    }
}
