package ru.bortexel.bot.commands.roles;

import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.Roles;

public class BuilderRoleCommand extends RoleCommand {
    public BuilderRoleCommand(BortexelBot bot) {
        super("builder", bot);
    }

    @Override
    protected Role getRole() {
        return Roles.builder(this.getBot()).getRole();
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BUILDERS_CHAT, Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.getBot().getAccessLevels().getHeadBuilderAccessLevel();
    }
}
