package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.InteractionContext;

import java.util.Optional;
import java.util.UUID;

public class ButtonClickListener extends BotListener {
    private static final Logger logger = LoggerFactory.getLogger("ButtonClickListener");

    public ButtonClickListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton() == null) return;
        String id = event.getButton().getId();
        if (id == null) return;

        try {
            UUID uuid = UUID.fromString(id);
            Optional<InteractionContext> context = this.getBot().consumeInteraction(uuid);
            if (context.isEmpty()) throw new Exception("Unknown context: " + id);

            event.deferReply().queue();
            context.get().onButtonClick(event);
        } catch (Exception exception) {
            logger.debug("Failed handling button click {}", id, exception);
            event.deferReply(true).queue(hook -> hook.sendMessage("Это взаимодействие недоступно").queue());
        }
    }
}
