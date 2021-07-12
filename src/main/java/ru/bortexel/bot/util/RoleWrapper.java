package ru.bortexel.bot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.BortexelBot;

public class RoleWrapper {
    private final @Nullable Role role;
    private final @NotNull BortexelBot bot;

    public RoleWrapper(String id, BortexelBot bot) {
        this.role = bot.getJDA().getRoleById(id);
        this.bot = bot;
    }

    public RoleWrapper(@Nullable Role role, @NotNull BortexelBot bot) {
        this.role = role;
        this.bot = bot;
    }

    public void addTo(Member member) {
        if (this.getRole() == null) return;
        this.getGuild().addRoleToMember(member, this.getRole()).queue();
    }

    public void addTo(String memberID) {
        if (this.getRole() == null) return;
        this.getGuild().addRoleToMember(memberID, this.getRole()).queue();
    }

    public void removeFrom(Member member) {
        if (this.getRole() == null) return;
        this.getGuild().removeRoleFromMember(member, this.getRole()).queue();
    }

    public void removeFrom(String memberID) {
        if (this.getRole() == null) return;
        this.getGuild().removeRoleFromMember(memberID, this.getRole()).queue();
    }

    private Guild getGuild() {
        return this.getBot().getMainGuild();
    }

    public @Nullable Role getRole() {
        return role;
    }

    public @NotNull BortexelBot getBot() {
        return bot;
    }
}
