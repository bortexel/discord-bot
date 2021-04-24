package ru.bortexel.bot.commands.roles;

import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.Roles;

public class TDWorkerRoleCommand extends RoleCommand {

    public TDWorkerRoleCommand(BortexelBot bot) {
        super("tdworker", bot);
    }

    @Override
    protected Role getRole() {
        return this.getBot().getJDA().getRoleById(Roles.TAX_DEPARTMENT_WORKER_ROLE);
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.TAX_DEPARTMENT_CHAT, Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.getBot().getAccessLevels().getTaxDepartmentHeadAccessLevel();
    }
}
