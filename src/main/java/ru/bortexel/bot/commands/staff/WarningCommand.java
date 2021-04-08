package ru.bortexel.bot.commands.staff;

import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.user.User;
import ru.ruscalworld.bortexel4j.models.warning.Warning;

import java.util.Arrays;

public class WarningCommand extends DefaultBotCommand {
    protected WarningCommand(BortexelBot bot) {
        super("warning", bot);
        this.addAlias("warn");
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        Bortexel4J bortexel = this.getBot().getApiClient();

        User.getByUsername(args[1], bortexel).executeAsync(user -> user.getAccount(bortexel).executeAsync(account -> {
            int power = 1;
            try {
                power = Integer.parseInt(args[2]);
            } catch (Exception ignored) { }

            Warning.Builder builder = new Warning.Builder()
                    .setAccountID(account.getAccount().getID())
                    .setReason(String.join(" ", Arrays.copyOfRange(args, 3, args.length)))
                    .setPower(power);

            Account.getByDiscordID(message.getAuthor().getId(), bortexel).executeAsync(admin -> {
                if (admin == null) return;
                builder.setAdminID(admin.getID());

                builder.build().create(bortexel).executeAsync(warning -> message.addReaction("✅").queue(),
                        error -> message.getTextChannel().sendMessage(EmbedUtil.makeError("Ошибка",
                        "Не удалось создать предупреждение. Повторите попытку позже. ```" + error.getMessage() + "```").build()).queue());
            });
        }, error -> message.getTextChannel().sendMessage(EmbedUtil.makeError("Аккаунт не найден", "Повторите попытку позже.").build()).queue()), error -> message.getTextChannel().sendMessage(EmbedUtil.makeError("Игрок не найден", "Игрок с таким никнеймом не найден. " +
                "Проверьте правильность ввода никнейма и повторите попытку.").build()).queue());
    }

    @Override
    public String getUsage() {
        return "<игрок> <мощность> <причина>";
    }

    @Override
    public String getUsageExample() {
        return "`$warning RuscalWorld 5 2.1` выдаст игроку `RuscalWorld` предупреждение мощностью 5 по причине `2.1`";
    }

    @Override
    public String getDescription() {
        return "Выдаёт указанному игроку предупреждение на игровом сервере";
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] {};
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.getBot().getAccessLevels().getModeratorAccessLevel();
    }

    @Override
    public int getMinArgumentCount() {
        return 3;
    }
}
