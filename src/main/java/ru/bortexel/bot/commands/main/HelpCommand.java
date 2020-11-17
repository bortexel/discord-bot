package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandProvider;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import java.util.Objects;

public class HelpCommand implements Command {
    private final BortexelBot bot;

    public HelpCommand(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public void onCommand(Message message) {
        try {
            String[] args = TextUtil.getCommandArgs(message);
            MessageChannel channel = message.getChannel();

            EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
            if (args.length == 1) {
                builder.setTitle("Команды");

                for (CommandProvider provider : bot.getCommandProviders()) {
                    StringBuilder commands = new StringBuilder();
                    int i = 0;

                    for (Command command : provider.getCommands()) {
                        if (command.getAccessLevel() != null && !command.getAccessLevel().hasAccess(message.getMember()))
                            continue;
                        i++;
                        if (i != 1) commands.append("\n");
                        commands.append("`").append(BortexelBot.COMMAND_PREFIX).append(command.getName()).append("`");
                        if (command.getDescription() != null) commands.append(" - ").append(command.getDescription());
                    }

                    if (i != 0) builder.addField(provider.getName(), commands.toString(), false);
                }

                channel.sendMessage(builder.build()).queue();
            } else if (args.length > 1) {
                Command command = bot.getCommand(args[1]);
                if (command == null || (command.getAccessLevel() != null && !command.getAccessLevel().hasAccess(message.getMember()))) {
                    MessageEmbed messageEmbed = EmbedUtil.makeError("Команда не найдена", "Указанная Вами команда не существует, " +
                            "либо у Вас недостаточно прав для просмотра информации о её использовании.").build();
                    channel.sendMessage(messageEmbed).queue();
                    return;
                }

                builder.setTitle("Команда **`" + BortexelBot.COMMAND_PREFIX + command.getName() + "`**");
                if (command.getDescription() != null) builder.setDescription(command.getDescription());
                if (command.getUsage() != null)
                    builder.addField("Использование", "`" + TextUtil.getFullCommandUsage(command) + "`", false);
                if (command.getUsageExample() != null)
                    builder.addField("Пример использования", command.getUsageExample(), false);
                if (command.getAliases().length > 0) builder.addField("Сокращения", "`" + BortexelBot.COMMAND_PREFIX +
                        String.join("`, `" + BortexelBot.COMMAND_PREFIX, command.getAliases()) + "`", false);

                StringBuilder access = new StringBuilder("Общедоступна");
                if (command.getAccessLevel() != null) {
                    access = new StringBuilder();
                    for (Role role : command.getAccessLevel().getRoles()) {
                        access.insert(0, role.getAsMention() + " ");
                    }
                }
                builder.addField("Доступ", access.toString(), true);
                channel.sendMessage(builder.build()).queue();
            }
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "[команда]";
    }

    @Override
    public String getUsageExample() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Отображает описание команд";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "помощь", "хелп" };
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return null;
    }

    @Override
    public int getMinArgumentCount() {
        return 0;
    }
}
