package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import org.jetbrains.annotations.NotNull;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.Command;

public class ButtonClickListener extends BotListener {
    public ButtonClickListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton() == null) return;
        String id = event.getButton().getId();
        if (id == null) return;

        event.deferReply().queue();

        String[] args = id.split(" ");
        Command command = this.getBot().getCommand(args[0]);
        command.onButtonClick(event, args);
    }
}
