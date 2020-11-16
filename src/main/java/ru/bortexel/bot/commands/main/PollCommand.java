package ru.bortexel.bot.commands.main;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import ru.bortexel.bot.util.poll.Poll;
import ru.bortexel.bot.util.poll.PollVariant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollCommand implements Command {
    private final BortexelBot bot;

    public PollCommand(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public void onCommand(Message message) {
        String[] lines = message.getContentRaw().split("\n");
        String[] args = TextUtil.getCommandArgs(lines[0]);
        String title = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        List<PollVariant> variants = new ArrayList<>();
        List<String> varEmojis = new ArrayList<>();

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            List<String> emojis = EmojiParser.extractEmojis(line);
            String emoji = TextUtil.getNumberEmoji(i);
            if (emojis.size() > 0) emoji = emojis.get(0);

            if (varEmojis.contains(emoji)) {
                EmbedBuilder builder = EmbedUtil.makeError("Ошибка", "Обозначение варианта, заданного в строке " + (i + 1) + ", " +
                        "совпадает с обозначением одного из других вариантов.");
                message.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            varEmojis.add(emoji);
            variants.add(new PollVariant(EmojiParser.removeAllEmojis(line), emoji));
        }

        Poll.create(message.getTextChannel(), title, variants);
    }

    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public String getUsage() {
        return "<args>";
    }

    @Override
    public String getUsageExample() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Создаёт опрос с заданными параметрами";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public AccessLevel getAccessLevel() {
        return bot.getAccessLevels().getAdministratorAccessLevel();
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }
}
