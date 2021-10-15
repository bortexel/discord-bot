package ru.bortexel.bot.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.models.BotRole;
import ru.bortexel.bot.util.AccessLevels;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

public class RoleInfoCommand extends DefaultBotCommand {
    protected RoleInfoCommand(BortexelBot bot) {
        super("roleinfo", bot);
    }

    @Override
    public void onCommand(Message message) {
        BotRole botRole = null;
        TextChannel channel = message.getTextChannel();

        try {
            String[] args = TextUtil.getCommandArgs(message);
            int id = Integer.parseInt(args[1]);
            botRole = BotRole.getByID(id, this.getBot());
        } catch (Exception ignored) { }

        if (botRole == null) {
            EmbedBuilder builder = EmbedUtil.makeError("Роль не найдена", "Роль с указанным идентификатором не существует.");
            channel.sendMessageEmbeds(builder.build()).queue();
            return;
        }

        Message infoMessage = channel.sendMessageEmbeds(botRole.getInfoEmbed().build()).complete();
        if (infoMessage != null) {
            Message oldMessage = botRole.getInfoMessage();
            if (oldMessage != null) oldMessage.delete().queue();

            botRole.setMessageID(infoMessage.getId());
            botRole.setChannelID(channel.getId());
            botRole.save();
        }
    }

    @Override
    public AccessLevel getAccessLevel() {
        return AccessLevels.getAdministratorAccessLevel();
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }
}
