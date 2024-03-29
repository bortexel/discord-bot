package ru.bortexel.bot;

import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bortexel.bot.commands.economy.EconomyCommandGroup;
import ru.bortexel.bot.commands.info.InfoCommandGroup;
import ru.bortexel.bot.commands.main.MainCommandGroup;
import ru.bortexel.bot.commands.roles.RoleCommandGroup;
import ru.bortexel.bot.commands.staff.StaffCommandGroup;
import ru.bortexel.bot.commands.stuff.StuffCommandGroup;
import ru.bortexel.bot.core.*;
import ru.bortexel.bot.listeners.ButtonClickListener;
import ru.bortexel.bot.listeners.GuildJoinListener;
import ru.bortexel.bot.listeners.GuildListener;
import ru.bortexel.bot.listeners.RoleUpdateListener;
import ru.bortexel.bot.listeners.bortexel.*;
import ru.bortexel.bot.tasks.InteractionCleanup;
import ru.bortexel.bot.util.poll.PollReactionListener;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.listening.BroadcastingServer;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BortexelBot {
    private static final Logger logger = LoggerFactory.getLogger("BortexelBot");
    private static BortexelBot instance;
    public static final String COMMAND_PREFIX = "$";
    public static final Color EMBED_COLOR = Color.decode("#FFB114");

    private final JDA jda;
    private final Bortexel4J api;
    private final Database database;
    private String mainGuildID;
    private boolean shouldRegisterCommands;
    private final Timer timer = new Timer();
    private final ConcurrentHashMap<UUID, InteractionContext> interactions = new ConcurrentHashMap<>();
    private final HashMap<String, Command> commands = new HashMap<>();
    private final List<CommandGroup> commandGroups = new ArrayList<>();
    private final BroadcastingServer broadcastingServer;

    public BortexelBot(JDA jda, Bortexel4J api, Database database, BroadcastingServer broadcastingServer) {
        this.jda = jda;
        this.api = api;
        this.database = database;
        this.broadcastingServer = broadcastingServer;
    }

    public static BortexelBot getInstance() {
        if (instance == null) throw new IllegalStateException("Bot has not initialized yet");
        return instance;
    }

    public static void main(String[] args) {
        String token = System.getenv("BOT_TOKEN");
        String apiToken = System.getenv("API_TOKEN");
        String sentryDsn = System.getenv("SENTRY_DSN");
        String apiUrl = System.getenv("API_URL");
        String bcsUrl = System.getenv("BCS_URL");
        String mainGuildId = System.getenv("MAIN_GUILD_ID");

        if (bcsUrl == null) bcsUrl = "wss://bcs.bortexel.ru/v1/websocket";
        if (mainGuildId == null) mainGuildId = "";
        if (sentryDsn != null) Sentry.init(sentryOptions -> sentryOptions.setDsn(sentryDsn));

        Database database;

        try {
            database = Database.setup();
        } catch (Exception exception) {
            BortexelBot.handleException(exception);
            exception.printStackTrace();
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setStatus(OnlineStatus.IDLE);
        builder.setActivity(Activity.playing("/help"));
        builder.addEventListeners(new PollReactionListener());
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        try {
            JDA jda = builder.build();

            Bortexel4J client = Bortexel4J.login(apiToken, apiUrl);
            BroadcastingServer broadcastingServer = client.getBroadcastingServer(bcsUrl);
            broadcastingServer.setName("BortexelBot");

            BortexelBot bot = new BortexelBot(jda, client, database, broadcastingServer);
            bot.setMainGuildID(mainGuildId);
            bot.setShouldRegisterCommands(System.getenv("DISABLE_SLASH_COMMANDS") == null);
            bot.run();
        } catch (Exception e) {
            Sentry.captureException(e);
            logger.error("Error while initializing bot", e);
        }
    }

    public void run() {
        this.registerCommandGroup(new MainCommandGroup(this));
        this.registerCommandGroup(new EconomyCommandGroup(this));
        this.registerCommandGroup(new InfoCommandGroup(this));
        this.registerCommandGroup(new RoleCommandGroup(this));
        this.registerCommandGroup(new StaffCommandGroup(this));
        this.registerCommandGroup(new StuffCommandGroup(this));

        this.registerGlobalSlashCommands();

        jda.addEventListener(new CommandListener(this));
        jda.addEventListener(new ButtonClickListener(this));
        jda.addEventListener(new RoleUpdateListener(this));
        jda.addEventListener(new GuildListener(this));
        jda.addEventListener(new GuildJoinListener(this));

        this.getBroadcastingServer().registerListener(new BanListener(this));
        this.getBroadcastingServer().registerListener(new UserListener(this));
        this.getBroadcastingServer().registerListener(new WarningListener(this));
        this.getBroadcastingServer().registerListener(new ProjectListener(this));
        this.getBroadcastingServer().registerListener(new DiscordLinkListener(this));
        this.getBroadcastingServer().registerListener(new WhitelistFormListener(this));
        this.getBroadcastingServer().connect();

        this.getTimer().schedule(new InteractionCleanup(), 60000L, 60000L);

        instance = this;
    }

    public static void handleException(Throwable throwable) {
        logger.error("Handling error", throwable);
        Sentry.captureException(throwable);
    }

    private void registerCommandGroup(CommandGroup provider) {
        this.commandGroups.add(provider);
        for (Command command : provider.getCommands()) {
            // Register command using its name
            this.commands.put(command.getName(), command);

            // Register command using aliases
            String[] aliases = command.getAliases();
            if (aliases.length > 0) for (String alias : aliases) this.commands.put(alias, command);
        }
    }

    private void registerGlobalSlashCommands() {
        List<CommandData> slashCommands = new ArrayList<>();
        if (this.shouldRegisterCommands()) for (CommandGroup commandGroup : this.getCommandGroups()) {
            for (Command command : commandGroup.getCommands()) {
                if (!command.isGlobal() || command.getSlashCommandData() == null) continue;
                slashCommands.add(command.getSlashCommandData());
            }
        }

        CommandListUpdateAction commands = this.getJDA().updateCommands();
        commands.addCommands(slashCommands).queue();
    }

    public UUID registerInteraction(CompletableFuture<Message> supplier) {
        UUID uuid = UUID.randomUUID();
        this.interactions.put(uuid, new InteractionContext(supplier));
        return uuid;
    }

    public Optional<InteractionContext> consumeInteraction(UUID uuid) {
        InteractionContext context = this.interactions.get(uuid);
        if (context == null) return Optional.empty();
        return Optional.of(this.interactions.remove(uuid));
    }

    public ConcurrentHashMap<UUID, InteractionContext> getInteractions() {
        return this.interactions;
    }

    public Command getCommand(String label) {
        return this.commands.get(label);
    }

    public List<CommandGroup> getCommandGroups() {
        return this.commandGroups;
    }

    public JDA getJDA() {
        return jda;
    }

    public Bortexel4J getApiClient() {
        return api;
    }

    public Database getDatabase() {
        return database;
    }

    public BroadcastingServer getBroadcastingServer() {
        return broadcastingServer;
    }

    public Guild getMainGuild() {
        return this.getJDA().getGuildById(this.getMainGuildID());
    }

    public String getMainGuildID() {
        return mainGuildID;
    }

    public void setMainGuildID(String mainGuildID) {
        this.mainGuildID = mainGuildID;
    }

    public boolean shouldRegisterCommands() {
        return shouldRegisterCommands;
    }

    public void setShouldRegisterCommands(boolean shouldRegisterCommands) {
        this.shouldRegisterCommands = shouldRegisterCommands;
    }

    public Timer getTimer() {
        return timer;
    }
}
