package ru.bortexel.bot.commands.info;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FileUtils;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.AccessLevels;
import ru.bortexel.bot.util.ChannelUtil;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.HttpRequest;
import ru.bortexel.bot.util.rules.RuleBot;
import ru.bortexel.bot.util.rules.RuleParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class UpdateRulesCommand extends DefaultBotCommand {
    protected UpdateRulesCommand(BortexelBot bot) {
        super("updaterules", bot);
    }

    @Override
    public void onCommand(Message message) {
        try {
            TextChannel channel = message.getTextChannel();
            ChannelUtil.clearMessages(channel, 50);

            File mainRulesTitle = new File("main-rules.png");
            String mainRules = "";

            try {
                FileUtils.copyURLToFile(new URL(RuleBot.MAIN_RULES_TITLE_URL), mainRulesTitle);
                mainRules = new HttpRequest(RuleBot.MAIN_RULES_URL).getResponse();
            } catch (IOException e) {
                e.printStackTrace();
            }

            channel.sendFile(mainRulesTitle).queue();
            RuleParser.parseToChannel(mainRules, channel, RuleBot.MAIN_RULES_COLOR);
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
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
        return null;
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.RULES_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return AccessLevels.getAdministratorAccessLevel();
    }

    @Override
    public int getMinArgumentCount() {
        return 0;
    }
}
