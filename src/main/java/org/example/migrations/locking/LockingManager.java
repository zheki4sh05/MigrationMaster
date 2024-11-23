package org.example.migrations.locking;

import org.example.entity.*;
import org.example.exceptions.*;
import org.example.migrations.managers.*;
import org.example.migrations.utils.*;

import java.sql.*;
import java.util.*;
import java.util.function.*;

import static org.example.settings.BaseSettings.tableLockName;
import static org.example.settings.BaseSettings.tableName;

public class LockingManager {

    private final String createLockTable =
            """
            CREATE TABLE %s (
            id SERIAL PRIMARY KEY,
            locked BOOLEAN NOT NULL,
            version INT NOT NULL DEFAULT 0,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    )""";

    private final String initLockTable ="insert into "+tableLockName+ " (id, locked, version, created_at, updated_at) values (1, false, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ";

    private final String selectLock = "select locked from "+tableLockName+ " where id=1 for share";

    private final String lockTable = "update "+tableLockName+ " set locked=true,updated_at=CURRENT_TIMESTAMP  where id=1 ";
    private final String unlockTable = "update "+tableLockName+ " set locked=false where id=1 ";



    private void createIfNotExists(){

        try( Connection connection1 = ConnectionManager.createConnection()){

            DatabaseMetaData databaseMetaData = connection1.getMetaData();
            try (ResultSet rs = databaseMetaData.getTables(null, null, tableLockName, null)) {
               if(!rs.next()){
                   initLockTable();
               }
            }
        }catch (SQLException sqlException){
            throw  new RuntimeException();
        }
    }

    private void initLockTable(){

        try(Connection connection = ConnectionManager.createConnection();) {

            Statement statement = connection.createStatement();
            statement.execute(String.format(createLockTable, tableLockName));
            System.out.println("Table "+tableLockName+" is created");
            statement.execute(initLockTable);
            System.out.println("Table "+tableLockName+" init");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    protected void lock(){

        try(Connection connection = ConnectionManager.createConnection();) {

            Statement statement = connection.createStatement();
            statement.execute(lockTable);
            System.out.println("Table "+tableLockName+" locked");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    protected void unlock(){

        try(Connection connection = ConnectionManager.createConnection();) {

            Statement statement = connection.createStatement();
            statement.execute(unlockTable);
            System.out.println("Table "+tableLockName+" not locked");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void executeMigrates(Function<HashMap<String, Resource>, List<Migration>> executor , HashMap<String, Resource> list){

        createIfNotExists();

        int limiter = PropertiesUtil.getProperties().getRateLimiter();

        try{
            if(limiter==0){
                while(true) {


                    if(makeMigrations(executor,list)){
                        unlock();
                       break;
                    }
                }
            }else if(limiter>0){

                int count=limiter;
                while (count>0){
                    System.out.println(count);
                    if(makeMigrations(executor,list)){
                        unlock();
                        break;
                    }else {
                        count--;
                    }
                }

            }

        }catch (SQLException e){

        }catch (InterruptedException e){

        }

    }

    private Boolean tryLock() {

        try (Connection connection = ConnectionManager.createConnection()) {

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectLock);
            if (rs.next()) {
                boolean locked = rs.getBoolean("locked");
                if (!locked) {
                    lock();
                }
                return !locked;
            }

        } catch (SQLException e) {
            throw new LockingProcessException("Error: There were problems when taking the lock." + e.getMessage());
        }
        return false;
    }

    private Boolean makeMigrations(Function<HashMap<String, Resource>, List<Migration>> executor , HashMap<String, Resource> list) throws InterruptedException,SQLException {
        if (tryLock()) {
            executor.apply(list);
            return true;
        }else{

            Thread.sleep(PropertiesUtil.getProperties().getRetryTime());

            return false;
        }

    }

    };





