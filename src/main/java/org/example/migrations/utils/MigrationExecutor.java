package org.example.migrations.utils;

import lombok.*;
import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.managers.*;
import org.example.migrations.readers.*;


import java.sql.*;
import java.time.*;
import java.util.*;

import static org.example.settings.BaseSettings.tableName;

public class MigrationExecutor {

    private final String migrationHistorySql = "select * from migrations";

    private final String createTableSql = """
            
           CREATE TABLE %s 
           
           (script VARCHAR(255) PRIMARY KEY, 
           checksum BIGINT, 
           executed TIMESTAMP, 
           state VARCHAR(255))
            """;

    private final String saveSql="insert into "+tableName+ " (script, checksum, executed, state) values (?, ?, ?, ?) ";

    private final String updateSql="update "+tableName+ " set state=? where script=? ";

    private final String deleteSql = "delete from "+tableName+ " where script=?";
    private final String dropTable = "DROP TABLE "+tableName;

    public MigrationExecutor() {
    }


    public List<Migration> getMigrations(){
        try(Connection connection = ConnectionManager.createConnection()) {
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
                .checksum(resultSet.getLong("checksum"))
                .executed(resultSet.getTimestamp("executed"))
                .script(resultSet.getString("script"))
                .state(resultSet.getString("state"))
                .build();
    }

    public Boolean isHistoryExists(){
        try( Connection connection1 = ConnectionManager.createConnection()){
                DatabaseMetaData databaseMetaData = connection1.getMetaData();
                try (ResultSet rs = databaseMetaData.getTables(null, null, tableName, null)) { return rs.next();}
        }catch (SQLException sqlException){
            throw  new RuntimeException();
        }
    }
    public void createMigrationTable(){
        try(Connection connection = ConnectionManager.createConnection();) {
            Statement statement = connection.createStatement();
            statement.execute(String.format(createTableSql, tableName));
            System.out.println("Table "+tableName+" is created");
        } catch (SQLException e) {
            e.printStackTrace();
        };

    }
    public Migration createMigration(String scriptName, String state, Long checksum) throws SQLException {

                try( Connection connection1 = ConnectionManager.createConnection()){
                    PreparedStatement statement = connection1.prepareStatement(saveSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, scriptName);
                    statement.setLong(2, checksum);
                    statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    statement.setString(4, state);
                     statement.executeUpdate();
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    generatedKeys.next();
                    return Migration.builder()
                            .checksum(checksum)
                            .state(state)
                            .executed(Timestamp.valueOf(LocalDateTime.now()))
                            .script(scriptName)
                            .build();
                }
    }
    private void updateMigration(Migration migration) throws SQLException{
        try( Connection connection1 = ConnectionManager.createConnection()) {
            PreparedStatement statement = connection1.prepareStatement(updateSql);
            statement.setString(1, migration.getState());
            statement.setString(2, migration.getScript());
             statement.executeUpdate();
        }
    }
    public void deleteMigration(Migration migration) throws SQLException{
        try(Connection connection1 = ConnectionManager.createConnection()){
            PreparedStatement statement = connection1.prepareStatement(deleteSql);
            statement.setString(1, migration.getScript());
            statement.executeUpdate();
        }
    }

    private void executeScript(String script) throws SQLException {
        Connection connection1 = ConnectionManager.createConnection();
        Statement statement = connection1.createStatement();
        try {
            connection1.setAutoCommit(false);
            statement.executeUpdate(script);
            connection1.commit();
        }catch (SQLException e){
            try {
                connection1.rollback();

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new SQLException(e.getMessage());
        }finally {
            statement.close();
            connection1.close();
        }
    }
    public List<Migration> execute(HashMap<String, Resource> map) {
        String scriptName = map.keySet().stream().findFirst().get();
        Resource resource = map.values().stream().findFirst().get();
        Migration newMigration=null;
        try {
            newMigration = createMigration(scriptName, State.PENDING.state(), resource.getChecksum());
            executeScript(resource.getFile());
            newMigration.setState(State.SUCCESS.state());
            updateMigration(newMigration);
        }catch (SQLException e){
            try {
                if(newMigration!=null){
                    newMigration.setState(State.FAILED.state());
                   updateMigration(newMigration);
                }
            } catch (SQLException ex) {
                throw new MigrationExecutionException("Error: error while update migrations record!");
            }
        }
        List<Migration> migrations = new ArrayList<>();
        migrations.add(newMigration);
        return migrations;
    }

    public List<Migration> executeAll(HashMap<String, Resource> updateData){

        List<Migration> migrations = new ArrayList<>();
        Migration migration=null;
        Connection connection1 = ConnectionManager.createConnection();
        try {
            Statement statement = connection1.createStatement();
            connection1.setAutoCommit(false);
            for(Map.Entry<String,Resource> entry : updateData.entrySet()){
                migration =  createMigration(entry.getKey(), State.PENDING.state(), entry.getValue().getChecksum());
              statement.executeUpdate(entry.getValue().getFile());
                migration.setState(State.SUCCESS.state());
                migration.setExecuted(Timestamp.valueOf(LocalDateTime.now()));
                updateMigration(migration);
                migrations.add(migration);
            }
            connection1.commit();
        }catch (SQLException e){

            try {
                connection1.rollback();
                if(migration!=null){
                    migration.setState(State.FAILED.state());
                    migration.setExecuted(Timestamp.valueOf(LocalDateTime.now()));
                    updateMigration(migration);
//                    deleteMigration(migration);
                    migrations.add(migration);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            try {
                connection1.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return migrations;
    }


    public void drop() {
        try(   Connection connection1 = ConnectionManager.createConnection();
               Statement statement = connection1.createStatement()) {
            connection1.setAutoCommit(false);
            statement.executeUpdate(dropTable);
            connection1.commit();
        }catch (SQLException e){
            throw new MigrationExecutionException("Error: drop fail!");
        }

    }
}
