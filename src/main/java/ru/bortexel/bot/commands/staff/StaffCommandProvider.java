package ru.bortexel.bot.commands.staff;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;

import java.util.ArrayList;
import java.util.List;

public class StaffCommandProvider implements CommandProvider {
    private final BortexelBot bot;

    public StaffCommandProvider(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "Модерация";
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<>() {{
            add(new BanCommand(bot));
        }};
    }
}
