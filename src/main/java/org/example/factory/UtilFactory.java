package org.example.factory;

import org.example.migrations.utils.*;

public class UtilFactory {
    public static PropertiesUtil createUtilProperties(){
        return new PropertiesUtil();
    }

    public static MigrationExecutor createMigrationExecutor(){
        return new MigrationExecutor();
    }
}
