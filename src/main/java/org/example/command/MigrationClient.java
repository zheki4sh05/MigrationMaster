package org.example.command;

import org.example.*;
import org.example.exceptions.*;
import java.net.*;
import java.util.*;
import static org.example.settings.BaseSettings.*;
public class MigrationClient {
    public static void read(String[] args){
        if(args.length>0){
            String command = args[0];
            switch (command){
                case command1:{
                    String changelogPath = getChangelogPath().orElseThrow(()->new ChangelogException("Error: Changelog folder not found!"));
                    MasterMigration.migrate(changelogPath);
                    break;
                }
                case command2:{
                    MasterMigration.status();
                    break;
                }
                case command3:{
                    MasterMigration.rollback();
                    break;
                }
                default:{
                    System.out.println("Unused command: "+command);
                    break;
                }
            }

        }else{
            System.out.println("no args!");
        }
    }

    private static Optional<String> getChangelogPath(){
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(baseFolder);
        return Optional.of(resource.getPath());
    }
}
