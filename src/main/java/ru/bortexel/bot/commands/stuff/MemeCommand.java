package ru.bortexel.bot.commands.stuff;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.math3.random.RandomDataGenerator;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.EmbedUtil;

import java.util.List;
import java.util.Random;

public class MemeCommand extends DefaultBotCommand {
    protected MemeCommand(BortexelBot bot) {
        super("meme", bot);
        this.addAlias("joke");
    }

    @Override
    public void onCommand(Message message) {
        TextChannel channel = this.getBot().getJDA().getTextChannelById(Channels.ART_CHANNEL);
        if (channel == null) return;

        MessageHistory history = channel.getHistoryFromBeginning(1).complete();
        Message firstMessage = history.getRetrievedHistory().get(0);
        long latestMessageId = channel.getLatestMessageIdLong();
        long id = new RandomDataGenerator().nextLong(firstMessage.getIdLong(), latestMessageId);

        channel.getHistoryAround(id, 100).queue(requestedHistory -> {
            List<Message> messages = requestedHistory.getRetrievedHistory();
            EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
            int depth = 0;

            while (depth < 50) {
                depth++;

                Random random = new Random(System.currentTimeMillis());
                Message randomMessage = messages.get(random.nextInt(messages.size() - 1));

                for (Message.Attachment attachment : randomMessage.getAttachments()) {
                    if (attachment.isImage()) {
                        builder.setImage(attachment.getUrl());
                    }
                }

                if (randomMessage.getAttachments().size() > 0 || depth == 50) {
                    User author = randomMessage.getAuthor();
                    builder.setTimestamp(randomMessage.getTimeCreated());
                    builder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
                    builder.setDescription(randomMessage.getContentRaw());

                    message.getChannel().sendMessage(builder.build()).queue();
                    return;
                }
            }

            EmbedBuilder error = EmbedUtil.makeError("Мем не нашёлся, попробуйте ещё раз :(", null);
            message.getChannel().sendMessage(error.build()).queue();
        });
    }

    @Override
    public String getDescription() {
        return "Выводит случайное сообщение из канала <#" + Channels.ART_CHANNEL + ">";
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }
}
