package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.util.*;
import ru.ruscalworld.bortexel4j.exceptions.NotFoundException;
import ru.ruscalworld.bortexel4j.models.profile.Ban;
import ru.ruscalworld.bortexel4j.models.profile.Profile;
import ru.ruscalworld.bortexel4j.models.profile.Warning;
import ru.ruscalworld.bortexel4j.util.BortexelSkins;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProfileCommand extends DefaultBotCommand {
    protected ProfileCommand(BortexelBot bot) {
        super("profile", bot);

        this.addAlias("user");
        this.addAlias("игрок");
        this.addAlias("профиль");
    }

    @Override
    public void onCommand(Message message) {
        String username = TextUtil.getCommandArgs(message)[1];
        getProfile(username, (embed, row) -> {
            MessageAction action = message.getTextChannel().sendMessageEmbeds(embed);
            if (row.isPresent()) action = action.setActionRows(row.get());
            action.queue();
        });
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        OptionMapping usernameOption = event.getOption("username");
        assert usernameOption != null;
        getProfile(usernameOption.getAsString(), (embed, row) -> {
            WebhookMessageAction<Message> action = hook.sendMessageEmbeds(embed);
            if (row.isPresent()) action = action.addActionRows(row.get());
            action.queue();
        });
    }

    private MessageEmbed getProfileBans(String username) {
        Ban.ProfileBans profile = Ban.getProfileBans(username).execute();
        EmbedBuilder builder = EmbedUtil.makeProfileEmbed(profile.getProfile());
        if (profile.getBans().size() == 0) {
            builder.setDescription("У этого игрока нет банов");
            return builder.build();
        }

        int active = 0;
        for (Ban ban : profile.getBans()) {
            if (!ban.isSuspended() && (ban.getExpiresAt() == null || ban.getExpiresAt().after(new Timestamp(System.currentTimeMillis())))) active++;
            builder.addField(
                    ban.getID() + ". Бан за " + ban.getReason() + " " + (ban.isSuspended() ? "(снят)" : ""),
                    "Выдан " + TextUtil.nullable("модератором `", ban.getAdminName(), "` ", "") +
                            TextUtil.getTemporal("R", ban.getCreatedAt()) + " " +
                            TextUtil.getNullableTemporal("до ", "D", ban.getExpiresAt(), "навсегда"),
                    false
            );
        }

        Profile.BanStats stats = profile.getProfile().getBanStats();
        builder.setFooter("Всего " + profile.getBans().size() + " " + TextUtil.getPlural(profile.getBans().size(), "бан", "бана", "банов") +
                ", из них " + active + " " + TextUtil.getPlural(active, "активный", "активных", "активных") +
                " и " + stats.getSuspendedCount() + " " + TextUtil.getPlural(stats.getSuspendedCount(), "снятый", "снятых", "снятых"));
        return builder.build();
    }

    private MessageEmbed getProfileWarnings(String username) {
        Warning.ProfileWarnings profile = Warning.getProfileWarnings(username).execute();
        EmbedBuilder builder = EmbedUtil.makeProfileEmbed(profile.getProfile());
        if (profile.getWarnings().size() == 0) {
            builder.setDescription("У этого игрока нет банов");
            return builder.build();
        }

        for (Warning warning : profile.getWarnings()) builder.addField(
                warning.getID() + ". Предупреждение мощностью " + warning.getPower() + " за " + warning.getReason(),
                "Выдано " + TextUtil.nullable("модератором `", warning.getAdminName(), "` ", "") +
                        TextUtil.getTemporal("R", warning.getCreatedAt()),
                false
        );

        Profile.WarningStats stats = profile.getProfile().getWarningStats();
        builder.setFooter("Всего " + profile.getWarnings().size() + " " +
                TextUtil.getPlural(profile.getWarnings().size(), "предупреждение", "предупреждения", "предупреждений") +
                " мощностью " + stats.getTotalPower());
        return builder.build();
    }

    private void getProfile(String username, BiConsumer<MessageEmbed, Optional<ActionRow>> callback) {
        Profile.getByUserName(username).executeAsync(profile -> {
            EmbedBuilder builder = EmbedUtil.makeProfileEmbed(profile);

            if (profile.getDiscordID() != null)
                builder.addField("Привязанный Discord", "<@" + profile.getDiscordID() + ">", true);

            builder.addField("ID (A/U)", "" + profile.getAccountID() + "/" + profile.getUserID(), true);
            builder.addField("Последний вход", profile.getLastLogin() != null
                    ? "<t:" + profile.getLastLogin().getTime() / 1000 + ":R>"
                    : "Никогда", true);

            List<Component> components = new ArrayList<>();
            if (profile.getBanStats().getCount() > 0) {
                UUID uuid = this.getBot().registerInteraction(CompletableFuture.supplyAsync(() -> {
                    MessageBuilder message = new MessageBuilder(getProfileBans(profile.getUsername()));
                    return message.build();
                }));

                components.add(Button.secondary(uuid.toString(), "Блокировки"));
            }

            if (profile.getWarningStats().getCount() > 0) {
                UUID uuid = this.getBot().registerInteraction(CompletableFuture.supplyAsync(() -> {
                    MessageBuilder message = new MessageBuilder(getProfileWarnings(profile.getUsername()));
                    return message.build();
                }));

                components.add(Button.secondary(uuid.toString(), "Предупреждения"));
            }

            callback.accept(builder.build(), components.size() > 0 ? Optional.of(ActionRow.of(components)) : Optional.empty());
        }, error -> {
            EmbedBuilder builder;
            if (error instanceof NotFoundException) {
                builder = EmbedUtil.makeError("Игрок не найден", "Игрок с указанным никнеймом не существует. " +
                        "Проверьте правильность ввода никнейма и повторите попытку.");
            } else {
                builder = EmbedUtil.makeError("Не удалось получить профиль", "При получении профиля произошла непредвиденная ошибка.");
                BortexelBot.handleException(error);
            }
            callback.accept(builder.build(), Optional.empty());
        });
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(OptionType.STRING, "username", "Никнейм игрока на сервере", true);
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
