# Migration Master
Migration Master - is a tool for managing database migrations.

## Table of Contents
- [Get Started](#get-started)
- [Feature](#feature)
- [How to use](#howtouse)
- [Contact](#logging)


## Get Started
Clone source code or download jar file from here: 

when download jar file:
1) Add migration.jar to your project dependencies
2) Don't forget to add environment variables like this key:
    ```java
       MIGRATION_DATABASE_URL
       MIGRATION_DATABASE_NAME
       MIGRATION_DATABASE_PASSWORD
    ```
3) Create application.properties in your resources:
      ```java
      migration.database.driver=org.postgresql.Driver
      migration.database.rollbackAll=true
      migration.database.retryTime=1000
      migration.database.rateLimiter=3
    ```
4) If you want to start migrations from console: 
    
    1) Add to your MainClass class MigrationClient.read(args)
    ````java
    import org.example.command.*;
    public class MainClass {
       public static void main(String[] args) {
           MigrationClient.read(args);
       }
    }
   ````
    2) execute this command in your build project:
   
    ```bash
    java -jar -cp yourProject.jar com.example.MainClass migrate/rollback/status
    ```
5) if your need to start migrations from your own, try this:
    ````java
    import org.example.command.*;
    public class Main {
       public static void main(String[] args) {
            MasterMigration.migrate("your path to changelog folder");
       }
    }
   ````
    

## Feature
   This simple migrations tool has some features that directed to your best migrations experience.
At first you can use CLI, carried out using this:
 
  ````java
    import org.example.command.*;
    public class Main {
       public static void main(String[] args) {
           MigrationClient.read(args);
       }
    }
   ````
After building your jar file, in terminal use this command:

To start migration

     migrate

To check status and import result in json:

     status

To rollback migrations records:

     rollback


  In other hand, you can realize optimistic or pessimistic lock by adding this property in application.properties:
  ````java
  migration.database.rollbackAll=true
   ````
  If you do this, then if at least one migration is unsuccessful, then all are rolled back. In this case, throughout the entire migration process, one user takes over the database
To avoid blocking the entire database and rolling back even successful ones, try this:
 ````java
  migration.database.rollbackAll=false
   ````
  To prevent other applications from constantly requesting access to the database and to avoid downtime, try playing with these values ​​(default limiter=0, and retryTime=3000 (3 seconds)
 ````java
    migration.database.retryTime=1000
    migration.database.rateLimiter=3
   ````

  Currently the tool supports the following databases: PostgresSQL, MySQL.
To indicate the database driver use this property:
 ````java
   migration.database.driver=org.postgresql.Driver
   ````
or

 ````java
   migration.database.driver=com.mysql.cj.jdbc.Driver
   ````
You don't need to specify the location of application.properties. This tool will do it for you.

## How to use
Create a **"сhangelogs"** folder in your resources. This is where you can put your migration scripts.
The library will apply migrations defined as SQL files
The user must be able to control the sequence of migrations by using the correct naming: 

 ````java
  V1__name.sql
   ````
Through proper naming, the library is able to understand the current version of the database and maintain a history of migrations. This is also used for reporting purposes.

You can get the report like this:
 ````java
   MasterMigration.status()
   ````

To delete migration records after a unsuccessful migration, use this

 ````java
   MasterMigration.rollback()
   ````
**Attention!** Any changelog changes are analyzed by the library. Otherwise there will be exceptions.
**Attention!** The contents of migration files must not be changed under any circumstances. Even if the migration was unsuccessful, just create a new migration file and add it to the changelog folder. The contents of the files are recorded by a checksum, which is checked when migrations are called.
## Contact
For suggestions and comments сontact me:
````java
e.shostak05@gmail.com
   ````


