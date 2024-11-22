package org.example.migrations.utils;

import org.example.exceptions.*;
import org.example.migrations.readers.*;

import java.io.*;
import java.util.*;

import static org.example.settings.BaseSettings.appName;
import static org.example.settings.BaseSettings.baseConfigFileName;

public class PropertiesUtil {
    private static UserProperties userProperties;
    public void readProperties(String propertiesPass) {

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(propertiesPass)) {
            properties.load(input);

            userProperties = parseProperties(properties);

            UserProperties.checkProperties(userProperties);

        }catch (FileNotFoundException e) {
            throw new ApplicationPropertiesException("Error: "+baseConfigFileName + " not found!");
        }
        catch (IOException e){
            throw new ApplicationPropertiesException("Error: unable to read application properties");
        }


    }

    private UserProperties parseProperties(Properties properties) {
        return UserProperties.builder()
                .url(properties.getProperty(appName+".database.url"))
                .driverName(properties.getProperty(appName+".database.driver"))
                .username(properties.getProperty(appName+".database.name"))
                .password(properties.getProperty(appName+".database.password"))
                .rollbackAll(Boolean.valueOf(properties.getProperty(appName+"database.rollbackAll")))
                .build();
    }

    public static UserProperties getProperties(){
        return userProperties;
    }

}
