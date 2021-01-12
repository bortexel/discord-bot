package ru.bortexel.bot.commands.roles;

import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.Roles;

public class BuilderRoleCommand extends RoleCommand {
    private final BortexelBot bot;

    public BuilderRoleCommand(BortexelBot bot) {
        super("builder");
        this.bot = bot;
    }

    @Override
    protected Role getRole() {
        return this.bot.getJDA().getRoleById(Roles.BUILDER_ROLE);
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BUILDERS_CHAT, Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.bot.getAccessLevels().getHeadBuilderAccessLevel();
    }
}
