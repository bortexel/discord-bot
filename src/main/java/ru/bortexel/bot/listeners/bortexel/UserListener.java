package ru.bortexel.bot.listeners.bortexel;

import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.Roles;
import ru.bortexel.bot.util.TimeUtil;
import ru.ruscalworld.bortexel4j.listening.events.user.GenericUserEvent;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.user.User;

public class UserListener extends BotListener {
    public UserListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onUserActivityUpdated(GenericUserEvent event) {
        User user = event.getUser();
        Role activePlayer = this.getBot().getJDA().getRoleById(Roles.ACTIVE_PLAYER_ROLE);
        if (activePlayer == null) return;

        event.getUser().getAccount(this.getBot().getApiClient()).executeAsync(userAccount -> {
            Account account = userAccount.getAccount();
            if (user.getActiveTill() == null || user.getActiveTill().before(TimeUtil.now())) {
                Roles.activePlayer().revoke(account.getDiscordID());
            } else Roles.activePlayer().grant(account.getDiscordID());
        });
    }
}
