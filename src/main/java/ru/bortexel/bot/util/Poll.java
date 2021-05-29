package ru.bortexel.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.bot.BortexelBot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Poll {
    private final BortexelBot bot;
    private final int id;
    private final String name;
    @Nullable private String title;
    @Nullable private String description;
    @Nullable private Timestamp endsAt;
    @Nullable private Message message;
    private boolean allowRevote;
    private boolean allowMultipleChoice;
    private boolean anonymous;

    private final @NotNull List<Variant> variants = new ArrayList<>();

    public Poll(int id, String name, BortexelBot bot) {
        this.bot = bot;
        this.id = id;
        this.name = name;
    }

    public static boolean isPoll(Message message) {
        if (message.getReactions().size() == 0) return false;
        if (message.getEmbeds().size() != 1) return false;
        MessageEmbed embed = message.getEmbeds().get(0);
        if (embed.getTitle() == null) return false;
        if (embed.getColor() == null) return false;
        if (!embed.getColor().equals(BortexelBot.EMBED_COLOR)) return false;
        return embed.getFields().size() != 0;
    }

    public static Poll getFromMessage(Message message, BortexelBot bot) throws SQLException {
        if (!isPoll(message)) return null;
        Connection connection = bot.getDatabase().getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM `polls` WHERE `message_id` = ?");
        statement.setString(1, message.getId());
        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next()) return null;
        Poll poll = new Poll(resultSet.getInt("id"), resultSet.getString("name"), bot);
        poll.setTitle(resultSet.getString("title"));
        poll.setDescription(resultSet.getString("description"));
        poll.setEndsAt(resultSet.getTimestamp("ends_at"));
        poll.setAllowRevote(resultSet.getBoolean("allow_revote"));
        poll.setMultipleChoiceAllowed(resultSet.getBoolean("allow_multiple_choice"));
        poll.setAnonymous(resultSet.getBoolean("is_anonymous"));
        poll.setMessage(message);

        poll.fetchVariants();
        return poll;
    }

    public static Poll create(String name, BortexelBot bot) throws SQLException {
        Connection connection = bot.getDatabase().getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO `polls` (`title`) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, name);
        int id = statement.executeUpdate();
        return new Poll(id, name, bot);
    }

    public void publish(TextChannel channel) throws SQLException {
        EmbedBuilder builder = this.getEmbed();
        Message message = channel.sendMessage(builder.build()).complete();
        for (Variant variant : this.getVariants()) message.addReaction(variant.getSign()).complete();
    }

    public void rerender() throws SQLException {
        EmbedBuilder builder = this.getEmbed();
        builder.setFooter(this.getEmbedFooter());
        if (this.getEndsAt() != null) builder.setTimestamp(this.getEndsAt().toInstant());
        if (this.getMessage() != null) this.getMessage().editMessage(builder.build()).queue();
    }

    private String getEmbedFooter() {
        int memberCount = this.getMemberCount();
        String plural = TextUtil.getPlural(memberCount, " человек принял ", " человека приняли ", " человек приняли ");
        return memberCount + plural + "участие" +
                (this.isAnonymous() ? " • " + "Анонимное голосование" : "") +
                (this.isMultipleChoiceAllowed() ? " • " + "Множественный выбор разрешён" : "") +
                (!this.isRevoteAllowed() ? " • " + "Изменить выбор нельзя" : "");
    }

    public EmbedBuilder getEmbed() throws SQLException {
        int totalVotes = this.getTotalVotes();
        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setTitle(this.getTitle());
        builder.setDescription(this.getDescription());

        for (Variant variant : this.getVariants()) {
            float percentage = totalVotes > 0 ? (float) Math.round((float) variant.getVotes().size() / (float) totalVotes * 1000) / 10 : 0;
            String name = variant.getSign() + " • " + variant.getName();
            String value = TextUtil.makeDefaultProgressBar(Math.round(percentage), 15) + " • " +
                    variant.getVotes() + " (" + percentage + "%)";
            builder.addField(name, value, false);
            builder.setFooter(this.getEmbedFooter());
        }

        return builder;
    }

    public int getTotalVotes() throws SQLException {
        int total = 0;
        for (Variant variant : this.getVariants()) total += variant.getVotes().size();
        return total;
    }

    public int getMemberCount() {
        if (this.getMessage() == null) return 0;
        List<String> members = new ArrayList<>();
        int total = 0;

        for (Variant variant : this.getVariants()) {
            List<User> currentMembers = variant.getReaction(this.getMessage()).retrieveUsers().complete();
            for (User currentMember : currentMembers) {
                if (members.contains(currentMember.getId())) continue;
                if (currentMember.isBot()) continue;
                members.add(currentMember.getId());
                total++;
            }
        }

        return total;
    }

    public @NotNull List<Variant> getVariants() {
        return variants;
    }

    public void fetchVariants() throws SQLException {
        List<Variant> variants = this.getVariants();
        variants.clear();
        Connection connection = this.getBot().getDatabase().getConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM `poll_variants` WHERE `poll_id` = ?");
        statement.setInt(1, this.getId());
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) variants.add(Variant.parseRow(resultSet, this));
    }

    public void save() throws SQLException {
        Connection connection = this.getBot().getDatabase().getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE `polls` SET " +
                "`description` = ?, `ends_at` = ?, `message_id` = ?, `channel_id` = ?, `guild_id` = ?, " +
                "`allow_revote` = ?, `allow_multiple_choice` = ?, `is_anonymous` = ? WHERE `id` = ?");

        statement.setString(1, this.getDescription());
        statement.setTimestamp(2, this.getEndsAt());

        if (this.getMessage() != null) {
            statement.setString(3, this.getMessage().getId());
            statement.setString(4, this.getMessage().getChannel().getId());
            statement.setString(5, this.getMessage().getGuild().getId());
        } else {
            statement.setString(3, null);
            statement.setString(4, null);
            statement.setString(5, null);
        }

        statement.setBoolean(6, this.isRevoteAllowed());
        statement.setBoolean(7, this.isMultipleChoiceAllowed());
        statement.setBoolean(8, this.isAnonymous());
        statement.setInt(9, this.getId());
    }

    public List<Vote> getVotes(User user) throws SQLException {
        List<Vote> votes = new ArrayList<>();
        Connection connection = this.getBot().getDatabase().getConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM `poll_votes` WHERE `member_id` = ?");
        statement.setString(1, user.getId());
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Variant variant = this.getVariant(resultSet.getInt("variant_id"));
            if (variant == null) continue;
            if (variant.getPoll().getId() != this.getId()) continue;
            votes.add(Vote.parseRow(resultSet, variant));
        }

        return votes;
    }

    public @Nullable Variant getVariant(int id) throws SQLException {
        return Variant.get(id, this);
    }

    public @Nullable Variant getVariant(String name) throws SQLException {
        return Variant.get(name, this);
    }

    public boolean isMultipleChoiceAllowed() {
        return this.allowMultipleChoice;
    }

    public void setMultipleChoiceAllowed(boolean multipleChoice) {
        this.allowMultipleChoice = multipleChoice;
    }

    public BortexelBot getBot() {
        return bot;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public @Nullable Timestamp getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(@Nullable Timestamp endsAt) {
        this.endsAt = endsAt;
    }

    public @Nullable Message getMessage() {
        return message;
    }

    public void setMessage(@Nullable Message message) {
        this.message = message;
    }

    public boolean isRevoteAllowed() {
        return allowRevote;
    }

    public void setAllowRevote(boolean allowRevote) {
        this.allowRevote = allowRevote;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public @Nullable String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    public static class Variant {
        private final int id;
        private final Poll poll;
        private final String name;
        private final String sign;
        private final String description;

        private final @NotNull List<Vote> votes = new ArrayList<>();
        private boolean votesFetched;

        public Variant(int id, Poll poll, String name, String sign, String description) {
            this.id = id;
            this.poll = poll;
            this.name = name;
            this.sign = sign;
            this.description = description;
        }

        public static @Nullable Variant get(int id, Poll poll) throws SQLException {
            Connection connection = poll.getBot().getDatabase().getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `poll_variants` WHERE `id` = ? AND `poll_id` = ?");
            statement.setInt(1, id);
            statement.setInt(2, poll.getId());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) return null;
            return parseRow(resultSet, poll);
        }

        public static @Nullable Variant get(String name, Poll poll) throws SQLException {
            Connection connection = poll.getBot().getDatabase().getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `poll_variants` WHERE `name` = ? AND `poll_id` = ?");
            statement.setString(1, name);
            statement.setInt(2, poll.getId());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) return null;
            return parseRow(resultSet, poll);
        }

        public static Variant create(Poll poll, String name, String sign, String description) throws SQLException {
            Connection connection = poll.getBot().getDatabase().getConnection();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO `poll_variants` " +
                    "(`poll_id`, `name`, `sign`, `description`) VALUES " +
                    "(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, poll.getId());
            statement.setString(2, name);
            statement.setString(3, sign);
            statement.setString(4, description);

            int id = statement.executeUpdate();
            return new Variant(id, poll, name, sign, description);
        }

        public static Variant parseRow(ResultSet resultSet, Poll poll) throws SQLException {
            return new Variant(
                    resultSet.getInt("id"),
                    poll,
                    resultSet.getString("name"),
                    resultSet.getString("title"),
                    resultSet.getString("description")
            );
        }

        public String getName() {
            return name;
        }

        public String getSign() {
            return sign;
        }

        public void fetchVotes() throws SQLException {
            List<Vote> votes = this.getVotes();
            votes.clear();

            Poll poll = this.getPoll();
            BortexelBot bot = poll.getBot();
            Connection connection = bot.getDatabase().getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `poll_votes` WHERE `variant_id` = ?");
            statement.setInt(1, this.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) votes.add(Vote.parseRow(resultSet, this));
        }

        public @Nullable Vote vote(User user) throws SQLException, Error {
            return Vote.create(this, user);
        }

        public MessageReaction getReaction(@Nullable Message message) {
            if (message == null) return null;
            if (!isPoll(message)) return null;
            for (MessageReaction reaction : message.getReactions())
                if (reaction.getReactionEmote().getEmoji().equals(this.getSign())) return reaction;
            return null;
        }

        public String getDescription() {
            return description;
        }

        public Poll getPoll() {
            return poll;
        }

        public int getId() {
            return id;
        }

        public @NotNull List<Vote> getVotes() throws SQLException {
            if (!this.isVotesFetched()) {
                this.fetchVotes();
                this.setVotesFetched(true);
            }

            return votes;
        }

        public boolean isVotesFetched() {
            return votesFetched;
        }

        public void setVotesFetched(boolean votesFetched) {
            this.votesFetched = votesFetched;
        }
    }

    public static class Vote {
        private final int id;
        @NotNull private final Poll poll;
        @NotNull private final Variant variant;
        @NotNull private final User member;
        @Nullable private final MessageReaction reaction;
        private final Timestamp createdAt;

        public Vote(int id, Variant variant, @NotNull User member, @Nullable MessageReaction reaction, Timestamp createdAt) {
            this.id = id;
            this.poll = variant.getPoll();
            this.variant = variant;
            this.member = member;
            this.reaction = reaction;
            this.createdAt = createdAt;
        }

        protected static Vote parseRow(ResultSet resultSet, Variant variant) throws SQLException {
            JDA jda = variant.getPoll().getBot().getJDA();
            User user = jda.getUserById(resultSet.getString("member_id"));

            Message message = variant.getPoll().getMessage();
            MessageReaction reaction = variant.getReaction(message);

            if (user == null) return null;
            return new Vote(
                    resultSet.getInt("id"),
                    variant, user, reaction,
                    resultSet.getTimestamp("created_at")
            );
        }

        public static Vote create(Variant variant, User user) throws SQLException, Error {
            Poll poll = variant.getPoll();

            if (poll.getEndsAt() != null && poll.getEndsAt().before(new Timestamp(System.currentTimeMillis())))
                throw new Error("Это голосование закончилось " + TimeUtil.getDefaultDateFormat().format(poll.getEndsAt()) +
                        " и Вы не можете больше принять в нём участие");

            List<Vote> votes = poll.getVotes(user);

            if (votes.size() > 0 && !poll.isRevoteAllowed()) {
                List<String> variants = new ArrayList<>();
                votes.forEach(vote -> variants.add(vote.getVariant().getDescription()));
                throw new Error("Вы уже проголосовали за " + String.join(", ", variants) +
                        " и не можете изменить свой выбор из-за настроек голосования");
            }

            if (!poll.isMultipleChoiceAllowed()) for (Vote vote : votes) vote.delete();

            Connection connection = poll.getBot().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `poll_votes` " +
                    "(`variant_id`, `member_id`) VALUES " +
                    "(?, ?)", Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, variant.getId());
            statement.setString(2, user.getId());
            int id = statement.executeUpdate();

            return new Vote(id, variant, user, null, new Timestamp(System.currentTimeMillis()));
        }

        public void delete() throws SQLException {
            Connection connection = this.getPoll().getBot().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `poll_votes` WHERE `id` = ?");
            statement.setInt(1, this.getId());
            statement.executeUpdate();

            MessageReaction reaction = this.getReaction();
            if (reaction == null) return;
            reaction.removeReaction(this.getMember()).queue();
        }

        public int getId() {
            return id;
        }

        public @NotNull Poll getPoll() {
            return poll;
        }

        public @NotNull Variant getVariant() {
            return variant;
        }

        public @NotNull User getMember() {
            return member;
        }

        public Timestamp getCreatedAt() {
            return createdAt;
        }

        public @Nullable MessageReaction getReaction() {
            return reaction;
        }
    }

    public static class Error extends Exception {
        public Error(String message) {
            super(message);
        }
    }
}
