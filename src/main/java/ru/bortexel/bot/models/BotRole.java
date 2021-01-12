package ru.bortexel.bot.models;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.EmbedUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BotRole {
    private final int id;
    private final String discordId;
    private String title;
    private String description;
    private final BortexelBot bot;

    public BotRole(int id, String discordId, String title, String description, BortexelBot bot) {
        this.id = id;
        this.discordId = discordId;
        this.title = title;
        this.description = description;
        this.bot = bot;
    }

    public static BotRole getByDiscordRole(Role role, BortexelBot bot) {
        try {
            PreparedStatement statement = bot.getDatabase().getConnection().prepareStatement("SELECT * FROM `roles` WHERE `discord_id` = ?");
            statement.setString(1, role.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) return getFromResult(resultSet, bot);
        } catch (SQLException throwables) {
            BortexelBot.handleException(throwables);
        }

        return null;
    }

    private static BotRole getFromResult(ResultSet resultSet, BortexelBot bot) throws SQLException {
        int id = resultSet.getInt("id");
        String discordId = resultSet.getString("discord_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        return new BotRole(id, discordId, title, description, bot);
    }

    public boolean save() {
        try {
            PreparedStatement statement = bot.getDatabase().getConnection().prepareStatement("UPDATE `roles` SET `title` = ?, `description` = ? WHERE `id` = ?");
            statement.setString(1, this.getTitle());
            statement.setString(2, this.getDescription());
            statement.setInt(3, this.getId());
            return statement.executeUpdate() != 0;
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public String getDiscordId() {
        return discordId;
    }

    public Role getDiscordRole() {
        return this.getBot().getJDA().getRoleById(this.getDiscordId());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BortexelBot getBot() {
        return bot;
    }

    public EmbedBuilder getInfoEmbed() {
        return EmbedUtil.makeRoleInfo(this);
    }
}
