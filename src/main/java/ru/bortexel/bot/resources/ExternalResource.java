package ru.bortexel.bot.resources;

import net.dv8tion.jda.api.entities.Message;
import ru.bortexel.bot.BortexelBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExternalResource {
    private final int id;
    private final ResourceType resourceType;
    private final int resourceID;
    private final String channelID;
    private final String messageID;
    private final BortexelBot bot;

    public ExternalResource(int id, ResourceType resourceType, int resourceID, String channelID, String messageID, BortexelBot bot) {
        this.id = id;
        this.resourceType = resourceType;
        this.resourceID = resourceID;
        this.channelID = channelID;
        this.messageID = messageID;
        this.bot = bot;
    }

    public static ExternalResource getByID(int id, BortexelBot bot) {
        try {
            Connection connection = bot.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `resources` WHERE `id` = ?");
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            return getFromResult(resultSet, bot);
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }

        return null;
    }

    public static ExternalResource register(ResourceType resourceType, int resourceID, Message message, BortexelBot bot) {
        try {
            Connection connection = bot.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `resources` (`resource_type`, `resource_id`, `channel_id`, `message_id`) VALUES (?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, resourceType.getValue());
            statement.setInt(2, resourceID);
            statement.setString(3, message.getChannel().getId());
            statement.setString(4, message.getId());

            int id = statement.executeUpdate();
            return getByID(id, bot);
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }

        return null;
    }

    private static ExternalResource getFromResult(ResultSet resultSet, BortexelBot bot) throws SQLException {
        int id = resultSet.getInt("id");
        ResourceType resourceType = ResourceType.fromString(resultSet.getString("resource_type"));
        int resourceID = resultSet.getInt("resource_id");
        String channelID = resultSet.getString("channel_id");
        String messageID = resultSet.getString("message_id");

        return new ExternalResource(id, resourceType, resourceID, channelID, messageID, bot);
    }

    public void unregister() {
        try {
            Connection connection = this.getBot().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `resources` WHERE `id` = ?");
            statement.setInt(1, this.getID());
            statement.executeUpdate();
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }
    }

    public int getID() {
        return id;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public int getResourceID() {
        return resourceID;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getMessageID() {
        return messageID;
    }

    public BortexelBot getBot() {
        return bot;
    }
}
