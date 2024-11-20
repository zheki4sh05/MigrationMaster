package org.example.factory;

import org.example.migrations.managers.*;

public class ManagersFactory {
//    public static ConnectionManager getConnectionManager(){
//        return new ConnectionManager();
//    }
    public static MigrationManager getMigrationManager(){
        return new MigrationManager(ReaderFactory.createFileReader(), UtilFactory.createMigrationExecutor());
    }
}
