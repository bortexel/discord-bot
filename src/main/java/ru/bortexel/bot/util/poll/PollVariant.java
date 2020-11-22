package ru.bortexel.bot.util.poll;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

public class PollVariant {
    private final String name;
    private final String emoji;
    private final int votes;

    public PollVariant(String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
        this.votes = 0;
    }

    public PollVariant(String name, String emoji, int votes) {
        this.name = name;
        this.emoji = emoji;
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getVotes() {
        return votes;
    }

    public MessageReaction getReaction(Message message) {
        if (!Poll.isPoll(message)) return null;
        for (MessageReaction reaction : message.getReactions())
            if (reaction.getReactionEmote().getEmoji().equals(this.emoji)) return reaction;
        return null;
    }
}
