package ru.bortexel.bot.util;

import ru.bortexel.bot.BortexelBot;

public class Roles {
    public static final String HELPER_ROLE = "536125597227941890";
    public static final String MODERATOR_ROLE = "536125163415273474";
    public static final String ADMIN_ROLE = "536124431140126730";

    public static final String PARLIAMENT_ROLE = "674168943908487189";
    public static final String HEAD_BUILDER_ROLE = "738098903634935838";
    public static final String BUILDER_ROLE = "685422421439807509";
    public static final String JUDGE_ROLE = "794944850000478218";

    public static final String SPONSOR_ROLE = "828006196401274900";
    public static final String SUPPORTER_ROLE = "651398861868367902";
    public static final String SERVER_BOOSTER_ROLE = "645498320285990923";

    public static final String CITY_REPRESENTATIVE_ROLE = "738083989092565053";
    public static final String SHOP_OWNER_ROLE = "817049891254829116";

    public static final String PLAYER_ROLE = "592674085092327434";
    public static final String ACTIVE_PLAYER_ROLE = "683954431418433557";
    public static final String BANNED_PLAYER_ROLE = "683956308411416623";

    public static RoleWrapper helper(BortexelBot bot) {
        return getRole(HELPER_ROLE, bot);
    }

    public static RoleWrapper moderator(BortexelBot bot) {
        return getRole(MODERATOR_ROLE, bot);
    }

    public static RoleWrapper admin(BortexelBot bot) {
        return getRole(ADMIN_ROLE, bot);
    }

    public static RoleWrapper parliament(BortexelBot bot) {
        return getRole(PARLIAMENT_ROLE, bot);
    }

    public static RoleWrapper headBuilder(BortexelBot bot) {
        return getRole(HEAD_BUILDER_ROLE, bot);
    }

    public static RoleWrapper builder(BortexelBot bot) {
        return getRole(BUILDER_ROLE, bot);
    }

    public static RoleWrapper judge(BortexelBot bot) {
        return getRole(JUDGE_ROLE, bot);
    }

    public static RoleWrapper sponsor(BortexelBot bot) {
        return getRole(SPONSOR_ROLE, bot);
    }

    public static RoleWrapper supporter(BortexelBot bot) {
        return getRole(SUPPORTER_ROLE, bot);
    }

    public static RoleWrapper serverBooster(BortexelBot bot) {
        return getRole(SERVER_BOOSTER_ROLE, bot);
    }

    public static RoleWrapper cityRepresentative(BortexelBot bot) {
        return getRole(CITY_REPRESENTATIVE_ROLE, bot);
    }

    public static RoleWrapper shopOwner(BortexelBot bot) {
        return getRole(SHOP_OWNER_ROLE, bot);
    }

    public static RoleWrapper player(BortexelBot bot) {
        return getRole(PLAYER_ROLE, bot);
    }

    public static RoleWrapper activePlayer(BortexelBot bot) {
        return getRole(ACTIVE_PLAYER_ROLE, bot);
    }

    public static RoleWrapper bannedPlayer(BortexelBot bot) {
        return getRole(BANNED_PLAYER_ROLE, bot);
    }

    private static RoleWrapper getRole(String id, BortexelBot bot) {
        return new RoleWrapper(id, bot);
    }
}
