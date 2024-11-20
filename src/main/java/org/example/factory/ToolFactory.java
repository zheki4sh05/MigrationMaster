package org.example.factory;

import org.example.migrations.*;

public class ToolFactory {
    public static MigrationTool getMigrationTool(){
        return new MigrationTool(ManagersFactory.getMigrationManager(), UtilFactory.createUtilProperties());
    }

}
