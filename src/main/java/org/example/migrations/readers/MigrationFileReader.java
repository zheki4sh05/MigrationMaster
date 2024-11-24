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

/**
 * Класс MigrationFileReader отвечает за чтение миграционных файлов из указанной папки.
 * Он извлекает имена файлов, сортирует их по версии, проверяет правильность именования,
 * вычисляет контрольные суммы и сохраняет информацию о миграциях в формате JSON.
 */
public class MigrationFileReader {
    /**
     * Читает миграционные файлы из указанной папки, сортирует их и возвращает их содержимое и контрольные суммы.
     *
     * @param folderPath Путь к папке с миграционными файлами.
     * @return Связанный список с именами файлов и их содержимым (объекты Resource).
     */
    public LinkedHashMap<String, Resource> readFilesFromFolder(String folderPath) {
        List<String> fileNames = getSortedFileNames(folderPath);
        return readFilesByNames(folderPath, fileNames);
    }

    /**
     * Получает отсортированный список имен файлов в указанной папке.
     *
     * @param folderPath Путь к папке с миграционными файлами.
     * @return Отсортированный список имен файлов.
     */
    private List<String> getSortedFileNames(String folderPath) {
        List<String> fileNames = readAllNamesFromChangelog(folderPath);
        Collections.sort(fileNames, this::compareFileNames);
        return fileNames;
    }

    /**
     * Сравнивает два имени файла по их версии, извлекая номер версии из имени файла.
     *
     * @param name1 Первое имя файла.
     * @param name2 Второе имя файла.
     * @return Результат сравнения номеров версий из имен файлов.
     */

    private int compareFileNames(String name1, String name2) {
        int num1 = ChangelogMaster.getNumber(name1);
        int num2 = ChangelogMaster.getNumber(name2);
        return Integer.compare(num1, num2);
    }

    /**
     * Читает все имена файлов из папки миграций и проверяет их на правильность.
     *
     * @param folderPath Путь к папке с миграционными файлами.
     * @return Список имен файлов, которые прошли проверку.
     */
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

    /**
     * Проверяет, соответствует ли имя файла правилам именования миграций.
     *
     * @param fileName Имя файла.
     * @throws MigrationFileException Если имя файла некорректно.
     */
    private void validateFileName(String fileName) {
        if (!ChangelogMaster.isCorrect(fileName) && fileName.endsWith(BaseSettings.fileMigrationExtension)) {
            throw new MigrationFileException("Error: incorrect migration file naming!");
        }
    }

    /**
     * Вычисляет контрольную сумму для заданных байтов данных с использованием CRC32.
     *
     * @param bytes Байты данных файла.
     * @return Контрольная сумма.
     */
    private long calculateChecksum(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    /**
     * Читает файлы по их именам и сохраняет их содержимое и контрольные суммы.
     *
     * @param folderPath Путь к папке с миграционными файлами.
     * @param fileNames Список имен файлов для чтения.
     * @return Словарь, где ключ - имя файла, значение - объект Resource с данными файла.
     */
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


    /**
     * Сохраняет список миграций в файл в формате JSON.
     *
     * @param migrationList Список миграций, который необходимо сохранить.
     */
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
