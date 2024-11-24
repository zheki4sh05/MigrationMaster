package org.example;
import org.example.command.*;
public class Main {
    public static void main(String[] args) {
//        MigrationClient.read(args);
//
        MasterMigration.migrate("C:/java projects/MigrationMaster/src/main/resources/changelogs");
    }
}