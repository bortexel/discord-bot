package ru.bortexel.bot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import ru.bortexel.bot.BortexelBot;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.ban.Ban;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.sql.Timestamp;

public class RoleChecker {
    public static void updateRoles(Member member) {
        BortexelBot bot = BortexelBot.getInstance();
        Bortexel4J client = bot.getApiClient();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Account.getByDiscordID(member.getId(), client).executeAsync(account -> {
            // Проверяем на наличие активных банов
            account.getBans(client).executeAsync(accountBans -> {
                for (Ban ban : accountBans.getBans()) {
                    // Выдаём роль "Забанен", если есть действующий бан
                    if (!ban.isActual()) continue;
                    Roles.bannedPlayer(bot).addTo(member);
                    break;
                }
            });

            // Проверяем на наличие игровых аккаунтов
            account.getUsers(client).executeAsync(accountUsers -> {
                for (User user : accountUsers.getUsers()) {
                    // Выдаём роль "Игрок", если срок действия игрового аккаунта не истёк
                    Timestamp validTill = user.getValidTill();
                    if (validTill != null && validTill.before(now)) continue;
                    Roles.player(bot).addTo(member);

                    // Выдаём роль "Активный игрок", если срок активности игрового аккаунта не истёк
                    Timestamp activeTill = user.getActiveTill();
                    if (activeTill == null || activeTill.before(now)) continue;
                    Roles.activePlayer(bot).addTo(member);
                    break;
                }
            });
        }, error -> {
            // Аккаунта нет, снимаем роли
            Roles.player(bot).removeFrom(member);
            Roles.activePlayer(bot).removeFrom(member);
            Roles.shopOwner(bot).removeFrom(member);
            Roles.cityRepresentative(bot).removeFrom(member);
        });
    }

    public static void updateRoles(String memberID) {
        if (memberID == null) return;
        Guild guild = BortexelBot.getInstance().getMainGuild();
        Member member = guild.getMemberById(memberID);
        if (member != null) updateRoles(member);
    }
}
