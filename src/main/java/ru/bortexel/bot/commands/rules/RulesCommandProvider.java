package ru.bortexel.bot.commands.rules;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;

import java.util.ArrayList;
import java.util.List;

public class RulesCommandProvider implements CommandProvider {
    private final BortexelBot bot;

    public RulesCommandProvider(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "Правила";
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<Command>() {{
            add(new UpdateRulesCommand(bot));
        }};
    }
}
