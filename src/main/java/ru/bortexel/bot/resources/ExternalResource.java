package ru.bortexel.bot.resources;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import ru.bortexel.bot.BortexelBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

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

    public static Optional<ExternalResource> getByID(int id, BortexelBot bot) {
        try {
            Connection connection = bot.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `resources` WHERE `id` = ?");
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            return Optional.of(getFromResult(resultSet, bot));
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }

        return Optional.empty();
    }

    public static Optional<ExternalResource> getByExternalID(int id, ResourceType resourceType, BortexelBot bot) {
        try {
            Connection connection = bot.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `resources` WHERE `resource_id` = ? AND `resource_type` = ?");
            statement.setInt(1, id);
            statement.setString(2, resourceType.getValue());

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            return Optional.of(getFromResult(resultSet, bot));
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }

        return Optional.empty();
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
            return getByID(id, bot).orElseThrow(() -> new RuntimeException("External " + resourceType + "#" + resourceID + " was registered, but failed to retrieve it"));
        } catch (SQLException exception) {
            BortexelBot.handleException(exception);
        }

        throw new RuntimeException("Failed to register " + resourceType);
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

    public RestAction<Message> retrieveMessage() {
        TextChannel channel = this.getBot().getJDA().getTextChannelById(this.getChannelID());
        if (channel == null) return null;
        return channel.retrieveMessageById(this.getMessageID());
    }

    public BortexelBot getBot() {
        return bot;
    }
}
