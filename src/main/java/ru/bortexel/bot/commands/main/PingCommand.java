package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import ru.bortexel.bot.commands.DefaultCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingCommand extends DefaultCommand {
    protected PingCommand() {
        super("ping");
    }

    @Override
    public void onCommand(Message message) {
        message.getChannel().sendMessage(ping(message.getJDA())).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, CommandHook hook) {
        hook.sendMessage(ping(event.getJDA())).queue();
    }

    private MessageEmbed ping(JDA jda) {
        long gatewayPing = jda.getGatewayPing();
        Long restPing = jda.getRestPing().complete();

        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setTitle("Пинг до Discord");
        builder.addField("Gateway", "" + gatewayPing + " ms", true);
        builder.addField("REST API", "" + restPing + " ms", true);
        try {
            InetAddress host = InetAddress.getLocalHost();
            builder.setFooter("Host: " + host.getHostName() + " (" + host.getHostAddress() + ")");
        } catch (UnknownHostException ignored) { }
        return builder.build();
    }

    @Override
    public CommandUpdateAction.CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this);
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getUsageExample() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Получает время отклика Discord API";
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
