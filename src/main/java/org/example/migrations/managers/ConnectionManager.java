package org.example.migrations.managers;

import org.example.exceptions.*;
import org.example.migrations.readers.*;
import org.example.migrations.utils.*;

import java.lang.reflect.*;
import java.sql.*;

/**
 * Класс ConnectionManager управляет установлением и закрытием соединений с базой данных.
 * Он предоставляет методы для создания нового соединения, получения текущего соединения и его завершения.
 */
public final class ConnectionManager {

    private static final  String mysqlDriverName = "com.mysql.cj.jdbc.Driver";
    private static final  String postgresDriverName= "org.postgresql.Driver";
    private static Boolean isLoaded = false;

    private static Connection connection;

    /**
     * Создает и возвращает новое соединение с базой данных.
     * Загружает нужный драйвер JDBC, если он еще не загружен, и устанавливает соединение
     * с использованием параметров, полученных из конфигурации.
     *
     * @return Соединение с базой данных.
     * @throws RuntimeException если произошла ошибка при загрузке драйвера или установке соединения.
     */
    public static  Connection createConnection() {

        UserProperties properties = PropertiesUtil.getProperties();

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
    /**
     * Возвращает текущее соединение с базой данных.
     * Если соединение не было установлено, вызывается метод {@link #createConnection()} для его создания.
     *
     * @return Текущее соединение с базой данных.
     */
    public static Connection getConnection() {
        return connection;
    }
    /**
     * Закрывает текущее соединение с базой данных.
     * Если соединение не было закрыто, будет выведено сообщение об ошибке.
     */
    public static void stopConnection() {

        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
