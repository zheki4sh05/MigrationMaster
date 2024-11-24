package org.example.command;

import lombok.extern.slf4j.*;
import org.example.*;
import org.example.exceptions.*;
import java.net.*;
import java.util.*;
import static org.example.settings.BaseSettings.*;
/** * Класс MigrationClient предназначен для выполнения различных команд миграции. */
@Slf4j
public class MigrationClient {

    /** * Метод read обрабатывает входные аргументы и выполняет соответствующую команду миграции.
     *
     * @param args массив строковых аргументов командной строки
     */
    public static void read(String[] args){
        if(args.length>0){
            String command = args[0];
            switch (command){
                case command1:{
                    String changelogPath = getChangelogPath().orElseThrow(()->new ChangelogException("Error: Changelog folder not found!"));
                    MasterMigration.migrate(changelogPath);
                    break;
                }
                case command2:{
                    MasterMigration.status();
                    break;
                }
                case command3:{
                    MasterMigration.rollback();
                    break;
                }
                default:{
                    log.info("Unused command: "+command);
                    break;
                }
            }

        }else{
            log.info("no args!");
        }
    }
    /**
     * Метод getChangelogPath возвращает путь к папке изменений.
     *
     * @return Optional, содержащий строку с путем к папке изменений, если она существует
     */
    private static Optional<String> getChangelogPath(){
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(baseFolder);
        return Optional.of(resource.getPath());
    }
}
