package ru.bortexel.bot.commands.roles;

import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.Roles;

public class JudgeRoleCommand extends RoleCommand {
    public JudgeRoleCommand(BortexelBot bot) {
        super("judge", bot);
    }

    @Override
    protected Role getRole() {
        return this.getBot().getJDA().getRoleById(Roles.JUDGE_ROLE);
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.JUDGES_CHAT, Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.getBot().getAccessLevels().getParliamentAccessLevel();
    }
}
