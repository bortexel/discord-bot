package ru.bortexel.bot.util.poll;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static ru.bortexel.bot.BortexelBot.handleException;

public class PollReactionListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        try {
            MessageReaction.ReactionEmote reactionEmote = event.getReaction().getReactionEmote();
            // Custom emojis aren't supported
            if (reactionEmote.isEmote()) return;

            Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();
            SelfUser selfUser = event.getJDA().getSelfUser();
            if (message == null) return;
            // Check if message was sent by current (!) bot
            if (!message.getAuthor().getId().equals(selfUser.getId())) return;

            // Attempt to get poll for message
            Poll poll = Poll.getFromMessage(message);
            if (poll == null) return;

            // Check if member reacted twice
            for (MessageReaction reaction : message.getReactions()) {
                MessageReaction.ReactionEmote currentReactionEmote = reaction.getReactionEmote();
                // Custom emojis aren't supported
                if (currentReactionEmote.isEmote()) continue;
                // Check if this reaction was added by user, not by current bot
                if (selfUser.getId().equals(event.getUserId())) continue;
                // Don't delete current reaction
                if (reactionEmote.getEmoji().equals(currentReactionEmote.getEmoji())) continue;
                // Remove reaction that was set by user
                reaction.retrieveUsers().forEach(user -> {
                    if (user.getId().equals(event.getUserId())) reaction.removeReaction(user).queue();
                });
            }

            // Finally, update embed with poll
            poll.rerender(message);
        } catch (Exception e) {
            handleException(e);
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
            handleException(e);
        }
    }
}
