package org.example.settings;

import lombok.*;

@Data
public class BaseSettings {
    public final static String baseFolder="changelogs";
    public final static String versionAlias = "V";
    public final static String appName = "migration";
    public final static String baseConfigFileName = "application.properties";
    public final static String fileMigrationExtension=".sql";
    public final static String nameVersionPatter="V(\\d+)";
    public final static String tableName ="migrations";
    public final static String tableLockName="migration_lock";
}
