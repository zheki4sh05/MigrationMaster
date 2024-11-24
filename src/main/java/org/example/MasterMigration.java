package org.example;

import org.example.command.*;
import org.example.factory.*;
import org.example.input.impl.*;
import org.example.input.interfaces.*;

import static org.example.settings.BaseSettings.baseConfigFileName;
/**
 * Класс MasterMigration предоставляет методы для управления процессом миграции.
 * Он включает функциональность для выполнения миграций, проверки статуса миграций
 * и отката изменений с использованием API миграции.
 */
public class MasterMigration {
    /**
     * Выполняет миграцию с использованием указанных параметров.
     * Загружает свойства конфигурации из файла по указанному пути и выполняет миграцию,
     * используя путь к файлам changelog.
     *
     * @param propertiesPass Путь к файлу с конфигурацией свойств для миграции.
     * @param changelogsPath Путь к файлам changelog, которые содержат инструкции для миграции.
     */
    public static void migrate(String propertiesPass,String changelogsPath){
        MigrationCommand migrationCommand = new MigrationCommand(propertiesPass, changelogsPath);
        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();
        masterMigrationApi.read(migrationCommand);
    }
    /**
     * Выполняет миграцию с использованием пути к файлам changelog и стандартного конфигурационного файла.
     * Этот метод предполагает использование конфигурации по умолчанию для свойств.
     *
     * @param changelogsPath Путь к файлам changelog, содержащим инструкции для миграции.
     */
    public static void migrate(String changelogsPath){
        MigrationCommand migrationCommand = new MigrationCommand(baseConfigFileName, changelogsPath);
        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();
        masterMigrationApi.read(migrationCommand);
    }
    /**
     * Отображает текущий статус миграции.
     * Включает вывод информации о выполненных миграциях, если такие имеются.
     */
    public static void status() {
        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();
        masterMigrationApi.status();
    }
    /**
     * Выполняет откат всех миграций.
     * Этот метод отменяет все выполненные миграции и очищает изменения.
     */
    public static void rollback() {
        MasterMigrationApi masterMigrationApi = ReaderFactory.createMigrationApi();
        masterMigrationApi.rollback();
    }
}
