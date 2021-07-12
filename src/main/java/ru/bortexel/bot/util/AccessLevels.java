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
            add(Roles.admin(bot).getRole());
        }}, bot);
    }

    public AccessLevel getModeratorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.admin(bot).getRole());
            add(Roles.moderator(bot).getRole());
        }}, bot);
    }

    public AccessLevel getHelperAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.admin(bot).getRole());
            add(Roles.moderator(bot).getRole());
            add(Roles.helper(bot).getRole());
        }}, bot);
    }

    public AccessLevel getSponsorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.sponsor(bot).getRole());
            add(Roles.supporter(bot).getRole());
            add(Roles.serverBooster(bot).getRole());
        }}, bot);
    }

    public AccessLevel getHeadBuilderAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.headBuilder(bot).getRole());
            add(Roles.parliament(bot).getRole());
        }}, bot);
    }

    public AccessLevel getParliamentAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.parliament(bot).getRole());
        }}, bot);
    }
}
