package ru.bortexel.bot.core;

import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private Connection connection;

    public Database(String host, String user, String password, String database) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public static Database setup() throws Exception {
        String host = System.getenv("DB_HOST");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        String database = System.getenv("DB_DATABASE");

        if (host == null || user == null || password == null || database == null) {
            throw new Exception("Some of database connection parameters weren't found in environment.");
        }

        Flyway flyway = Flyway.configure().dataSource(getConnectionURL(host, database), user, password).load();
        flyway.migrate();

        return new Database(host, user, password, database);
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || !connection.isValid(1))
            connection = DriverManager.getConnection(getConnectionURL(host, database), user, password);
        return connection;
    }

    private static String getConnectionURL(String host, String database) {
        return "jdbc:mysql://" + host + "/" + database + "?verifyServerCertificate=false&useSSL=true&serverTimezone=Europe/Moscow";
    }
}
