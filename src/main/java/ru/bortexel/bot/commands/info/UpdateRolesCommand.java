package ru.bortexel.bot.commands.info;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.models.BotRole;
import ru.bortexel.bot.util.ChannelUtil;
import ru.bortexel.bot.util.Channels;

import java.util.List;

public class UpdateRolesCommand extends DefaultBotCommand {
    protected UpdateRolesCommand(BortexelBot bot) {
        super("updateroles", bot);
    }

    @Override
    public void onCommand(Message message) {
        ChannelUtil.clearMessages(message.getTextChannel(), 50);

        for (Role role : message.getGuild().getRoles()) {
            BotRole botRole = BotRole.getByDiscordRole(role, this.getBot());
            if (botRole == null) continue;

            MessageEmbed embed = botRole.getInfoEmbed().build();
            message.getChannel().sendMessage(embed).queue();
        }
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.ROLES_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.getBot().getAccessLevels().getAdministratorAccessLevel();
    }
}
