package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.photo.Photo;
import ru.ruscalworld.bortexel4j.models.season.Season;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PhotoCommand implements Command {
    private final BortexelBot bot;

    public PhotoCommand(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        int seasonId = 0;

        if (args.length >= 2) {
            try {
                seasonId = Integer.parseInt(args[1]);
            } catch (Exception ignored) { }
        }

        if (seasonId == 0) {
            Photo.getAll(bot.getApiClient()).executeAsync(photos -> handlePhotos(photos, message.getChannel()));
        } else Season.getByID(seasonId, bot.getApiClient()).executeAsync(season -> {
            if (season == null) handlePhotos(Collections.emptyList(), message.getChannel());
            if (season != null) season.getPhotos(bot.getApiClient()).executeAsync(seasonPhotos -> handlePhotos(seasonPhotos.getPhotos(), message.getChannel()));
        });
    }

    private void handlePhotos(List<Photo> photos, MessageChannel channel) {
        try {
            if (photos == null || photos.size() == 0) {
                EmbedBuilder builder = EmbedUtil.makeError("Неизвестная ошибка", "Не удалось получить скриншоты с сайта");
                channel.sendMessage(builder.build()).queue();
                return;
            }

            Random random = new Random();
            int index = random.nextInt(photos.size());
            Photo photo = photos.get(index);

            EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
            builder.setImage(photo.getUrl());
            builder.addField("Сезон", "" + photo.getSeason(), true);
            if (photo.getDescription() != null) builder.addField("Описание", photo.getDescription(), true);
            if (photo.getAuthor() != null) builder.addField("Автор", photo.getAuthor(), true);

            channel.sendMessage(builder.build()).queue();
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public String getName() {
        return "photo";
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getUsageExample() {
        return "`$photo` покажет случайный скриншот с сайта\n" +
                "`$photo 4` покажет случайный скриншот с четвёртого сезона";
    }

    @Override
    public String getDescription() {
        return "Отображает случайный скриншот с указанного сезона";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "screenshot", "фото", "скриншот", "скрин" };
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return null;
    }

    @Override
    public int getMinArgumentCount() {
        return 0;
    }
}
