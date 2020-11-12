package ru.bortexel.bot.commands.stuff;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;

import java.util.ArrayList;
import java.util.List;

public class StuffCommandProvider implements CommandProvider {
    private final BortexelBot bot;

    public StuffCommandProvider(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "Разное";
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<Command>() {{
            add(new CowsayCommand(bot));
        }};
    }
}