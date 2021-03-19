package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;

public class V1__Add_roles_table extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        PreparedStatement statement = context.getConnection().prepareStatement("CREATE TABLE `roles` (`id` INT NOT NULL AUTO_INCREMENT, `discord_id` VARCHAR(50) NOT NULL, `title` VARCHAR(500), `description` TEXT, `join_info` TEXT, `headmaster_id` VARCHAR(100), `message_id` VARCHAR(100), `show_members` TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        statement.executeUpdate();
    }
}
