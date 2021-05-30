package ru.bortexel.bot.util;

import net.dv8tion.jda.api.JDA;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;

import java.util.ArrayList;

public class AccessLevels {
    private final JDA jda;
    private final BortexelBot bot;

    public AccessLevels(BortexelBot bot) {
        this.jda = bot.getJDA();
        this.bot = bot;
    }

    public AccessLevel getAdministratorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(jda.getRoleById(Roles.ADMIN_ROLE));
        }}, bot);
    }

    public AccessLevel getModeratorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(jda.getRoleById(Roles.ADMIN_ROLE));
            add(jda.getRoleById(Roles.MODERATOR_ROLE));
        }}, bot);
    }

    public AccessLevel getHelperAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(jda.getRoleById(Roles.ADMIN_ROLE));
            add(jda.getRoleById(Roles.MODERATOR_ROLE));
            add(jda.getRoleById(Roles.HELPER_ROLE));
        }}, bot);
    }

    public AccessLevel getSponsorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(jda.getRoleById(Roles.SPONSOR_ROLE));
            add(jda.getRoleById(Roles.SUPPORTER_ROLE));
            add(jda.getRoleById(Roles.SERVER_BOOSTER_ROLE));
        }}, bot);
    }

    public AccessLevel getHeadBuilderAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(jda.getRoleById(Roles.HEAD_BUILDER_ROLE));
            add(jda.getRoleById(Roles.PARLIAMENT_ROLE));
        }}, bot);
    }

    public AccessLevel getParliamentAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(jda.getRoleById(Roles.PARLIAMENT_ROLE));
        }}, bot);
    }
}
