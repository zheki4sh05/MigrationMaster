package org.example.migrations.readers;
import com.fasterxml.jackson.databind.*;
import org.example.entity.*;
import org.example.exceptions.*;
import org.example.settings.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import static org.example.settings.BaseSettings.fileMigrationExtension;


public class MigrationFileReader {

//    public HashMap<String, Resource> readFilesFromFolder(String folderPath) {
//
//        List<String> list = readAllNamesFromChangelog(folderPath);
//
//        list = sortNames(list);
//
//        return readFilesByNames(folderPath, list);
//    }
//
//    private List<String> sortNames(List<String> list) {
//
//        Collections.sort(list, new Comparator<String>()
//        { public int compare(String name1, String name2) {
//            int num1 = ChangelogMaster.getNumber(name1);
//            int num2 = ChangelogMaster.getNumber(name2);
//            return Integer.compare(num1, num2); } });
//
//        return list;
//
//    }
//
//
//
//    private List<String> readAllNamesFromChangelog(String folderPath){
//
//        List<String> fileNames = new ArrayList<>();
//
//        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folderPath))) {
//
//            for (Path path : directoryStream) {
//                if (Files.isRegularFile(path)) {
//                    String fileName = path.getFileName().toString();
//                    if(!ChangelogMaster.isCorrect(fileName) && fileName.endsWith(fileMigrationExtension))
//                        throw new MigrationFileException("Error: incorrect migration file naming!");
//                    fileNames.add(fileName);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return  fileNames;
//    }
//
//    private Long calculateChecksum(byte[] bytes) {
//        final CRC32 crc32 = new CRC32();
//
//        crc32.update(bytes);
//
//        return crc32.getValue();
//    }
//    private HashMap<String, Resource> readFilesByNames(String folderPath, List<String> fileNames){
//        HashMap<String, Resource> fileContentMap = new LinkedHashMap<>();
//        try {
//
//            for(String file: fileNames ){
//
//                byte[] bytes = Files.readAllBytes(Paths.get(folderPath+"/"+file));
//
//                fileContentMap.put(file, Resource.builder()
//                                .file(new String(bytes))
//                                .checksum(calculateChecksum(bytes))
//                        .build());
//            }
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return fileContentMap;
//
//    }
    //--------
    public LinkedHashMap<String, Resource> readFilesFromFolder(String folderPath) {
        List<String> fileNames = getSortedFileNames(folderPath);
        return readFilesByNames(folderPath, fileNames);
    }

    private List<String> getSortedFileNames(String folderPath) {
        List<String> fileNames = readAllNamesFromChangelog(folderPath);
        Collections.sort(fileNames, this::compareFileNames);
        return fileNames;
    }

    private int compareFileNames(String name1, String name2) {
        int num1 = ChangelogMaster.getNumber(name1);
        int num2 = ChangelogMaster.getNumber(name2);
        return Integer.compare(num1, num2);
    }

    private List<String> readAllNamesFromChangelog(String folderPath) {
        List<String> fileNames = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    validateFileName(fileName);
                    fileNames.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    private void validateFileName(String fileName) {
        if (!ChangelogMaster.isCorrect(fileName) && fileName.endsWith(BaseSettings.fileMigrationExtension)) {
            throw new MigrationFileException("Error: incorrect migration file naming!");
        }
    }

    private long calculateChecksum(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    private LinkedHashMap<String, Resource> readFilesByNames(String folderPath, List<String> fileNames) {
        LinkedHashMap<String, Resource> fileContentMap = new LinkedHashMap<>();

        for (String file : fileNames) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(folderPath, file));
                Resource resource = Resource.builder()
                        .file(new String(bytes))
                        .checksum(calculateChecksum(bytes))
                        .build();
                fileContentMap.put(file, resource);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + file, e);
            }
        }

        return fileContentMap;
    }


    public void save(List<Migration> migrationList) {

        String jsonName = "migrations.json";
        MigrationDto migrationDto = new MigrationDto(migrationList);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(jsonName), migrationDto);
        } catch (IOException e) {
            System.out.println("Error: unable to write result!");
        }

    }
}
