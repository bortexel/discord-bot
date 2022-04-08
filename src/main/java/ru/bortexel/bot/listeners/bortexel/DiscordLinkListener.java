package ru.bortexel.bot.listeners.bortexel;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.RoleChecker;
import ru.ruscalworld.bortexel4j.listening.events.account.GenericDiscordLinkEvent;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.util.List;

public class DiscordLinkListener extends BotListener {
    public DiscordLinkListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onAccountDiscordLinked(GenericDiscordLinkEvent event) {
        RoleChecker.updateRoles(event.getDiscord().getDiscordID());

        event.getAccount().getUsers(this.getBot().getApiClient()).executeAsync(accountUsers -> {
            List<User> users = accountUsers.getUsers();
            if (users.size() == 0) return;

            this.getBot().getMainGuild().retrieveMemberById(event.getDiscord().getDiscordID()).queue(member -> {
                if (member == null) return;
                member.modifyNickname(users.get(0).getUsername()).queue();
            });
        });
    }

    @Override
    public void onAccountDiscordUnlinked(GenericDiscordLinkEvent event) {
        RoleChecker.updateRoles(event.getDiscord().getDiscordID());
    }
}
