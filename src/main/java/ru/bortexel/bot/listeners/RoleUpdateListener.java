package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.*;
import org.jetbrains.annotations.NotNull;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.models.BotRole;

import java.util.List;

public class RoleUpdateListener extends BotListener {
    public RoleUpdateListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getMember() == null) return;
        handle(event.getMember().getRoles());
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        handle(event.getMember().getRoles());
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        handle(event.getRoles());
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        handle(event.getRoles());
    }

    private void handle(List<Role> roles) {
        for (Role role : roles) {
            BotRole botRole = BotRole.getByDiscordRole(role, this.getBot());
            if (botRole == null) continue;

            Message infoMessage = botRole.getInfoMessage();
            if (infoMessage == null) continue;

            infoMessage.editMessage(botRole.getInfoEmbed().build()).queue();
        }
    }
}
