package org.example.migrations;

import org.example.command.*;
import org.example.migrations.managers.*;
import org.example.migrations.readers.*;
import org.example.migrations.utils.*;

import java.sql.*;

public class MigrationTool {

    private final MigrationManager migrationManager;



    private final PropertiesUtil propertiesUtil;

    public MigrationTool(MigrationManager migrationManager, PropertiesUtil propertiesUtil) {
        this.migrationManager = migrationManager;
        this.propertiesUtil = propertiesUtil;

    }

    public void migrate(MigrationCommand migrationCommand){

         UserProperties properties = propertiesUtil.readProperties(migrationCommand.getPropertiesPass());

         properties.checkProperties(properties);

         migrationManager.execute(migrationCommand.getChangelogsPath(), properties);

    }


}
