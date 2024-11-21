package org.example.migrations.managers;

import org.example.exceptions.*;
import org.example.migrations.readers.*;

import java.lang.reflect.*;
import java.sql.*;

public final class ConnectionManager {

    private static final  String mysqlDriverName = "com.mysql.cj.jdbc.Driver";
    private static final  String postgresDriverName= "org.postgresql.Driver";

    private static Boolean isLoaded = false;


    private static Connection connection;
    public static  Connection createConnection(UserProperties properties) {

        try{

            if(!isLoaded)
                Class.forName(properties.getDriverName()).getDeclaredConstructor().newInstance();
            isLoaded = true;

        }catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }


        try {

            connection =  DriverManager.getConnection(properties.getUrl(), properties.getUsername(), properties.getPassword());
            return connection;
        } catch (SQLException e) {
            throw new DbConnectionException("Error: wrong database connection properties! More: "+e.getMessage());
        }
    }
    public static Connection getConnection() {
        return connection;
    }

    public static void stopConnection() {

        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
