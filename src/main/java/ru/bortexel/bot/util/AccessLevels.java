package ru.bortexel.bot.util;

import ru.bortexel.bot.core.AccessLevel;

import java.util.ArrayList;

public class AccessLevels {
    public static AccessLevel getAdministratorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.headAdmin().getRole());
        }});
    }

    public static AccessLevel getModeratorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.headAdmin().getRole());
            add(Roles.admin().getRole());
            add(Roles.moderator().getRole());
        }});
    }

    public static AccessLevel getHelperAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.headAdmin().getRole());
            add(Roles.admin().getRole());
            add(Roles.moderator().getRole());
            add(Roles.helper().getRole());
        }});
    }

    public static AccessLevel getSponsorAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.sponsor().getRole());
            add(Roles.supporter().getRole());
            add(Roles.serverBooster().getRole());
        }});
    }

    public static AccessLevel getHeadBuilderAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.headBuilder().getRole());
            add(Roles.parliament().getRole());
        }});
    }

    public static AccessLevel getParliamentAccessLevel() {
        return new AccessLevel(new ArrayList<>() {{
            add(Roles.parliament().getRole());
        }});
    }
}
