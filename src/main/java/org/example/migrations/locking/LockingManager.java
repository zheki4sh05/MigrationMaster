package org.example.migrations.locking;

import org.example.migrations.managers.*;

import java.sql.*;

import static org.example.settings.BaseSettings.tableName;

public class LockingManager {

    private final String createLockTable =
            """
            CREATE TABLE IF NOT EXISTS %s (
            id SERIAL PRIMARY KEY,
            locked BOOLEAN NOT NULL,
            version INT NOT NULL DEFAULT 0,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    )""";

    public void init(){
        //создать таблицу миграции
        try(Connection connection = ConnectionManager.createConnection();) {

            Statement statement = connection.createStatement();
            statement.execute(String.format(createLockTable, tableName));
            System.out.println("Table "+tableName+" is created");
        } catch (SQLException e) {
            e.printStackTrace();
        };
    };

    public void lock(){



    }
    public void unlock(){



    }




}
