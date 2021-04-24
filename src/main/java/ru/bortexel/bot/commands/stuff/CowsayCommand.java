package ru.bortexel.bot.commands.stuff;

import com.github.ricksbrown.cowsay.Cowsay;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;

import java.util.Arrays;

public class CowsayCommand extends DefaultBotCommand {
    protected CowsayCommand(BortexelBot bot) {
        super("cowsay", bot);
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        args = Arrays.copyOfRange(args, 1, args.length);
        message.getChannel().sendMessage(cowsay(args)).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, CommandHook hook) {
        SlashCommandEvent.OptionData textOption = event.getOption("text");
        if (textOption == null) return;
        String[] args = textOption.getAsString().split(" ");
        hook.sendMessage(cowsay(args)).queue();
    }

    private MessageEmbed cowsay(String[] args) {
        String say = Cowsay.say(args);
        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setDescription("```" + say + "```");
        return builder.build();
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
        return "Выводит корову, которая \"говорит\" указанный текст.";
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.getBot().getAccessLevels().getSponsorAccessLevel();
    }

    @Override
    public CommandUpdateAction.CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING, "text", "Текст, который необходимо вывести")
                        .setRequired(true));
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }
}
