package ru.bortexel.bot.commands.main;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;

import java.util.ArrayList;
import java.util.List;

public class MainCommandProvider implements CommandProvider {
    private final BortexelBot bot;

    public MainCommandProvider(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "Основные";
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<Command>() {{
            add(new HelpCommand(bot));
            add(new PhotoCommand(bot));
            add(new PollCommand(bot));
            add(new PingCommand());
            add(new SkinCommand(bot));
        }};
    }
}
