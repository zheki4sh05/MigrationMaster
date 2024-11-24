package org.example.migrations;

import org.example.command.*;
import org.example.entity.*;
import org.example.migrations.managers.*;
import org.example.migrations.utils.*;

import java.util.*;


public class MigrationTool {
    private final MigrationManager migrationManager;

    private final PropertiesUtil propertiesUtil;

    public MigrationTool(MigrationManager migrationManager, PropertiesUtil propertiesUtil) {
        this.migrationManager = migrationManager;
        this.propertiesUtil = propertiesUtil;

    }

    public void migrate(MigrationCommand migrationCommand){

        //propertiesUtil.readProperties(migrationCommand.getPropertiesPass());

        propertiesUtil.readProperties();

        migrationManager.execute(migrationCommand.getChangelogsPath());

    }


    public void status() {
        propertiesUtil.readProperties();
     List<Migration> migrationsList =  migrationManager.getMigrations();
     migrationsList.forEach(item-> System.out.println(item.toString()));
    }

    public void rollback() {
        propertiesUtil.readProperties();
         migrationManager.clearAll();
        migrationManager.drop();

    }
}
