package org.example.migrations.readers;
import org.example.entity.*;
import org.example.exceptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import static org.example.settings.BaseSettings.fileMigrationExtension;


public class MigrationFileReader {

    public HashMap<String, Resource> readFilesFromFolder(String folderPath) {

        List<String> list = readAllNamesFromChangelog(folderPath);

        list = sortNames(list);

        return readFilesByNames(folderPath, list);
    }

    private List<String> sortNames(List<String> list) {

        Collections.sort(list, new Comparator<String>()
        { public int compare(String name1, String name2) {
            int num1 = ChangelogMaster.getNumber(name1);
            int num2 = ChangelogMaster.getNumber(name2);
            return Integer.compare(num1, num2); } });

        return list;

    }



    private List<String> readAllNamesFromChangelog(String folderPath){

        List<String> fileNames = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folderPath))) {

            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    if(!ChangelogMaster.isCorrect(fileName) && fileName.endsWith(fileMigrationExtension))
                        throw new MigrationFileException("Error: incorrect migration file naming!");
                    fileNames.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  fileNames;
    }

    private Long calculateChecksum(byte[] bytes) {
        final CRC32 crc32 = new CRC32();

        crc32.update(bytes);

        return crc32.getValue();
    }
    private HashMap<String, Resource> readFilesByNames(String folderPath, List<String> fileNames){
        HashMap<String, Resource> fileContentMap = new LinkedHashMap<>();
        try {

            for(String file: fileNames ){

                byte[] bytes = Files.readAllBytes(Paths.get(folderPath+"/"+file));

                fileContentMap.put(file, Resource.builder()
                                .file(new String(bytes))
                                .checksum(calculateChecksum(bytes))
                        .build());
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileContentMap;

    }



}
