package ru.bortexel.bot.commands.stuff;

import com.github.ricksbrown.cowsay.Cowsay;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

import java.util.Arrays;

public class CowsayCommand implements Command {
    private final BortexelBot bot;

    public CowsayCommand(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        args = Arrays.copyOfRange(args, 1, args.length);
        String say = Cowsay.say(args);
        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setDescription("```" + say + "```");
        message.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "cowsay";
    }

    @Override
    public String getUsage() {
        return "<текст>";
    }

    @Override
    public String getUsageExample() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Выводит корову, которая \"говорит\" указанный текст. Для работы используется вот эта " +
                "[небезызвестная библиотека](https://github.com/ricksbrown/cowsay).";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.bot.getAccessLevels().getSponsorAccessLevel();
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }
}
