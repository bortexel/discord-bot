package ru.bortexel.bot;

import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import ru.bortexel.bot.commands.economy.EconomyCommandProvider;
import ru.bortexel.bot.commands.main.MainCommandProvider;
import ru.bortexel.bot.commands.roles.RoleCommandProvider;
import ru.bortexel.bot.commands.rules.RulesCommandProvider;
import ru.bortexel.bot.commands.stuff.StuffCommandProvider;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.CommandListener;
import ru.bortexel.bot.core.CommandProvider;
import ru.bortexel.bot.core.Database;
import ru.bortexel.bot.util.AccessLevels;
import ru.bortexel.bot.util.poll.PollReactionListener;
import ru.ruscalworld.bortexel4j.Bortexel4J;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BortexelBot {
    public static final String COMMAND_PREFIX = "$";
    public static final Color EMBED_COLOR = Color.decode("#FFB114");

    private final JDA jda;
    private final Bortexel4J api;
    private final Database database;
    private AccessLevels accessLevels;
    private final HashMap<String, Command> commands = new HashMap<>();
    private final List<CommandProvider> commandProviders = new ArrayList<>();

    public BortexelBot(JDA jda, Bortexel4J api, Database database) {
        this.jda = jda;
        this.api = api;
        this.database = database;
    }

    public static void main(String[] args) {
        String token = System.getenv("BOT_TOKEN");
        String apiToken = System.getenv("API_TOKEN");
        String sentryDsn = System.getenv("SENTRY_DSN");
        String apiUrl = System.getenv("API_URL");

        if (sentryDsn != null) {
            Sentry.init(sentryOptions -> sentryOptions.setDsn(sentryDsn));
        }

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

        try {
            JDA jda = builder.build();

            Bortexel4J client = Bortexel4J.login(apiToken);
            if (apiUrl != null) client.setApiUrl(apiUrl);

            new BortexelBot(jda, client, database).run();
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }

    public void run() {
        this.accessLevels = new AccessLevels(this);
        this.registerCommandProvider(new MainCommandProvider(this));
        this.registerCommandProvider(new EconomyCommandProvider(this));
        this.registerCommandProvider(new RulesCommandProvider(this));
        this.registerCommandProvider(new RoleCommandProvider(this));
        this.registerCommandProvider(new StuffCommandProvider(this));
        jda.addEventListener(new CommandListener(this));
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

    public JDA getJda() {
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
}
