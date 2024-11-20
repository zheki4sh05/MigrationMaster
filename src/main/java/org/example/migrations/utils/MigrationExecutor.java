package org.example.migrations.utils;

import lombok.*;
import org.example.entity.*;
import org.example.migrations.managers.*;


import java.sql.*;
import java.util.*;

import static org.example.settings.BaseSettings.tableName;

public class MigrationExecutor {

    private  Connection connection;

    private final String migrationHistorySql = "";

    private final String createTableSql = """
            
           CREATE TABLE %s 
           
           ( id INT PRIMARY KEY, 
           script VARCHAR(255), 
           checksum BIGINT, 
           executed TIMESTAMP, 
           state VARCHAR(255), 
           locked BOOLEAN
            
            """;



    public MigrationExecutor(Connection connection) {
        this.connection = connection;


    }

    public MigrationExecutor() {
    }

    public List<Migration> getMigrations(){

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(migrationHistorySql);

            List<Migration> migrations = new ArrayList<>();

            while (resultSet.next()){
                migrations.add(buildMigration(resultSet));
            }

            return  migrations;

        }catch (SQLException e){
            throw new RuntimeException(e.getMessage());
        }


    }
    private Migration buildMigration(ResultSet resultSet) throws SQLException{

        return Migration.builder()
                .id(resultSet.getInt("id"))
                .checksum(resultSet.getLong("checksum"))
                .executed(resultSet.getTimestamp("executed"))
                .script(resultSet.getString("script"))
                .state(resultSet.getString("state"))
                .locked(resultSet.getBoolean("locked"))
                .build();
    }

    public Boolean isHistoryExists(){
        try(Connection connection1 = ConnectionManager.getConnection()){
                DatabaseMetaData databaseMetaData = connection1.getMetaData();
                try (ResultSet rs = databaseMetaData.getTables(null, null, tableName, null)) { return rs.next();}

        }catch (SQLException sqlException){
            throw  new RuntimeException();
        }
    }

    public void createMigrationTable(){

        try (Connection connection = ConnectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute( String.format(createTableSql, tableName));
            System.out.println("Table "+tableName+" is created");
        } catch (SQLException e) {
            e.printStackTrace();
        };

    }

    public Migration execute(String key, String value) {
        return null;
    }
}
