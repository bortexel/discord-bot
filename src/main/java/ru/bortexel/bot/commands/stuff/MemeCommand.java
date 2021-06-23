package ru.bortexel.bot.commands.stuff;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.math3.random.RandomDataGenerator;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class MemeCommand extends DefaultBotCommand {
    protected MemeCommand(BortexelBot bot) {
        super("art", bot);
        this.addAlias("meme", true);
    }

    @Override
    public void onCommand(Message message) {
        getMeme(response -> message.reply(response).queue());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        getMeme(response -> hook.sendMessageEmbeds(response).queue());
    }

    private void getMeme(Consumer<MessageEmbed> callback) {
        TextChannel channel = this.getBot().getJDA().getTextChannelById(Channels.ART_CHANNEL);
        if (channel == null) return;

        MessageHistory history = channel.getHistoryFromBeginning(1).complete();
        Message firstMessage = history.getRetrievedHistory().get(0);

        List<Message> latest = channel.getHistory().retrievePast(1).complete();
        long latestMessageId = latest.get(latest.size() - 1).getIdLong();

        long id = new RandomDataGenerator().nextLong(firstMessage.getIdLong(), latestMessageId);

        channel.getHistoryAround(id, 100).queue(requestedHistory -> {
            List<Message> messages = requestedHistory.getRetrievedHistory();
            EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
            int depth = 0;

            while (depth < 50) {
                depth++;

                Random random = new Random(System.currentTimeMillis());
                Message randomMessage = messages.get(random.nextInt(messages.size() - 1));

                boolean attachments = false;
                for (Message.Attachment attachment : randomMessage.getAttachments()) {
                    if (attachment.isImage()) {
                        attachments = true;
                        builder.setImage(attachment.getUrl());
                    }
                }

                if (attachments || depth == 50) {
                    User author = randomMessage.getAuthor();
                    builder.setTimestamp(randomMessage.getTimeCreated());
                    builder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
                    builder.setDescription(randomMessage.getContentRaw());

                    callback.accept(builder.build());
                    return;
                }
            }

            EmbedBuilder error = EmbedUtil.makeError("Мем не нашёлся, попробуйте ещё раз :(", null);
            callback.accept(error.build());
        });
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this);
    }

    @Override
    public String getDescription() {
        return "Выводит случайное сообщение из канала #творчество";
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }
}
