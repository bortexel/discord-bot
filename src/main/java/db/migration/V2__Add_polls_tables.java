package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;

public class V2__Add_polls_tables extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        PreparedStatement statement = context.getConnection().prepareStatement("CREATE TABLE `polls` (" +
                "`id` INT NOT NULL AUTO_INCREMENT, " +
                "`name` VARCHAR(50), " +
                "`title` VARCHAR(200) DEFAULT NULL, " +
                "`description` TEXT DEFAULT NULL, " +
                "`ends_at` TIMESTAMP DEFAULT NULL, " +
                "`message_id` VARCHAR(100) DEFAULT NULL, " +
                "`channel_id` VARCHAR(100) DEFAULT NULL, " +
                "`guild_id` VARCHAR(100) DEFAULT NULL, " +
                "`allow_revote` TINYINT NOT NULL DEFAULT 0, " +
                "`allow_multiple_choice` TINYINT NOT NULL DEFAULT 0, " +
                "`is_anonymous` TINYINT NOT NULL DEFAULT 0, " +
                "PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        statement.executeUpdate();

        statement = context.getConnection().prepareStatement("CREATE TABLE `poll_variants` (" +
                "`id` INT NOT NULL AUTO_INCREMENT, " +
                "`poll_id` INT NOT NULL, " +
                "`name` VARCHAR(50), " +
                "`sign` VARCHAR(50), " +
                "`title` VARCHAR(100), " +
                "`description` TEXT, " +
                "PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        statement.executeUpdate();

        statement = context.getConnection().prepareStatement("CREATE TABLE `poll_votes` (" +
                "`id` INT NOT NULL AUTO_INCREMENT, " +
                "`variant_id` INT NOT NULL, " +
                "`member_id` VARCHAR(50) NOT NULL , " +
                "`reaction_id` VARCHAR(50) DEFAULT NULL, " +
                "`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        statement.executeUpdate();
    }
}
