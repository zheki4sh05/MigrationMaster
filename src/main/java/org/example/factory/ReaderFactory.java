package org.example.factory;

import org.example.input.impl.*;
import org.example.input.interfaces.*;
import org.example.migrations.*;
import org.example.migrations.readers.*;

public class ReaderFactory {

    public static MasterMigrationApi createMigrationApi(){
        return new MigrationCommandReader(ToolFactory.getMigrationTool());
    }

    public static MigrationFileReader createFileReader(){
        return new MigrationFileReader();
    }

}
