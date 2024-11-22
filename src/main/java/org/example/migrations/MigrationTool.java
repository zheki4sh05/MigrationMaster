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

        propertiesUtil.readProperties(migrationCommand.getPropertiesPass());

         migrationManager.execute(migrationCommand.getChangelogsPath());

    }


}
