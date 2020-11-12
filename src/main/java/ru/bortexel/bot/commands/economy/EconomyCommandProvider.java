package ru.bortexel.bot.commands.economy;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommandProvider implements CommandProvider {
    private final BortexelBot bot;

    public EconomyCommandProvider(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "Экономика";
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<Command>() {{
            add(new GetPriceCommand(bot));
        }};
    }
}
