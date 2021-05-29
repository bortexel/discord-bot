package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandGroup;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

public class HelpCommand extends DefaultBotCommand {
    protected HelpCommand(BortexelBot bot) {
        super("help", bot);

        this.addAlias("помощь");
        this.addAlias("хелп");

        this.setGlobal(true);
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        MessageChannel channel = message.getChannel();
        Member member = message.getMember();
        if (args.length == 1) channel.sendMessage(getHelp(member, null)).queue();
        else channel.sendMessage(getHelp(member, args[1])).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        Member member = event.getMember();
        OptionMapping commandOption = event.getOption("command");
        if (commandOption == null) hook.sendMessageEmbeds(getHelp(member, null)).queue();
        else hook.sendMessageEmbeds(getHelp(member, commandOption.getAsString())).queue();
    }

    private MessageEmbed getHelp(@Nullable Member member, @Nullable String commandName) {
        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();

        if (commandName == null) {
            builder.setTitle("Команды");

            for (CommandGroup provider : this.getBot().getCommandProviders()) {
                StringBuilder commands = new StringBuilder();
                int i = 0;

                for (Command command : provider.getCommands()) {
                    if (member == null || command.getAccessLevel() != null && !command.getAccessLevel().hasAccess(member)) continue;
                    if (!command.isGlobal() && !member.getGuild().getId().equals(this.getBot().getMainGuildID())) continue;
                    i++;
                    if (i != 1) commands.append("\n");
                    commands.append("`").append(CommandUtil.getPreferredPrefix(command)).append(command.getName()).append("`");
                    if (command.getDescription() != null) commands.append(" - ").append(command.getDescription());
                }

                if (i != 0) builder.addField(provider.getName(), commands.toString(), false);
            }
        } else {
            Command command = this.getBot().getCommand(commandName);
            MessageEmbed embed = EmbedUtil.makeError("Команда не найдена", "Указанная Вами команда не существует, " +
                    "либо у Вас недостаточно прав для просмотра информации о её использовании.").build();

            // Error if command doesn't exist
            if (command == null) return embed;
            // Error if not global command executed outside guild
            if (member == null && !command.isGlobal()) return embed;
            // Error if not global command executed outside main guild
            if (!command.isGlobal() && member != null && !member.getGuild().getId().equals(this.getBot().getMainGuildID())) return embed;
            // Error if member doesn't have access to this command
            if (command.getAccessLevel() != null && !command.getAccessLevel().hasAccess(member)) return embed;

            builder = EmbedUtil.makeCommandInfo(command);
        }

        return builder.build();
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(OptionType.STRING, "command", "Команда");
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
