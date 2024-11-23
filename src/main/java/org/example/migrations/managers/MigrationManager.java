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

        if(PropertiesUtil.getProperties().getRollbackAll()){



        }else{
            for(Map.Entry<String,Resource> entry : filesData.entrySet()){

                Migration migration = migrationExecutor.execute(entry.getKey(), entry.getValue());
                System.out.println(migration);

                if(migration.getState().equals(State.FAILED.state())){
                    System.out.println("Migration failed!");
                    return;
                }

            }
        }




    }
    public void execute(String changelogsPath) {

        if(PropertiesUtil.getProperties().getRollbackAll()){

            executePessimistic(changelogsPath);

        }else{

        }


        LinkedHashMap<String, Resource> filesData = migrationFileReader.readFilesFromFolder(changelogsPath);

        if(filesData.isEmpty())
            throw new MigrationFileException("Error: changelog directory is empty. No any migrations files found");

        if(migrationExecutor.isHistoryExists()){

            List<Migration> migrationList = new ArrayList<>(migrationExecutor.getMigrations());

            if(migrationList.size()==0){
                throw new MigrationExecutionException("Error: no migrations rows founded, migrations table exists, but is empty!");
            }

            ChangelogMaster.checkSum(migrationList, filesData);
//            checkSum(migrationList, filesData);

            HashMap<String, Resource> updateData = ChangelogMaster.processMigrations(migrationList, filesData);

            if(updateData.isEmpty()){
                System.out.println("Warning: all migrations are applied!");
                return;
            }

            callExecutor(updateData);

        }else{
            migrationExecutor.createMigrationTable();
            callExecutor(filesData);
        }

    }

//    private Boolean hasMigration(String name, List<Migration> migrationList){
//
//
//        Optional<Migration> result = migrationList
//                .stream()
//                .filter(item -> item.getScript().equals(name)).findAny();
//        return  !result.isEmpty();
//    }
//
//    private HashMap<String, Resource> processMigrations(List<Migration> migrationList, HashMap<String, Resource> filesData) {
//
//        HashMap<String, Resource> hashMap = new LinkedHashMap<>();
//
//        for(Map.Entry<String,Resource> entry : filesData.entrySet()) {
//            if(!hasMigration(entry.getKey(), migrationList)){
//                hashMap.put(entry.getKey(), entry.getValue());
//            }
//        }
//
//        return hashMap;
//    }

//    private void checkSum(List<Migration> migrationList, HashMap<String, Resource> filesData){
//
//        for(Migration item :migrationList){
//            if(!filesData.get(item.getScript()).getChecksum().equals(item.getChecksum())){
//
//                throw new ChecksumException("Migration script "+item.getScript() + " has incorrect checksum!");
//
//            }
//        }
//
//
//    }


    private void executePessimistic(String changelogsPath){

        LinkedHashMap<String, Resource> filesData = migrationFileReader.readFilesFromFolder(changelogsPath);

        if(filesData.isEmpty())
            throw new MigrationFileException("Error: changelog directory is empty. No any migrations files found");

        if(migrationExecutor.isHistoryExists()){

            List<Migration> migrationList = new ArrayList<>(migrationExecutor.getMigrations());

            if(migrationList.size()==0){
                throw new MigrationExecutionException("Error: no migrations rows founded, migrations table exists, but is empty!");
            }

            ChangelogMaster.checkSum(migrationList, filesData);
//            checkSum(migrationList, filesData);

            HashMap<String, Resource> updateData = ChangelogMaster.processMigrations(migrationList, filesData);

            if(updateData.isEmpty()){
                System.out.println("Warning: all migrations are applied!");
                return;
            }

//            callExecutor(updateData);

            Function<HashMap<String, Resource>, List<Migration>> func = migrationExecutor::executeAll;
            lockingManager.executeMigrates(func, updateData);


        }else{
            migrationExecutor.createMigrationTable();
            callExecutor(filesData);
        }

    }
    private void executeOptimistic(){

    }



}
