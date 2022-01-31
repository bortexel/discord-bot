package ru.bortexel.bot.listeners.bortexel;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.Roles;
import ru.bortexel.bot.util.TimeUtil;
import ru.ruscalworld.bortexel4j.listening.events.ban.GenericBanEvent;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.ban.Ban;

import java.awt.*;

public class BanListener extends BotListener {
    public BanListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onBanCreated(GenericBanEvent event) {
        try {
            Ban ban = event.getBan();
            getLogger().info("Received info about ban #" + ban.getID());

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.decode("#AA0000"));

            String time = "навсегда";
            if (!ban.isPermanent() && ban.getExpiresAt() != null) {
                long delay = ban.getExpiresAt().getTime() - ban.getCreatedAt().getTime();
                time = "на **" + TimeUtil.formatLength(delay / 1000 + 1) + "**";
            }

            builder.setTitle("**" + ban.getUsername().replace("_", "\\_") + "** был забанен " + time);
            builder.setFooter("Параметры могут быть изменены со временем. В таком случае администратор обязан оставить сообщение ниже.");

            builder.addField("Идентификатор", "" + ban.getID(), true);
            builder.addField("Причина", ban.getReason(), true);

            if (!ban.isPermanent() && ban.getExpiresAt() != null) {
                builder.addField("Истекает", "<t:" + ban.getExpiresAt().getTime() / 1000 + ":F>", true);
            }

            JDA jda = this.getBot().getJDA();
            TextChannel channel = jda.getTextChannelById(Channels.PUNISHMENTS_CHANNEL);
            if (ban.getAdminID() != 0) {
                Account.getByID(ban.getAdminID(), this.getBot().getApiClient())
                        .executeAsync(account -> jda.retrieveUserById(account.getDiscordID()).queue(user -> {
                            builder.setAuthor(ban.getAdminName(), null, user.getAvatarUrl());
                            if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
                        }));
            } else if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();

            Account.getByID(ban.getAccountID(), this.getBot().getApiClient())
                    .executeAsync(account -> jda.retrieveUserById(account.getDiscordID()).queue(user -> Roles
                            .bannedPlayer().grant(account.getDiscordID())));
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(BanListener.class);
    }
}
