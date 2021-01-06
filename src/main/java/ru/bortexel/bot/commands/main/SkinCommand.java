package ru.bortexel.bot.commands.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import ru.ruscalworld.bortexel4j.exceptions.NotFoundException;
import ru.ruscalworld.bortexel4j.models.user.User;
import ru.ruscalworld.bortexel4j.util.BortexelSkins;

public class SkinCommand extends DefaultBotCommand {
    protected SkinCommand(BortexelBot bot) {
        super("skin", bot);
        this.addAlias("скин");
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        try {
            User user = User.getByUsername(args[1], this.getBot().getApiClient()).execute();

            EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
            builder.setTitle(user.getUsername());
            if (user.getSkinSystem() != null) builder.addField("Система скинов", user.getSkinSystem(), true);
            if (user.getSkinName() != null) builder.addField("Название скина", user.getSkinName(), true);
            builder.setImage(BortexelSkins.getBodyRenderURL(user.getUsername(), true));

            message.reply(builder.build()).queue();
        } catch (NotFoundException e) {
            EmbedBuilder builder = EmbedUtil.makeError("Игрок не найден", "Не удалось найти игрока с никнеймом `" + args[1] + "` " +
                    "на сервере. Проверьте правильность написания никнейма и повторите попытку.");
            message.reply(builder.build()).queue();
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public String getUsage() {
        return "<игрок>";
    }

    @Override
    public String getUsageExample() {
        return "`$skin RuscalWorld` выведет информацию о скине игрока RuscalWorld";
    }

    @Override
    public String getDescription() {
        return "Показывает информацию о скине игрока на сервере";
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }
}
