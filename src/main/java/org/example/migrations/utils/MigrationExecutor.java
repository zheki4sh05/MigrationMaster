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
           state VARCHAR(255), 
           locked BOOLEAN)
            
            """;

    private final String saveSql="insert into "+tableName+ " (script, checksum, executed, state, locked) values (?, ?, ?, ?, ?) ";


    private final String updateSql="update "+tableName+ " set state=?, locked=? where script=? ";

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
                .locked(resultSet.getBoolean("locked"))
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
    public Migration createMigration(String scriptName, String state, Long checksum, Boolean locked) throws SQLException {

                try( Connection connection1 = ConnectionManager.createConnection()){
                    PreparedStatement statement = connection1.prepareStatement(saveSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, scriptName);
                    statement.setLong(2, checksum);
                    statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    statement.setString(4, state);
                    statement.setBoolean(5, locked);
                    int id=  statement.executeUpdate();
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    generatedKeys.next();
                    return Migration.builder()
                            .checksum(checksum)
                            .locked(locked)
                            .state(state)
                            .executed(Timestamp.valueOf(LocalDateTime.now()))
                            .script(scriptName)
                            .build();
                }

    }

    public void updateMigration(Migration migration) throws SQLException{


        try( Connection connection1 = ConnectionManager.createConnection()) {
            PreparedStatement statement = connection1.prepareStatement(updateSql);
            statement.setString(1, migration.getState());
            statement.setBoolean(2, migration.getLocked());
            statement.setString(3, migration.getScript());
             statement.executeUpdate();

        }



    }

    private void executeScript(String script) throws SQLException {
        Connection connection1 = ConnectionManager.createConnection();
        try {

            Statement statement = connection1.createStatement();

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
            connection1.close();
        }

    }

    public Migration execute(String scriptName, Resource resource) {
        Migration newMigration=null;

        try {

            newMigration = createMigration(scriptName, State.PENDING.state(), resource.getChecksum(), true);

            executeScript(resource.getFile());

            newMigration.setState(State.SUCCESS.state());

            newMigration.setLocked(false);

            updateMigration(newMigration);


        }catch (SQLException e){

            try {
                if(newMigration!=null){
                    newMigration.setState(State.FAILED.state());
                    newMigration.setLocked(false);
                   updateMigration(newMigration);
                }
            } catch (SQLException ex) {
                throw new MigrationExecutionException("!!!!");
            }


        }

        return newMigration;
    }


    public List<Migration> executeAll(HashMap<String, Resource> updateData) {
        return new ArrayList<>();
    }


}
