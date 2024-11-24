package org.example.input.impl;

import org.example.command.*;
import org.example.input.interfaces.*;
import org.example.migrations.*;

public class MigrationCommandReader implements ConsoleInput, MasterMigrationApi, RemoteInput {

    private final MigrationTool migrationTool;
    public MigrationCommandReader(MigrationTool migrationTool) {
        this.migrationTool = migrationTool;
    }

    @Override
    public void read(MigrationCommand migrationCommand) {
        start(migrationCommand);
//        stop();
    }

    @Override
    public void status() {
        migrationTool.status();
    }

    @Override
    public void rollback() {
        migrationTool.rollback();
    }

    private void start(MigrationCommand migrationCommand){
        migrationTool.migrate(migrationCommand);
    }

//    private void stop(){
//        ConnectionManager.stopConnection();
//    }
}
