package ru.bortexel.bot.util.poll;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class Poll {
    protected static final String MULTIPLE_CHOICE_ALLOWED_STRING = "Множественный выбор разрешён";

    private final String title;
    private final List<PollVariant> variants;
    private boolean multipleChoice;

    public Poll(String title, List<PollVariant> variants) {
        this.title = title;
        this.variants = variants;
    }

    public static boolean isPoll(Message message) {
        if (message.getReactions().size() == 0) return false;
        if (message.getEmbeds().size() != 1) return false;
        MessageEmbed embed = message.getEmbeds().get(0);
        if (embed.getTitle() == null) return false;
        if (embed.getColor() == null) return false;
        if (!embed.getColor().equals(BortexelBot.EMBED_COLOR)) return false;
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
            if (reactions.indexOf(reaction) >= embed.getFields().size()) continue;
            MessageEmbed.Field field = embed.getFields().get(reactions.indexOf(reaction));
            int votes = reaction.getCount() - 1;
            if (field.getName() == null || !field.getName().contains(" • ")) return null;
            String name = field.getName().split(" • ")[1];
            variants.add(new PollVariant(name, emoji, votes));
        }

        String title = embed.getTitle();
        Poll poll = new Poll(title, variants);

        MessageEmbed.Footer footer = embed.getFooter();
        if (footer != null && footer.getText() != null && footer.getText().contains(MULTIPLE_CHOICE_ALLOWED_STRING))
            poll.setMultipleChoiceAllowed(true);

        return poll;
    }

    public static Poll create(String title, List<PollVariant> variants) {
        return new Poll(title, variants);
    }

    public void send(TextChannel channel) {
        EmbedBuilder builder = this.makePollEmbed();
        Message message = channel.sendMessage(builder.build()).complete();
        for (PollVariant variant : variants) message.addReaction(variant.getEmoji()).complete();
    }

    public void rerender(Message message) {
        EmbedBuilder builder = this.makePollEmbed();
        int memberCount = this.getMemberCount(message);
        String plural = TextUtil.getPlural(memberCount, " человек принял ", " человека приняли ", " человек приняли ");
        builder.setFooter(memberCount + plural + "участие" + (this.isMultipleChoiceAllowed() ? " • " + MULTIPLE_CHOICE_ALLOWED_STRING : ""));
        message.editMessage(builder.build()).queue();
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
            builder.setFooter(this.isMultipleChoiceAllowed() ? MULTIPLE_CHOICE_ALLOWED_STRING : "");
        }

        return builder;
    }

    public int getTotalVotes() {
        int total = 0;
        for (PollVariant variant : this.variants) total += variant.getVotes();
        return total;
    }

    public int getMemberCount(Message message) {
        List<String> members = new ArrayList<>();
        int total = 0;

        for (PollVariant variant : this.variants) {
            List<User> currentMembers = variant.getReaction(message).retrieveUsers().complete();
            for (User currentMember : currentMembers) {
                if (members.contains(currentMember.getId())) continue;
                if (currentMember.isBot()) continue;
                members.add(currentMember.getId());
                total++;
            }
        }

        return total;
    }

    public boolean isMultipleChoiceAllowed() {
        return this.multipleChoice;
    }

    public void setMultipleChoiceAllowed(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }
}
