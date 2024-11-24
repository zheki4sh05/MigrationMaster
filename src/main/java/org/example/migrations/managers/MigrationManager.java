package org.example.migrations.managers;


import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.locking.*;
import org.example.migrations.readers.*;
import org.example.migrations.utils.*;

import java.sql.*;
import java.util.*;
import java.util.function.*;

public class MigrationManager {

    private final MigrationFileReader migrationFileReader;

    private final MigrationExecutor migrationExecutor;

    private final LockingManager lockingManager = new LockingManager();


    public MigrationManager(MigrationFileReader migrationFileReader, MigrationExecutor migrationExecutor) {
        this.migrationFileReader = migrationFileReader;
        this.migrationExecutor = migrationExecutor;
    }
    public void execute(String changelogsPath) {
        if(PropertiesUtil.getProperties().getRollbackAll()){
            executePessimistic(changelogsPath);
        }else{
            executeOptimistic(changelogsPath);
        }
    }
    private void executePessimistic(String changelogsPath){


        Function<HashMap<String, Resource>, List<Migration>> func = migrationExecutor::executeAll;
        lockingManager.executeMigrates(func, initMigrations(changelogsPath));

        System.out.println(lockingManager.migrations());

    }


    private void executeOptimistic(String changelogsPath){
        while(true){
            var map  = initMigrations(changelogsPath);

            if(map.isEmpty())
                break;

            Function<HashMap<String, Resource>, List<Migration>> func = migrationExecutor::execute;
            lockingManager.executeMigrates(func, map);
        }
    }

    private HashMap<String, Resource> initMigrations(String changelogsPath){
        HashMap<String, Resource> filesData;
        filesData = migrationFileReader.readFilesFromFolder(changelogsPath);
        if(filesData.isEmpty())
            throw new MigrationFileException("Error: changelog directory is empty. No any migrations files found");
        if(migrationExecutor.isHistoryExists()){
            filesData= checkExistsMigrations(filesData);
        }else{
            migrationExecutor.createMigrationTable();
        }
        return filesData;
    }

    private HashMap<String, Resource> checkExistsMigrations(HashMap<String, Resource> filesData){
        List<Migration> migrationList = new ArrayList<>(migrationExecutor.getMigrations());

        if(migrationList.size()==0){
            throw new MigrationExecutionException("Error: no migrations rows founded, migrations table exists, but is empty!");
        }

        ChangelogMaster.checkSum(migrationList, filesData);

        filesData = ChangelogMaster.processMigrations(migrationList, filesData);

        return filesData;
    }


    public List<Migration> getMigrations() {
        if(migrationExecutor.isHistoryExists()){
            List<Migration> migrationList = migrationExecutor.getMigrations();
            migrationFileReader.save(migrationList);
            return migrationList;
        }else{
           return new ArrayList<>();
        }
    }

    public void clearAll() {
        if(migrationExecutor.isHistoryExists()) {
            List<Migration> migrationList = migrationExecutor.getMigrations();
            try {
                for (Migration migration : migrationList) {
                    migrationExecutor.deleteMigration(migration);
                }
            } catch (SQLException e) {
                throw new MigrationExecutionException("Error: unable to delete!");
            }
        }
    }

    public void drop() {

        migrationExecutor.drop();

    }
}
