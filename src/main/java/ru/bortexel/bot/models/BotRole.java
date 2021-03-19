package ru.bortexel.bot.models;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.util.EmbedUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BotRole {
    private final int id;
    private final String discordID;
    private String title;
    private String description;
    private String joinInfo;
    private String headmasterID;
    private String messageID;
    private String channelID;
    private boolean showMembers;
    private final BortexelBot bot;

    public BotRole(int id, String discordId, String title, String description, String joinInfo, String headmasterID, String messageID, String channelID, boolean showMembers, BortexelBot bot) {
        this.id = id;
        this.discordID = discordId;
        this.title = title;
        this.description = description;
        this.joinInfo = joinInfo;
        this.headmasterID = headmasterID;
        this.messageID = messageID;
        this.channelID = channelID;
        this.showMembers = showMembers;
        this.bot = bot;
    }

    public static BotRole getByID(int id, BortexelBot bot) {
        try {
            PreparedStatement statement = bot.getDatabase().getConnection().prepareStatement("SELECT * FROM `roles` WHERE `id` = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) return getFromResult(resultSet, bot);
        } catch (SQLException throwables) {
            BortexelBot.handleException(throwables);
        }

        return null;
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
        String discordID = resultSet.getString("discord_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        String joinInfo = resultSet.getString("join_info");
        String headmasterID = resultSet.getString("headmaster_id");
        String messageID = resultSet.getString("message_id");
        String channelID = resultSet.getString("channel_id");
        boolean showMembers = resultSet.getBoolean("show_members");
        return new BotRole(id, discordID, title, description, joinInfo, headmasterID, messageID, channelID, showMembers, bot);
    }

    public boolean save() {
        try {
            PreparedStatement statement = bot.getDatabase().getConnection().prepareStatement("UPDATE `roles` SET `title` = ?, `description` = ?, `join_info` = ?, `headmaster_id` = ?, `message_id` = ?, `channel_id` = ?, `show_members` = ? WHERE `id` = ?");
            statement.setString(1, this.getTitle());
            statement.setString(2, this.getDescription());
            statement.setString(3, this.getJoinInfo());
            statement.setString(4, this.getHeadmasterID());
            statement.setString(5, this.getMessageID());
            statement.setString(6, this.getChannelID());
            statement.setBoolean(7, this.isShowMembers());
            statement.setInt(8, this.getID());
            return statement.executeUpdate() != 0;
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }

        return false;
    }

    public int getID() {
        return id;
    }

    public String getDiscordID() {
        return discordID;
    }

    public Role getDiscordRole() {
        return this.getBot().getJDA().getRoleById(this.getDiscordID());
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

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public boolean isShowMembers() {
        return showMembers;
    }

    public void setShowMembers(boolean showMembers) {
        this.showMembers = showMembers;
    }

    public String getJoinInfo() {
        return joinInfo;
    }

    public void setJoinInfo(String joinInfo) {
        this.joinInfo = joinInfo;
    }

    public String getHeadmasterID() {
        return headmasterID;
    }

    public void setHeadmasterID(String headmasterID) {
        this.headmasterID = headmasterID;
    }

    public RestAction<Member> getHeadmaster() {
        return this.getDiscordRole().getGuild().retrieveMemberById(this.getHeadmasterID());
    }

    public Message getInfoMessage() {
        if (this.getMessageID() != null && this.getChannelID() != null) {
            TextChannel oldChannel = this.getBot().getJDA().getTextChannelById(this.getChannelID());
            if (oldChannel != null) return oldChannel.retrieveMessageById(this.getMessageID()).complete();
        }

        return null;
    }
}
