package org.example.migrations.managers;


import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.readers.*;
import org.example.migrations.utils.*;

import java.sql.*;
import java.util.*;

public class MigrationManager {

    private final MigrationFileReader migrationFileReader;

    private final MigrationExecutor migrationExecutor;


    public MigrationManager(MigrationFileReader migrationFileReader, MigrationExecutor migrationExecutor) {
        this.migrationFileReader = migrationFileReader;
        this.migrationExecutor = migrationExecutor;
    }

    public void execute(Connection connection, String changelogsPath) {

        HashMap<String, String> filesData = migrationFileReader.readFilesFromFolder(changelogsPath);

        if(filesData.isEmpty())
            throw new MigrationFileException("Error: changelog directory is empty. No any migrations files found");


        if(migrationExecutor.isHistoryExists()){
            List<Migration> migrationList = new ArrayList<>(migrationExecutor.getMigrations());

            HashMap<String, String> updateData = processMigrations(migrationList);

           for(Map.Entry<String,String> entry : updateData.entrySet()){
                Migration migration = migrationExecutor.execute(entry.getKey(), entry.getValue());



           }

        }else{
            migrationExecutor.createMigrationTable();
        }

    }

    private HashMap<String, String> processMigrations(List<Migration> migrationList) {
    }


}
