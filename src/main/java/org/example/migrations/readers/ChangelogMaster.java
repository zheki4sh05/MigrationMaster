package org.example.migrations.readers;

import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.utils.*;

import java.util.*;
import java.util.regex.*;

import static org.example.settings.BaseSettings.nameVersionPatter;

public class ChangelogMaster {

    public static Boolean isCorrect(String fileName){
        Pattern pattern = Pattern.compile(nameVersionPatter);
        Matcher matcher =pattern.matcher(fileName);
        return matcher.find();
    }

    public static Integer getNumber(String fileName){
        Pattern pattern = Pattern.compile(nameVersionPatter);
        Matcher matcher =pattern.matcher(fileName);
        if (matcher.find()) { return Integer.parseInt(matcher.group(1)); } return 0;
    }

    public static void checkSum(List<Migration> migrationList, HashMap<String, Resource> filesData){

        for(Migration item :migrationList){
            if(!filesData.get(item.getScript()).getChecksum().equals(item.getChecksum())){

                throw new ChecksumException("Migration script "+item.getScript() + " has incorrect checksum!");

            }
        }


    }

    public static HashMap<String, Resource> processMigrations(List<Migration> migrationList, HashMap<String, Resource> filesData) {

        HashMap<String, Resource> hashMap = new LinkedHashMap<>();

        for(Map.Entry<String,Resource> entry : filesData.entrySet()) {
            if(!hasMigration(entry.getKey(), migrationList)){
                hashMap.put(entry.getKey(), entry.getValue());
            }
        }

//        var migration = migrationList.getLast();
//
//        if(migration.getState().equals(State.FAILED.state())
//                && !Objects.equals(
//                filesData.get(migration.getScript()).getChecksum(),
//                migration.getChecksum())){
//
//            hashMap.put(migration.getScript(), filesData.get(migration.getScript()));
//
//        }else{
//            throw new MigrationExecutionException("Error: last migration was fail!");
//        }


        return hashMap;
    }
    private static Boolean hasMigration(String name, List<Migration> migrationList){


        Optional<Migration> result = migrationList
                .stream()
                .filter(item -> item.getScript().equals(name)).findAny();
        return result.isPresent();
    }



}
