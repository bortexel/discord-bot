package ru.bortexel.bot.util.poll;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class Poll {
    private final String title;
    private final List<PollVariant> variants;

    public Poll(String title, List<PollVariant> variants) {
        this.title = title;
        this.variants = variants;
    }

    public static boolean isPoll(Message message) {
        if (message.getReactions().size() == 0) return false;
        if (message.getEmbeds().size() != 1) return false;
        MessageEmbed embed = message.getEmbeds().get(0);
        if (embed.getTitle() == null) return false;
        return embed.getFields().size() != 0;
    }

    public static Poll getFromMessage(Message message) {
        if (!isPoll(message)) return null;
        List<PollVariant> variants = new ArrayList<>();

        MessageEmbed embed = message.getEmbeds().get(0);
        List<MessageReaction> reactions = message.getReactions();

        for (MessageReaction reaction : reactions) {
            if (reaction.getReactionEmote().isEmote()) continue;
            String emoji = reaction.getReactionEmote().getEmoji();
            MessageEmbed.Field field = embed.getFields().get(reactions.indexOf(reaction));
            int votes = reaction.getCount() - 1;
            String name = field.getName() == null ? "" : field.getName().split(" • ")[1];
            variants.add(new PollVariant(name, emoji, votes));
        }

        String title = embed.getTitle();
        return new Poll(title, variants);
    }

    public static Poll create(TextChannel channel, String title, List<PollVariant> variants) {
        Poll poll = new Poll(title, variants);
        EmbedBuilder builder = poll.makePollEmbed();
        Message message = channel.sendMessage(builder.build()).complete();
        for (PollVariant variant : variants) message.addReaction(variant.getEmoji()).complete();
        return poll;
    }

    public void rerender(Message message) {
        message.editMessage(this.makePollEmbed().build()).queue();
    }

    private EmbedBuilder makePollEmbed() {
        int totalVotes = this.getTotalVotes();
        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setTitle(this.title);

        for (PollVariant variant : this.variants) {
            float percentage = totalVotes > 0 ? (float) Math.round((float) variant.getVotes() / (float) totalVotes * 1000) / 10 : 0;
            String name = variant.getEmoji() + " • " + variant.getName();
            String value = TextUtil.makeDefaultProgressBar(Math.round(percentage), 15) + " • " +
                    variant.getVotes() + " (" + percentage + "%)";
            builder.addField(name, value, false);
        }

        return builder;
    }

    public int getTotalVotes() {
        int total = 0;
        for (PollVariant variant : this.variants) total += variant.getVotes();
        return total;
    }
}
