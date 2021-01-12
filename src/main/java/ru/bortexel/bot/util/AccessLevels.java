package ru.bortexel.bot.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;

import java.util.ArrayList;

public class AccessLevels {
    private final JDA jda;

    public AccessLevels(BortexelBot bot) {
        this.jda = bot.getJDA();
    }

    public AccessLevel getAdministratorAccessLevel() {
        return new AccessLevel(new ArrayList<Role>() {{
            add(jda.getRoleById(Roles.ADMIN_ROLE));
        }});
    }

    public AccessLevel getModeratorAccessLevel() {
        return new AccessLevel(new ArrayList<Role>() {{
            add(jda.getRoleById(Roles.ADMIN_ROLE));
            add(jda.getRoleById(Roles.MODERATOR_ROLE));
        }});
    }

    public AccessLevel getHelperAccessLevel() {
        return new AccessLevel(new ArrayList<Role>() {{
            add(jda.getRoleById(Roles.ADMIN_ROLE));
            add(jda.getRoleById(Roles.MODERATOR_ROLE));
            add(jda.getRoleById(Roles.HELPER_ROLE));
        }});
    }

    public AccessLevel getSponsorAccessLevel() {
        return new AccessLevel(new ArrayList<Role>() {{
            add(jda.getRoleById(Roles.SPONSOR_ROLE));
            add(jda.getRoleById(Roles.SERVER_BOOSTER_ROLE));
        }});
    }

    public AccessLevel getHeadBuilderAccessLevel() {
        return new AccessLevel(new ArrayList<Role>() {{
            add(jda.getRoleById(Roles.HEAD_BUILDER_ROLE));
            add(jda.getRoleById(Roles.PARLIAMENT_ROLE));
        }});
    }
}
