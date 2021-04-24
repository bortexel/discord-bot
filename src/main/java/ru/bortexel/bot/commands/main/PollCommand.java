package ru.bortexel.bot.commands.main;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.AccessLevels;
import ru.bortexel.bot.util.Channels;
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

        // Заголовок - все слова (начиная со второго, чтобы исключить саму команду) первой строки, разделённые пробелом
        String title = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        List<PollVariant> variants = new ArrayList<>();
        List<String> varEmojis = new ArrayList<>();
        boolean multipleChoice = false;

        // Обрабатываем каждую строку
        for (int i = 1; i < lines.length; i++) {
            // Убираем лишние пробелы
            String line = TextUtil.removeDoubleSpaces(lines[i]);
            line = TextUtil.removeSpacesInStart(line);

            // Если строка начинается с %, то не добавляем её в список вариантов, а проверяем слово после %
            if (line.startsWith("%")) {
                // Если после % написано "multiple", то включаем множественный выбор в голосовании
                if (line.substring(1).equalsIgnoreCase("multiple")) multipleChoice = true;
                continue;
            }

            // Получаем список эмодзи в строке
            List<String> emojis = EmojiParser.extractEmojis(line);
            // По умолчанию обозначением варианта будет эмодзи с номером этого варианта
            String emoji = TextUtil.getNumberEmoji(i);
            // Если эмодзи для варианта был задан пользователем, то перезаписываем стандартное значение
            if (emojis.size() > 0) emoji = emojis.get(0);

            // Проверяем, что никакой другой вариант не имеет такого же обозначения
            if (varEmojis.contains(emoji)) {
                EmbedBuilder builder = EmbedUtil.makeError("Ошибка", "Обозначение варианта, заданного в строке " + (i + 1) + ", " +
                        "совпадает с обозначением одного из других вариантов.");
                message.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            // Добавляем в список эмодзи вариантов, чтобы при следующем шаге итерации можно было проверить, что такое обозначение уже есть
            varEmojis.add(emoji);
            variants.add(new PollVariant(TextUtil.removeSpacesInStart(EmojiParser.removeAllEmojis(line)), emoji));
        }

        if (variants.size() < 2) {
            EmbedBuilder builder = EmbedUtil.makeError("Ошибка", "Вы должны задать как минимум два варианта.");
            message.getChannel().sendMessage(builder.build()).queue();
            return;
        }

        Poll poll = Poll.create(title, variants);
        poll.setMultipleChoiceAllowed(multipleChoice);
        poll.send(message.getTextChannel());
    }

    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public String getUsage() {
        return "<вопрос>\n[эмодзи] <название варианта>\n[эмодзи] <название варианта>\n...\n[%multiple]";
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
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL, Channels.PARLIAMENT_CHAT, Channels.ADMIN_CHAT, Channels.POLL_CHAT };
    }

    @Override
    public AccessLevel getAccessLevel() {
        AccessLevels levels = bot.getAccessLevels();
        return AccessLevel.make(bot, levels.getHelperAccessLevel(), levels.getHeadBuilderAccessLevel());
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }
}
