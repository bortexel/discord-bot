package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.jetbrains.annotations.NotNull;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.MemberSynchronizer;

public class GuildJoinListener extends BotListener {
    public GuildJoinListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        MemberSynchronizer.synchronize(member);
    }
}
