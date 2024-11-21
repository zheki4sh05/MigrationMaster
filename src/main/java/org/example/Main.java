package org.example;

public class Main {


    public static void main(String[] args) {

        MasterMigration.migrate("C:/java projects/MigrationMaster/src/main/resources/application.properties",
                "C:/java projects/MigrationMaster/src/main/resources/changelogs");
    }
}