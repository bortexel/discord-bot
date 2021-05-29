package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.*;

import java.sql.SQLException;
import java.util.HashMap;

public class PollCommand extends DefaultBotCommand {
    private final HashMap<String, Poll> polls = new HashMap<>();

    protected PollCommand(BortexelBot bot) {
        super("poll", bot);
        this.setLegacySupported(false);
        this.setEphemeral(true);
    }

    @Override
    public void onCommand(Message message) {
        System.out.println(1);

//        String[] lines = message.getContentRaw().split("\n");
//        String[] args = TextUtil.getCommandArgs(lines[0]);
//
//        // Заголовок - все слова (начиная со второго, чтобы исключить саму команду) первой строки, разделённые пробелом
//        String title = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
//        List<PollVariant> variants = new ArrayList<>();
//        List<String> varEmojis = new ArrayList<>();
//        boolean multipleChoice = false;
//
//        // Обрабатываем каждую строку
//        for (int i = 1; i < lines.length; i++) {
//            // Убираем лишние пробелы
//            String line = TextUtil.removeDoubleSpaces(lines[i]);
//            line = TextUtil.removeSpacesInStart(line);
//
//            // Если строка начинается с %, то не добавляем её в список вариантов, а проверяем слово после %
//            if (line.startsWith("%")) {
//                // Если после % написано "multiple", то включаем множественный выбор в голосовании
//                if (line.substring(1).equalsIgnoreCase("multiple")) multipleChoice = true;
//                continue;
//            }
//
//            // Получаем список эмодзи в строке
//            List<String> emojis = EmojiParser.extractEmojis(line);
//            // По умолчанию обозначением варианта будет эмодзи с номером этого варианта
//            String emoji = TextUtil.getNumberEmoji(i);
//            // Если эмодзи для варианта был задан пользователем, то перезаписываем стандартное значение
//            if (emojis.size() > 0) emoji = emojis.get(0);
//
//            // Проверяем, что никакой другой вариант не имеет такого же обозначения
//            if (varEmojis.contains(emoji)) {
//                EmbedBuilder builder = EmbedUtil.makeError("Ошибка", "Обозначение варианта, заданного в строке " + (i + 1) + ", " +
//                        "совпадает с обозначением одного из других вариантов.");
//                message.getChannel().sendMessage(builder.build()).queue();
//                return;
//            }
//
//            // Добавляем в список эмодзи вариантов, чтобы при следующем шаге итерации можно было проверить, что такое обозначение уже есть
//            varEmojis.add(emoji);
//            variants.add(new PollVariant(id, poll, TextUtil.removeSpacesInStart(EmojiParser.removeAllEmojis(line)), emoji, description));
//        }
//
//        if (variants.size() < 2) {
//            EmbedBuilder builder = EmbedUtil.makeError("Ошибка", "Вы должны задать как минимум два варианта.");
//            message.getChannel().sendMessage(builder.build()).queue();
//            return;
//        }
//
//        Poll poll = Poll.create(title, variants);
//        poll.setMultipleChoiceAllowed(multipleChoice);
//        poll.publish(message.getTextChannel());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        if (event.getSubcommandName() == null) return;
        try {
            switch (event.getSubcommandName()) {
                case "create":
                    OptionMapping name = event.getOption("name");
                    assert name != null;

                    try {
                        Poll poll = Poll.create(name.getAsString(), this.getBot());
                        this.getPolls().put(event.getUser().getId(), poll);
                        hook.sendMessage("Голосование успешно создано").queue();
                        poll.publish(event.getTextChannel());
                    } catch (SQLException exception) {
                        hook.sendMessageEmbeds(EmbedUtil.makeError(
                                "Не удалось создать опрос",
                                "Не удалось создать опрос, повторите попытку позже. ```" + exception.getMessage() + "```"
                        ).build()).queue();
                        BortexelBot.handleException(exception);
                    }

                    break;
                case "describe":
                    this.ensureHasActivePoll(event);
                    hook.sendMessage("describe").queue();
                    break;
                case "set end time":
                    hook.sendMessage("set end time").queue();
                    break;
                case "set anonymous":
                    hook.sendMessage("set anonymous").queue();
                    break;
                case "allow multiple choice":
                    hook.sendMessage("allow multiple choice").queue();
                    break;
                case "allow revote":
                    hook.sendMessage("allow revote").queue();
                    break;
            }
        } catch (Poll.Error error) {
            hook.sendMessage(error.getMessage()).queue();
        }
    }

    private void ensureHasActivePoll(SlashCommandEvent event) throws Poll.Error {
        if (this.getPolls().containsKey(event.getUser().getId())) return;
        throw new Poll.Error("У Вас нет активного голосования. Создайте новое с помощью `/poll create`.");
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this).addSubcommands(
                new SubcommandData("create", "Создаёт новый опрос")
                        .addOption(OptionType.STRING, "name", "Название опроса"),
                new SubcommandData("describe", "Обновляет описание опроса")
                        .addOption(OptionType.STRING, "description", "Описание опроса"),
                new SubcommandData("endtime", "Устанавливает время окончания опроса")
                        .addOption(OptionType.STRING, "time", "Время окончания"),
                new SubcommandData("anonymous", "Включает режим анонимного опроса или отключает его")
                        .addOption(OptionType.BOOLEAN, "value", "Сделать опрос анонимным?"),
                new SubcommandData("multiple", "Включает множественный выбор или отключает его")
                        .addOption(OptionType.BOOLEAN, "value", "Разрешить множественный выбор?"),
                new SubcommandData("revote", "Включает возможность изменить выбор или отключает её")
                        .addOption(OptionType.BOOLEAN, "value", "Разрешить изменять выбор?")
        );
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
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL, Channels.PARLIAMENT_CHAT, Channels.ADMIN_CHAT, Channels.POLL_CHAT };
    }

    @Override
    public AccessLevel getAccessLevel() {
        AccessLevels levels = this.getBot().getAccessLevels();
        return AccessLevel.make(this.getBot(), levels.getHelperAccessLevel(), levels.getHeadBuilderAccessLevel());
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }

    public HashMap<String, Poll> getPolls() {
        return polls;
    }
}
