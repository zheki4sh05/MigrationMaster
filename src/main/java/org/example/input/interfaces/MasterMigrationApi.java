package org.example.input.interfaces;

import org.example.command.*;

public interface MasterMigrationApi{
    void read(MigrationCommand migrationCommand);

    void status();

    void rollback();
}
