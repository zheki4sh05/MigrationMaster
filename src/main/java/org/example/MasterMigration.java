package org.example;

import org.example.command.*;
import org.example.factory.*;
import org.example.input.impl.*;
import org.example.input.interfaces.*;

import static org.example.settings.BaseSettings.baseConfigFileName;

public class MasterMigration {
    public static void migrate(String propertiesPass,String changelogsPath){

        MigrationCommand migrationCommand = new MigrationCommand(propertiesPass, changelogsPath);

        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();
        masterMigrationApi.read(migrationCommand);

    }

    public static void migrate(String changelogsPath){

        MigrationCommand migrationCommand = new MigrationCommand(baseConfigFileName, changelogsPath);

        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();

        masterMigrationApi.read(migrationCommand);

    }

    public static void status() {
        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();
        masterMigrationApi.status();
    }

    public static void rollback() {
        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();
        masterMigrationApi.rollback();
    }
}
