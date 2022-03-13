package ru.bortexel.bot.util;

import ru.bortexel.bot.BortexelBot;

public class Roles {
    private static final BortexelBot bot = BortexelBot.getInstance();

    public static final String HELPER_ROLE = "536125597227941890";
    public static final String MODERATOR_ROLE = "536125163415273474";
    public static final String ADMIN_ROLE = "536124431140126730";
    public static final String HEAD_ADMIN_ROLE = "945712835357470801";

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

    public static RoleWrapper helper() {
        return getRole(HELPER_ROLE);
    }

    public static RoleWrapper moderator() {
        return getRole(MODERATOR_ROLE);
    }

    public static RoleWrapper admin() {
        return getRole(ADMIN_ROLE);
    }

    public static RoleWrapper headAdmin() {
        return getRole(HEAD_ADMIN_ROLE);
    }

    public static RoleWrapper parliament() {
        return getRole(PARLIAMENT_ROLE);
    }

    public static RoleWrapper headBuilder() {
        return getRole(HEAD_BUILDER_ROLE);
    }

    public static RoleWrapper builder() {
        return getRole(BUILDER_ROLE);
    }

    public static RoleWrapper judge() {
        return getRole(JUDGE_ROLE);
    }

    public static RoleWrapper sponsor() {
        return getRole(SPONSOR_ROLE);
    }

    public static RoleWrapper supporter() {
        return getRole(SUPPORTER_ROLE);
    }

    public static RoleWrapper serverBooster() {
        return getRole(SERVER_BOOSTER_ROLE);
    }

    public static RoleWrapper cityRepresentative() {
        return getRole(CITY_REPRESENTATIVE_ROLE);
    }

    public static RoleWrapper shopOwner() {
        return getRole(SHOP_OWNER_ROLE);
    }

    public static RoleWrapper player() {
        return getRole(PLAYER_ROLE);
    }

    public static RoleWrapper activePlayer() {
        return getRole(ACTIVE_PLAYER_ROLE);
    }

    public static RoleWrapper bannedPlayer() {
        return getRole(BANNED_PLAYER_ROLE);
    }

    private static RoleWrapper getRole(String id) {
        return new RoleWrapper(id, bot);
    }
}
