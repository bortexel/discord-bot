package ru.bortexel.bot.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.listeners.BotListener;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

import java.util.Arrays;

public class CommandListener extends BotListener {
    public CommandListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        try {
            String text = event.getMessage().getContentRaw();
            if (!text.startsWith(BortexelBot.COMMAND_PREFIX)) return;
            text = TextUtil.removeDoubleSpaces(text);

            String[] args = text.split(" ");
            String commandLabel = args[0].substring(BortexelBot.COMMAND_PREFIX.length());
            Command command = this.getBot().getCommand(commandLabel);
            if (command == null) return;
            if (command.isGlobal() && !event.getGuild().getId().equals(this.getBot().getMainGuildID())) return;

            assert event.getMember() != null;
            AccessLevel accessLevel = command.getAccessLevel();
            if (accessLevel != null && !accessLevel.hasAccess(event.getMember())) return;

            if (!event.getMember().isOwner() && command.getAllowedChannelIds().length > 0 &&
                    !Arrays.asList(command.getAllowedChannelIds()).contains(event.getChannel().getId())) {
                EmbedBuilder builder = EmbedUtil.makeError("Недопустимый канал", "Данная команда не может быть выполнена здесь. " +
                        "Допустимые каналы: <#" + String.join(">, <#", command.getAllowedChannelIds()) + ">");
                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            if (args.length >= 2 && args[1].equalsIgnoreCase("help")) {
                EmbedBuilder builder = EmbedUtil.makeCommandInfo(command);
                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            if (args.length - 1 < command.getMinArgumentCount()) {
                EmbedBuilder builder = EmbedUtil.makeCommandUsage(command);
                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            command.onCommand(event.getMessage());
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        try {
            // Executing slash commands in DMs makes errors, so reject any DM executions
            if (event.getGuild() == null) return;

            Command command = this.getBot().getCommand(event.getName());
            if (command == null || command.getSlashCommandData() == null) return;

            if (command.getAccessLevel() != null) {
                if (event.getMember() == null || event.getGuild() == null) {
                    sendError(event, "Эта команда недоступна в личных сообщения.");
                    return;
                }

                if (!command.getAccessLevel().hasAccess(event.getMember())) {
                    sendError(event, "У Вас недостаточно прав для выполнения данной команды.");
                    return;
                }
            }

            if (event.getMember() != null && !event.getMember().isOwner() && command.getAllowedChannelIds().length > 0 && !command.isEphemeral()) {
                if (!Arrays.asList(command.getAllowedChannelIds()).contains(event.getChannel().getId())) {
                    sendError(event, "Эта команда не может быть выполнена здесь.");
                    return;
                }
            }

            InteractionHook hook = event.getHook();
            event.deferReply(command.isEphemeral()).queue();
            hook.setEphemeral(command.isEphemeral());

            command.onSlashCommand(event, hook);
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    public void sendError(SlashCommandEvent event, String message) {
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        event.deferReply(true).queue();
        hook.sendMessage(message).queue();
    }
}
