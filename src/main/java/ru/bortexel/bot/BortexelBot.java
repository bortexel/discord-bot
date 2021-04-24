package ru.bortexel.bot;

import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import ru.bortexel.bot.commands.economy.EconomyCommandProvider;
import ru.bortexel.bot.commands.main.MainCommandProvider;
import ru.bortexel.bot.commands.roles.RoleCommandProvider;
import ru.bortexel.bot.commands.info.InfoCommandProvider;
import ru.bortexel.bot.commands.staff.StaffCommandProvider;
import ru.bortexel.bot.commands.stuff.StuffCommandProvider;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandListener;
import ru.bortexel.bot.core.CommandProvider;
import ru.bortexel.bot.core.Database;
import ru.bortexel.bot.listeners.GuildListener;
import ru.bortexel.bot.listeners.RoleUpdateListener;
import ru.bortexel.bot.listeners.bortexel.BanListener;
import ru.bortexel.bot.listeners.bortexel.WarningListener;
import ru.bortexel.bot.util.AccessLevels;
import ru.bortexel.bot.util.poll.PollReactionListener;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.listening.BroadcastingServer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BortexelBot {
    public static final String COMMAND_PREFIX = "$";
    public static final Color EMBED_COLOR = Color.decode("#FFB114");

    private final JDA jda;
    private final Bortexel4J api;
    private final Database database;
    private final String mainGuildID;
    private AccessLevels accessLevels;
    private final HashMap<String, Command> commands = new HashMap<>();
    private final List<CommandProvider> commandProviders = new ArrayList<>();
    private final BroadcastingServer broadcastingServer;

    public BortexelBot(JDA jda, Bortexel4J api, Database database, String mainGuildID, BroadcastingServer broadcastingServer) {
        this.jda = jda;
        this.api = api;
        this.database = database;
        this.mainGuildID = mainGuildID;
        this.broadcastingServer = broadcastingServer;
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
        builder.addEventListeners(new PollReactionListener());
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        try {
            JDA jda = builder.build();

            Bortexel4J client = Bortexel4J.login(apiToken);
            if (apiUrl != null) client.setApiUrl(apiUrl);
            BroadcastingServer broadcastingServer = client.getBroadcastingServer(bcsUrl);

            new BortexelBot(jda, client, database, mainGuildId, broadcastingServer).run();
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }

    public void run() {
        this.accessLevels = new AccessLevels(this);

        this.registerCommandProvider(new MainCommandProvider(this));
        this.registerCommandProvider(new EconomyCommandProvider(this));
        this.registerCommandProvider(new InfoCommandProvider(this));
        this.registerCommandProvider(new RoleCommandProvider(this));
        this.registerCommandProvider(new StaffCommandProvider(this));
        this.registerCommandProvider(new StuffCommandProvider(this));

        jda.addEventListener(new CommandListener(this));
        jda.addEventListener(new RoleUpdateListener(this));
        jda.addEventListener(new GuildListener(this));

        this.getBroadcastingServer().registerListener(new BanListener(this));
        this.getBroadcastingServer().registerListener(new WarningListener(this));
        this.getBroadcastingServer().connect();
    }

    public static void handleException(Throwable throwable) {
        Sentry.captureException(throwable);
    }

    private void registerCommandProvider(CommandProvider provider) {
        this.commandProviders.add(provider);
        for (Command command : provider.getCommands()) {
            // Register command using its name
            this.commands.put(command.getName(), command);

            // Register command using aliases
            String[] aliases = command.getAliases();
            if (aliases.length > 0) for (String alias : aliases) this.commands.put(alias, command);
        }
    }

    public Command getCommand(String label) {
        return this.commands.get(label);
    }

    public List<CommandProvider> getCommandProviders() {
        return this.commandProviders;
    }

    public JDA getJDA() {
        return jda;
    }

    public Bortexel4J getApiClient() {
        return api;
    }

    public AccessLevels getAccessLevels() {
        return this.accessLevels;
    }

    public Database getDatabase() {
        return database;
    }

    public BroadcastingServer getBroadcastingServer() {
        return broadcastingServer;
    }

    public String getMainGuildID() {
        return mainGuildID;
    }
}
