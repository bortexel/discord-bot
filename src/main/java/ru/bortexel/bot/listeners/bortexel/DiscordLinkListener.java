package ru.bortexel.bot.listeners.bortexel;

import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.MemberSynchronizer;
import ru.ruscalworld.bortexel4j.listening.events.account.GenericDiscordLinkEvent;

public class DiscordLinkListener extends BotListener {
    public DiscordLinkListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onAccountDiscordLinked(GenericDiscordLinkEvent event) {
        MemberSynchronizer.synchronize(event.getDiscord().getDiscordID());
    }

    @Override
    public void onAccountDiscordUnlinked(GenericDiscordLinkEvent event) {
        MemberSynchronizer.synchronize(event.getDiscord().getDiscordID());
    }
}
