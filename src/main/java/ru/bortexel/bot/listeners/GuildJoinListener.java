package ru.bortexel.bot.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.jetbrains.annotations.NotNull;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.Roles;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.ban.Ban;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.sql.Timestamp;

public class GuildJoinListener extends BotListener {
    public GuildJoinListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Bortexel4J client = this.getBot().getApiClient();
        JDA jda = this.getBot().getJDA();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Account.getByDiscordID(member.getId(), client).executeAsync(account -> {
            // Проверяем на наличие активных банов
            account.getBans(client).executeAsync(accountBans -> {
                Role bannedPlayer = jda.getRoleById(Roles.BANNED_PLAYER_ROLE);
                if (bannedPlayer == null) return;

                for (Ban ban : accountBans.getBans()) {
                    // Выдаём роль "Забанен", если есть действующий бан
                    if (!ban.isActual()) continue;
                    guild.addRoleToMember(member, bannedPlayer).queue();
                    break;
                }
            });

            // Проверяем на наличие игровых аккаунтов
            account.getUsers(client).executeAsync(accountUsers -> {
                Role player = jda.getRoleById(Roles.PLAYER_ROLE);
                if (player == null) return;

                for (User user : accountUsers.getUsers()) {
                    // Выдаём роль "Игрок", если срок действия игрового аккаунта не истёк
                    Timestamp validTill = user.getValidTill();
                    if (validTill == null || validTill.before(now)) continue;
                    guild.addRoleToMember(member, player).queue();

                    // Находим роль "Активный игрок", а если её нет, выходим из цикла
                    Role activePlayer = jda.getRoleById(Roles.ACTIVE_PLAYER_ROLE);
                    if (activePlayer == null) break;

                    // Выдаём роль "Активный игрок", если срок активности игрового аккаунта не истёк
                    Timestamp activeTill = user.getActiveTill();
                    if (activeTill == null || activeTill.before(now)) continue;
                    guild.addRoleToMember(member, activePlayer).queue((a) -> {}, Throwable::printStackTrace);
                    break;
                }
            });
        });
    }
}
