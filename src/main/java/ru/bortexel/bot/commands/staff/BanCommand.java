package ru.bortexel.bot.commands.staff;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import ru.bortexel.bot.util.TimeUtil;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.ban.Ban;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

public class BanCommand extends DefaultBotCommand {
    protected BanCommand(BortexelBot bot) {
        super("ban", bot);
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        Bortexel4J bortexel = this.getBot().getApiClient();
        User.getByUsername(args[1], bortexel).executeAsync(user -> user.getAccount(bortexel).executeAsync(account -> {
            Ban.Builder builder = new Ban.Builder()
                    .setAccountID(account.getAccount().getID())
                    .setReason(String.join(" ", Arrays.copyOfRange(args, 3, args.length)))
                    .setByIP(true)
                    .setByName(true)
                    .setIP(account.getAccount().getAuthorizedIP());

            Account.getByDiscordID(message.getAuthor().getId(), bortexel).executeAsync(admin -> {
                if (admin == null) return;
                builder.setAdminID(admin.getID());

                Timestamp expiresAt = null;
                if (!args[2].equalsIgnoreCase("permanent")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUtil.convertString(args[2]) * 1000 - calendar.getTimeZone().getRawOffset());
                    expiresAt = new Timestamp(calendar.getTimeInMillis());
                }

                builder.setExpiresAt(expiresAt);
                builder.build().create(bortexel).executeAsync(ban -> message.addReaction("✅").queue(), error -> {
                    MessageEmbed embed = EmbedUtil.makeError("Ошибка", "Не удалось создать бан. Повторите попытку позже. " +
                            "```" + error.getMessage() + "```").build();
                    message.getTextChannel().sendMessage(embed).queue();
                });
            });
        }, error -> {
            MessageEmbed embed = EmbedUtil.makeError("Аккаунт не найден", "Повторите попытку позже.").build();
            message.getTextChannel().sendMessage(embed).queue();
        }), error -> {
            MessageEmbed embed = EmbedUtil.makeError("Игрок не найден", "Игрок с таким никнеймом не найден. " +
                    "Проверьте правильность ввода никнейма и повторите попытку.").build();
            message.getTextChannel().sendMessage(embed).queue();
        });
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[]{};
    }

    @Override
    public String getUsage() {
        return "<игрок> <время> <причина>";
    }

    @Override
    public String getUsageExample() {
        return "`$ban RuscalWorld 10m 2.1` выдаст игроку `RuscalWorld` бан на 10 минут по причине `2.1`";
    }

    @Override
    public String getDescription() {
        return "Блокирует аккаунт игрока на игровом сервере.";
    }

    @Override
    public int getMinArgumentCount() {
        return 3;
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.getBot().getAccessLevels().getModeratorAccessLevel();
    }
}
