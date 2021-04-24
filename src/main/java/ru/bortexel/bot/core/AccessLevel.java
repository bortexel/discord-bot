package ru.bortexel.bot.core;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;

import java.util.ArrayList;
import java.util.List;

public class AccessLevel {
    private final List<Role> roles;
    private final BortexelBot bot;

    public AccessLevel(List<Role> roles, BortexelBot bot) {
        this.roles = roles;
        this.bot = bot;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public boolean hasAccess(Member member) {
        if (member == null) return false;
        if (member.isOwner() && member.getGuild().getId().equals(bot.getMainGuildID())) return true;
        for (Role role : this.roles) if (member.getRoles().contains(role)) return true;
        return false;
    }

    public static AccessLevel make(BortexelBot bot, AccessLevel... levels) {
        List<Role> roles = new ArrayList<>();
        for (AccessLevel level : levels) roles.addAll(level.roles);
        return new AccessLevel(roles, bot);
    }
}
