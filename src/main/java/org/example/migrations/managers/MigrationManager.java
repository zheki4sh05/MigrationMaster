package org.example.migrations.managers;


import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.locking.*;
import org.example.migrations.readers.*;
import org.example.migrations.utils.*;

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
    private void callExecutor(HashMap<String, Resource> filesData){

//            for(Map.Entry<String,Resource> entry : filesData.entrySet()) {
//
//                Migration migration = migrationExecutor.execute(entry.getKey(), entry.getValue());
//                System.out.println(migration);
//
//
//
//                if (migration.getState().equals(State.FAILED.state())) {
//                    System.out.println("Migration failed!");
//                    return;
//                }
//
//            }

    }
    public void execute(String changelogsPath) {
        if(PropertiesUtil.getProperties().getRollbackAll()){
            executePessimistic(changelogsPath);
        }else{
            executeOptimistic(changelogsPath);
        }
    }
    private void executePessimistic(String changelogsPath){
//
//        HashMap<String, Resource> filesData;
//
//        filesData = migrationFileReader.readFilesFromFolder(changelogsPath);
//
//        if(filesData.isEmpty())
//            throw new MigrationFileException("Error: changelog directory is empty. No any migrations files found");
//
//        if(migrationExecutor.isHistoryExists()){
//
//            filesData= checkExistsMigrations(filesData);
//
////            List<Migration> migrationList = new ArrayList<>(migrationExecutor.getMigrations());
////
////            if(migrationList.size()==0){
////                throw new MigrationExecutionException("Error: no migrations rows founded, migrations table exists, but is empty!");
////            }
////
////            ChangelogMaster.checkSum(migrationList, filesData);
////
////            filesData = ChangelogMaster.processMigrations(migrationList, filesData);
////
////            if(filesData.isEmpty()){
////                System.out.println("Warning: all migrations are applied!");
////                return;
////            }
//
//        }else{
//            migrationExecutor.createMigrationTable();
//
//        }

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

//        if(filesData.isEmpty()){
//            System.out.println("Warning: all migrations are applied!");
//            return filesData;
//        }
        return filesData;
    }



}
