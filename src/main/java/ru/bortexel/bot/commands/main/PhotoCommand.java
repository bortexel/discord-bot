package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.core.Callback;
import ru.ruscalworld.bortexel4j.exceptions.NotFoundException;
import ru.ruscalworld.bortexel4j.models.photo.Photo;
import ru.ruscalworld.bortexel4j.models.season.Season;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PhotoCommand extends DefaultBotCommand {
    protected PhotoCommand(BortexelBot bot) {
        super("photo", bot);

        this.addAlias("screenshot");
        this.addAlias("фото");
        this.addAlias("скриншот");
        this.addAlias("скрин");
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        int seasonId = 0;

        if (args.length >= 2) try {
            seasonId = Integer.parseInt(args[1]);
        } catch (Exception ignored) { }

        getPhotos(seasonId, response -> message.reply(response).queue());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, CommandHook hook) {
        SlashCommandEvent.OptionData seasonOption = event.getOption("season");
        int seasonID = 0;
        if (seasonOption != null) seasonID = (int) seasonOption.getAsLong();
        getPhotos(seasonID, response -> hook.sendMessage(response).queue());
    }

    private void getPhotos(int seasonID, Callback<MessageEmbed> callback) {
        if (seasonID == 0) {
            Photo.getAll(this.getBot().getApiClient()).executeAsync(photos -> handlePhotos(photos, callback));
        } else try {
            Season season = Season.getByID(seasonID, this.getBot().getApiClient()).execute();
            if (season == null) handlePhotos(Collections.emptyList(), callback);
            if (season != null) season.getPhotos(this.getBot().getApiClient()).executeAsync(seasonPhotos -> handlePhotos(seasonPhotos.getPhotos(), callback));
        } catch (NotFoundException ignored) {
            MessageEmbed embed = EmbedUtil.makeError("Не удалось получить скриншоты", null).build();
            callback.handle(embed);
        }
    }

    private void handlePhotos(List<Photo> photos, Callback<MessageEmbed> callback) {
        try {
            if (photos == null || photos.size() == 0) {
                EmbedBuilder builder = EmbedUtil.makeError("Неизвестная ошибка", "Не удалось получить скриншоты с сайта");
                callback.handle(builder.build());
                return;
            }

            Random random = new Random();
            int index = random.nextInt(photos.size());
            Photo photo = photos.get(index);

            EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
            builder.setImage(photo.getURL());
            builder.addField("Сезон", "" + photo.getSeason(), true);
            if (photo.getDescription() != null) builder.addField("Описание", photo.getDescription(), true);
            if (photo.getAuthorName() != null) builder.addField("Автор", photo.getAuthorName(), true);

            callback.handle(builder.build());
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public CommandUpdateAction.CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.INTEGER, "season", "Номер сезона"));
    }

    @Override
    public String getUsage() {
        return "[сезон]";
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
