package ru.bortexel.bot.core;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class AccessLevel {
    private final List<Role> roles;

    public AccessLevel(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public boolean hasAccess(Member member) {
        if (member == null) return false;
        if (member.isOwner()) return true;
        for (Role role : this.roles) if (member.getRoles().contains(role)) return true;
        return false;
    }
}
