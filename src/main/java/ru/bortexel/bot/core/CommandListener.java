package ru.bortexel.bot.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

public class CommandListener extends ListenerAdapter {
    private final BortexelBot bot;

    public CommandListener(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        try {
            String text = event.getMessage().getContentRaw();
            if (!text.startsWith(BortexelBot.COMMAND_PREFIX)) return;
            text = TextUtil.removeDoubleSpaces(text);

            String[] args = text.split(" ");
            String commandLabel = args[0].substring(BortexelBot.COMMAND_PREFIX.length());
            Command command = bot.getCommand(commandLabel);
            if (command == null) return;

            assert event.getMember() != null;
            AccessLevel accessLevel = command.getAccessLevel();
            if (accessLevel != null && !accessLevel.hasAccess(event.getMember())) return;

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
}
