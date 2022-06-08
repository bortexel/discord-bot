package ru.bortexel.bot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import ru.bortexel.bot.BortexelBot;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.ban.Ban;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.sql.Timestamp;

public class MemberSynchronizer {
    public static void synchronize(Member member) {
        BortexelBot bot = BortexelBot.getInstance();
        Bortexel4J client = bot.getApiClient();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Account.getByDiscordID(member.getId(), client).executeAsync(account -> {
            // Проверяем на наличие активных банов
            account.getBans(client).executeAsync(accountBans -> {
                for (Ban ban : accountBans.getBans()) {
                    // Выдаём роль "Забанен", если есть действующий бан
                    if (!ban.isActual()) continue;
                    Roles.bannedPlayer().grant(member);
                    break;
                }
            });

            // Проверяем на наличие игровых аккаунтов
            account.getUsers(client).executeAsync(accountUsers -> {
                for (User user : accountUsers.getUsers()) {
                    // Выдаём роль "Игрок", если срок действия игрового аккаунта не истёк
                    Timestamp validTill = user.getValidTill();
                    if (validTill != null && validTill.before(now)) continue;
                    Roles.player().grant(member);

                    // Выдаём роль "Активный игрок", если срок активности игрового аккаунта не истёк
                    Timestamp activeTill = user.getActiveTill();
                    if (activeTill == null || activeTill.before(now)) continue;
                    Roles.activePlayer().grant(member);
                    break;
                }

                if (accountUsers.getUsers().size() > 0) {
                    member.modifyNickname(accountUsers.getUsers().get(0).getUsername()).queue();
                }
            });
        }, error -> {
            // Аккаунта нет, снимаем роли
            Roles.player().revoke(member);
            Roles.activePlayer().revoke(member);
            Roles.shopOwner().revoke(member);
            Roles.cityRepresentative().revoke(member);
        });
    }

    public static void synchronize(String memberID) {
        if (memberID == null) return;
        Guild guild = BortexelBot.getInstance().getMainGuild();
        Member member = guild.getMemberById(memberID);
        if (member != null) synchronize(member);
    }
}
