package org.example.migrations;

import org.example.command.*;
import org.example.entity.*;
import org.example.migrations.managers.*;
import org.example.migrations.utils.*;

import java.util.*;

/**
 * Класс инструмента миграции, предназначенный для выполнения миграций базы данных.
 * Используется для управления процессами миграции, проверки текущего состояния миграций и отката изменений.
 */
public class MigrationTool {
    /**
     * Менеджер миграций, ответственный за выполнение миграций.
     */
    private final MigrationManager migrationManager;
    /**
     * Утилита для работы с конфигурационными свойствами.
     */
    private final PropertiesUtil propertiesUtil;
    /**
     * Конструктор, создающий объект инструмента миграции.
     *
     * @param migrationManager Менеджер миграций, управляющий процессом миграции.
     * @param propertiesUtil Утилита для работы с конфигурационными свойствами.
     */
    public MigrationTool(MigrationManager migrationManager, PropertiesUtil propertiesUtil) {
        this.migrationManager = migrationManager;
        this.propertiesUtil = propertiesUtil;
    }
    /**
     * Выполняет миграцию, используя указанные команды миграции.
     * Сначала загружает свойства, затем выполняет миграции, указаные в командных файлах.
     *
     * @param migrationCommand Команда миграции, содержащая путь к файлам changelog.
     */
    public void migrate(MigrationCommand migrationCommand){
        propertiesUtil.readProperties();
        migrationManager.execute(migrationCommand.getChangelogsPath());
    }
    /**
     * Показывает статус текущих миграций, выводя список всех выполненных миграций.
     * Загружает свойства конфигурации и выводит информацию о текущем состоянии миграций.
     */
    public void status() {
        propertiesUtil.readProperties();
     List<Migration> migrationsList =  migrationManager.getMigrations();
     migrationsList.forEach(item-> System.out.println(item.toString()));
    }
    /**
     * Выполняет откат всех миграций, очищая их и удаляя изменения.
     * Загружает свойства конфигурации перед выполнением операции.
     */
    public void rollback() {
        propertiesUtil.readProperties();
         migrationManager.clearAll();
        migrationManager.drop();

    }
}
