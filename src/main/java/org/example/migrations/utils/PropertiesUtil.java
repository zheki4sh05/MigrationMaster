package org.example.migrations.utils;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.fluent.*;
import org.apache.commons.configuration2.ex.*;
import org.example.exceptions.*;
import org.example.migrations.readers.*;

import java.io.*;
import java.util.*;

import static org.example.settings.BaseSettings.*;

/**
 * Утилитный класс для работы с конфигурационными файлами.
 * Читает параметры из различных источников (файлы, переменные окружения) и сохраняет их в объекте UserProperties.
 */
public class PropertiesUtil {
    private static UserProperties userProperties;

    /**
     * Читает конфигурационный файл свойств и загружает параметры в объект UserProperties.
     *
     * @param propertiesPass Путь к файлу с конфигурацией.
     * @throws ApplicationPropertiesException Если произошла ошибка при загрузке или парсинге файла свойств.
     */
    public void readProperties(String propertiesPass) {

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(propertiesPass)) {
            properties.load(input);

            userProperties = parseProperties(properties);

            if(!UserProperties.checkProperties(userProperties))
                throw new ApplicationPropertiesException("Error: properties are null");


        }catch (FileNotFoundException e) {
            throw new ApplicationPropertiesException("Error: "+baseConfigFileName + " not found!");
        }
        catch (IOException e){
            throw new ApplicationPropertiesException("Error: unable to read application properties");
        }


    }
    /**
     * Читает конфигурацию по умолчанию из файла "application.properties" и загружает параметры в объект UserProperties.
     * Также пытается загрузить переменные окружения.
     *
     * @throws ApplicationPropertiesException Если произошла ошибка при чтении или парсинге свойств.
     */
    public void readProperties() {
        Configurations configs = new Configurations();

        try {
            Configuration config = configs.properties(new File("application.properties"));
            userProperties = parseProperties(config);
            parseEnvVariables();
            if(!UserProperties.checkProperties(userProperties))
                throw new ApplicationPropertiesException("Error: properties are null");
        } catch (ConfigurationException e) {
            throw new ApplicationPropertiesException("Error: unable to parse properties: " + e.getMessage());
        }
    }
    /**
     * Загружает значения переменных окружения в объект UserProperties.
     */
    private void parseEnvVariables() {

        userProperties.setUrl(System.getenv(MIGRATION_DATABASE_URL));
        userProperties.setUsername(System.getenv(MIGRATION_DATABASE_NAME));
        userProperties.setPassword(System.getenv(MIGRATION_DATABASE_PASSWORD));
    }
    /**
     * Преобразует объект Configuration в объект UserProperties.
     *
     * @param config Конфигурация из файла.
     * @return Объект UserProperties с параметрами из конфигурации.
     */
    private UserProperties parseProperties(Configuration config) {
        return UserProperties.builder()
                .driverName(config.getString(appName+".database.driver"))
                .rollbackAll(Boolean.valueOf(config.getString(appName+".database.rollbackAll")))
                .retryTime(Integer.parseInt(config.getString(appName+".database.retryTime")))
                .rateLimiter(Integer.parseInt(config.getString(appName+".database.rateLimiter")))
                .build();
    }
    /**
     * Преобразует объект Properties в объект UserProperties.
     *
     * @param properties Свойства из файла.
     * @return Объект UserProperties с параметрами из свойств.
     */
    private UserProperties parseProperties(Properties properties) {
        return UserProperties.builder()
                .url(properties.getProperty(appName+".database.url"))
                .driverName(properties.getProperty(appName+".database.driver"))
                .username(properties.getProperty(appName+".database.name"))
                .password(properties.getProperty(appName+".database.password"))
                .rollbackAll(Boolean.valueOf(properties.getProperty(appName+".database.rollbackAll")))
                .retryTime(Integer.parseInt(properties.getProperty(appName+".database.retryTime")))
                .rateLimiter(Integer.parseInt(properties.getProperty(appName+".database.rateLimiter")))
                .build();
    }
    /**
     * Возвращает текущие пользовательские свойства.
     *
     * @return Объект UserProperties с конфигурационными параметрами.
     */
    public static UserProperties getProperties(){
        return userProperties;
    }

}
