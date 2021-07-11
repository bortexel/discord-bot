package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;

public class V2__Add_external_resources_table extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        PreparedStatement statement = context.getConnection().prepareStatement("CREATE TABLE `resources` (`id` INT NOT NULL AUTO_INCREMENT, `resource_type` VARCHAR(50) NOT NULL, `resource_id` INT NOT NULL, `channel_id` VARCHAR(50) NOT NULL, `message_id` VARCHAR(50) NOT NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        statement.executeUpdate();
    }
}
