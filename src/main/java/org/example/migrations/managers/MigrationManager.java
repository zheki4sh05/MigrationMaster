package org.example.migrations.managers;


import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.locking.*;
import org.example.migrations.readers.*;
import org.example.migrations.utils.*;

import java.sql.*;
import java.util.*;
import java.util.function.*;
/**
 * Класс MigrationManager управляет процессом выполнения миграций в базе данных.
 * Он решает, какой подход к выполнению миграций использовать (пессимистичный или оптимистичный),
 * читает файлы миграций и управляет выполнением миграций с использованием LockingManager и MigrationExecutor.
 */
public class MigrationManager {

    private final MigrationFileReader migrationFileReader;

    private final MigrationExecutor migrationExecutor;

    private final LockingManager lockingManager = new LockingManager();

    /**
     * Конструктор класса MigrationManager.
     *
     * @param migrationFileReader Читатель файлов миграций, используемый для загрузки файлов миграций.
     * @param migrationExecutor Экземпляр класса для выполнения миграций.
     */
    public MigrationManager(MigrationFileReader migrationFileReader, MigrationExecutor migrationExecutor) {
        this.migrationFileReader = migrationFileReader;
        this.migrationExecutor = migrationExecutor;
    }
    /**
     * Выполняет миграции с учетом текущих настроек (пессимистичный или оптимистичный подход).
     *
     * @param changelogsPath Путь к директории с файлами миграций.
     */
    public void execute(String changelogsPath) {
        if(PropertiesUtil.getProperties().getRollbackAll()){
            executePessimistic(changelogsPath);
        }else{
            executeOptimistic(changelogsPath);
        }
    }
    /**
     * Выполняет миграции с использованием пессимистичного подхода:
     * блокирует выполнение миграций и выполняет их все сразу.
     *
     * @param changelogsPath Путь к директории с файлами миграций.
     */
    private void executePessimistic(String changelogsPath){
        Function<HashMap<String, Resource>, List<Migration>> func = migrationExecutor::executeAll;
        lockingManager.executeMigrates(func, initMigrations(changelogsPath));
        System.out.println(lockingManager.migrations());
    }

    /**
     * Выполняет миграции с использованием оптимистичного подхода:
     * выполняет миграции по одной, пока все не будут завершены.
     *
     * @param changelogsPath Путь к директории с файлами миграций.
     */
    private void executeOptimistic(String changelogsPath){
        while(true){
            var map  = initMigrations(changelogsPath);

            if(map.isEmpty())
                break;

            Function<HashMap<String, Resource>, List<Migration>> func = migrationExecutor::execute;
            lockingManager.executeMigrates(func, map);
        }
    }
    /**
     * Инициализирует миграции, читая файлы из указанной директории и проверяя их с существующими миграциями.
     *
     * @param changelogsPath Путь к директории с файлами миграций.
     * @return Карта, содержащая данные о миграциях.
     * @throws MigrationFileException если директория с миграциями пуста.
     */
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
    /**
     * Проверяет существующие миграции и сопоставляет их с файлами миграций.
     *
     * @param filesData Данные о миграционных файлах.
     * @return Обновленная карта с данными о миграциях.
     * @throws MigrationExecutionException если таблица миграций пуста или произошла другая ошибка.
     */
    private HashMap<String, Resource> checkExistsMigrations(HashMap<String, Resource> filesData){
        List<Migration> migrationList = new ArrayList<>(migrationExecutor.getMigrations());

        if(migrationList.size()==0){
            throw new MigrationExecutionException("Error: no migrations rows founded, migrations table exists, but is empty!");
        }

        ChangelogMaster.checkSum(migrationList, filesData);

        filesData = ChangelogMaster.processMigrations(migrationList, filesData);

        return filesData;
    }

    /**
     * Возвращает список миграций, которые должны быть выполнены.
     * Если история миграций существует, загружаются выполненные миграции из базы данных.
     * Если история отсутствует, возвращается пустой список.
     *
     * @return Список миграций.
     */
    public List<Migration> getMigrations() {
        if(migrationExecutor.isHistoryExists()){
            List<Migration> migrationList = migrationExecutor.getMigrations();
            migrationFileReader.save(migrationList);
            return migrationList;
        }else{
           return new ArrayList<>();
        }
    }
    /**
     * Очищает все миграции из базы данных, если история миграций существует.
     * В случае ошибки при удалении миграций выбрасывается исключение.
     */
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
    /**
     * Удаляет таблицу миграций из базы данных.
     */
    public void drop() {

        migrationExecutor.drop();

    }
}
