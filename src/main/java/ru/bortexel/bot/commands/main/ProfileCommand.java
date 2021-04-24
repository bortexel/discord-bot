package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.util.*;
import ru.ruscalworld.bortexel4j.core.Callback;
import ru.ruscalworld.bortexel4j.exceptions.NotFoundException;
import ru.ruscalworld.bortexel4j.models.profile.Profile;
import ru.ruscalworld.bortexel4j.util.BortexelSkins;

public class ProfileCommand extends DefaultBotCommand {
    protected ProfileCommand(BortexelBot bot) {
        super("profile", bot);

        this.addAlias("user");
        this.addAlias("игрок");
        this.addAlias("profile");
        this.addAlias("профиль");
    }

    @Override
    public void onCommand(Message message) {
        String username = TextUtil.getCommandArgs(message)[1];
        getProfile(username, response -> message.getTextChannel().sendMessage(response).queue());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, CommandHook hook) {
        SlashCommandEvent.OptionData usernameOption = event.getOption("username");
        assert usernameOption != null;
        getProfile(usernameOption.getAsString(), response -> hook.sendMessage(response).queue());
    }

    private void getProfile(String username, Callback<MessageEmbed> callback) {
        Profile.getByUserName(username).executeAsync(profile -> {
            EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
            builder.setAuthor(profile.getUsername(), null, BortexelSkins.getAvatarURL(profile.getUsername(), true));
            builder.setThumbnail(BortexelSkins.getBodyRenderURL(profile.getUsername(), true));
            builder.addField("Последний вход", profile.getLastLogin() != null
                    ? TimeUtil.getDefaultDateFormat().format(profile.getLastLogin())
                    : "Никогда", true);
            builder.addField("ID (A/U)", "" + profile.getAccountID() + "/" + profile.getUserID(), true);
            callback.handle(builder.build());
        }, error -> {
            EmbedBuilder builder;
            if (error instanceof NotFoundException) {
                builder = EmbedUtil.makeError("Игрок не найден", "Игрок с указанным никнеймом не существует. " +
                        "Проверьте правильность ввода никнейма и повторите попытку.");
            } else {
                builder = EmbedUtil.makeError("Не удалось получить профиль", "При получении профиля произошла непредвиденная ошибка.");
                BortexelBot.handleException(error);
            }
            callback.handle(builder.build());
        });
    }

    @Override
    public CommandUpdateAction.CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING, "username", "Никнейм игрока на сервере")
                        .setRequired(true));
    }

    @Override
    public String getUsage() {
        return "<никнейм>";
    }

    @Override
    public String getUsageExample() {
        return "`$profile RuscalWorld` выведет информацию об игроке RuscalWorld";
    }

    @Override
    public String getDescription() {
        return "Выводит общую информацию об игроке с указанным никнеймом";
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }
}
