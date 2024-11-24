package org.example.migrations.readers;

import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.utils.*;

import java.util.*;
import java.util.regex.*;

import static org.example.settings.BaseSettings.nameVersionPatter;
/**
 * Класс ChangelogMaster управляет обработкой миграций и их проверкой, включая контрольные суммы и проверку версий.
 * Он предоставляет методы для проверки правильности имен файлов миграций, получения номеров версий, проверки контрольных сумм,
 * и обработки миграций, чтобы убедиться, что только новые миграции будут выполнены.
 */
public class ChangelogMaster {
    /**
     * Проверяет, соответствует ли имя файла миграции шаблону (например, содержит ли оно номер версии).
     *
     * @param fileName Имя файла миграции.
     * @return true, если имя файла соответствует шаблону, иначе false.
     */
    public static Boolean isCorrect(String fileName){
        Pattern pattern = Pattern.compile(nameVersionPatter);
        Matcher matcher =pattern.matcher(fileName);
        return matcher.find();
    }
    /**
     * Извлекает номер версии из имени файла миграции.
     * Имя файла должно соответствовать определенному шаблону, например, "1_create_table.sql", где "1" - это версия.
     *
     * @param fileName Имя файла миграции.
     * @return Номер версии, извлеченный из имени файла. Если имя не соответствует шаблону, возвращает 0.
     */
    public static Integer getNumber(String fileName){
        Pattern pattern = Pattern.compile(nameVersionPatter);
        Matcher matcher =pattern.matcher(fileName);
        if (matcher.find()) { return Integer.parseInt(matcher.group(1)); } return 0;
    }
    /**
     * Проверяет контрольные суммы миграционных файлов.
     * Если контрольная сумма файла в базе данных не совпадает с контрольной суммой файла на диске, выбрасывается исключение.
     *
     * @param migrationList Список миграций, уже выполненных в базе данных.
     * @param filesData Словарь с миграционными файлами, где ключ - имя файла, значение - объект Resource с данными файла.
     * @throws ChecksumException если контрольные суммы не совпадают.
     */
    public static void checkSum(List<Migration> migrationList, HashMap<String, Resource> filesData){
        for(Migration item :migrationList){
            if(!filesData.get(item.getScript()).getChecksum().equals(item.getChecksum())){
                throw new ChecksumException("Migration script "+item.getScript() + " has incorrect checksum!");
            }
        }
    }
    /**
     * Обрабатывает миграции, исключая те, которые уже были выполнены.
     * Возвращает только те миграции, которые еще не были выполнены.
     *
     * @param migrationList Список миграций, которые уже были выполнены.
     * @param filesData Словарь с миграционными файлами, где ключ - имя файла, значение - объект Resource с данными файла.
     * @return Словарь с миграциями, которые еще не были выполнены.
     */
    public static HashMap<String, Resource> processMigrations(List<Migration> migrationList, HashMap<String, Resource> filesData) {
        HashMap<String, Resource> hashMap = new LinkedHashMap<>();
        for(Map.Entry<String,Resource> entry : filesData.entrySet()) {
            if(!hasMigration(entry.getKey(), migrationList)){
                hashMap.put(entry.getKey(), entry.getValue());
            }
        }
        return hashMap;
    }
    /**
     * Проверяет, была ли уже выполнена миграция с данным именем.
     *
     * @param name Имя файла миграции.
     * @param migrationList Список выполненных миграций.
     * @return true, если миграция с таким именем уже выполнена, иначе false.
     */
    private static Boolean hasMigration(String name, List<Migration> migrationList){
        Optional<Migration> result = migrationList
                .stream()
                .filter(item -> item.getScript().equals(name)).findAny();
        return result.isPresent();
    }
}
