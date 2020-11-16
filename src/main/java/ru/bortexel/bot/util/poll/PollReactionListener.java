package ru.bortexel.bot.util.poll;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.bortexel.bot.BortexelBot;

public class PollReactionListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        try {
            if (event.getReaction().getReactionEmote().isEmote()) return;
            Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();
            if (message == null) return;
            Poll poll = Poll.getFromMessage(message);
            if (poll != null) poll.rerender(message);

            for (MessageReaction reaction : message.getReactions()) {
                if (!reaction.getReactionEmote().isEmoji()) continue;
                if (event.getJDA().getSelfUser().getId().equals(event.getUserId())) continue;
                if (event.getReaction().getReactionEmote().getEmoji().equals(reaction.getReactionEmote().getEmoji()))
                    continue;
                reaction.retrieveUsers().forEach(user -> {
                    if (user.getId().equals(event.getUserId())) reaction.removeReaction(user).queue();
                });
            }
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        try {
            if (event.getReaction().getReactionEmote().isEmote()) return;
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            if (message == null) return;
            Poll poll = Poll.getFromMessage(message);
            if (poll != null) poll.rerender(message);
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }
}
