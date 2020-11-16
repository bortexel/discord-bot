package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.EmbedUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingCommand implements Command {
    @Override
    public void onCommand(Message message) {
        long gatewayPing = message.getJDA().getGatewayPing();
        Long restPing = message.getJDA().getRestPing().complete();

        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setTitle("Пинг до Discord");
        builder.addField("Gateway", "" + gatewayPing + " ms", true);
        builder.addField("REST API", "" + restPing + " ms", true);
        try {
            InetAddress host = InetAddress.getLocalHost();
            builder.setFooter("Host: " + host.getHostName() + " (" + host.getHostAddress() + ")");
        } catch (UnknownHostException ignored) { }
        message.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "ping";
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
    public String[] getAliases() {
        return new String[0];
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
