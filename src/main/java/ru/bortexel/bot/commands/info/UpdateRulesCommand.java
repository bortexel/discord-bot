package ru.bortexel.bot.commands.info;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FileUtils;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.ChannelUtil;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.rules.RuleBot;
import ru.bortexel.bot.util.rules.RuleParser;
import ru.bortexel.bot.util.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class UpdateRulesCommand implements Command {
    private final BortexelBot bot;

    public UpdateRulesCommand(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public void onCommand(Message message) {
        try {
            TextChannel channel = message.getTextChannel();
            ChannelUtil.clearMessages(channel, 50);

            File mainRulesTitle = new File("main-rules.png");
            File roleplayRulesTitle = new File("roleplay-rules.png");
            File placesRulesTitle = new File("places-rules.png");
            String mainRules = "";
            String roleplayRules = "";
            String placesRules = "";

            try {
                FileUtils.copyURLToFile(new URL(RuleBot.MAIN_RULES_TITLE_URL), mainRulesTitle);
                FileUtils.copyURLToFile(new URL(RuleBot.ROLEPLAY_RULES_TITLE_URL), roleplayRulesTitle);
                FileUtils.copyURLToFile(new URL(RuleBot.PLACES_RULES_TITLE_URL), placesRulesTitle);
                mainRules = new HttpRequest(RuleBot.MAIN_RULES_URL).getResponse();
                roleplayRules = new HttpRequest(RuleBot.ROLEPLAY_RULES_URL).getResponse();
                placesRules = new HttpRequest(RuleBot.PLACES_RULES_URL).getResponse();
            } catch (IOException e) {
                e.printStackTrace();
            }

            channel.sendFile(mainRulesTitle).queue();
            RuleParser.parseToChannel(mainRules, channel, RuleBot.MAIN_RULES_COLOR);
            channel.sendFile(roleplayRulesTitle).queue();
            RuleParser.parseToChannel(roleplayRules, channel, RuleBot.ROLEPLAY_RULES_COLOR);
            channel.sendFile(placesRulesTitle).queue();
            RuleParser.parseToChannel(placesRules, channel, RuleBot.PLACES_RULES_COLOR);
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public String getName() {
        return "updaterules";
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
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.RULES_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.bot.getAccessLevels().getAdministratorAccessLevel();
    }

    @Override
    public int getMinArgumentCount() {
        return 0;
    }
}
