package org.example.command;

import lombok.*;

@Data
public class MigrationCommand {

    private String propertiesPass;
    private String changelogsPath;
    public MigrationCommand(String propertiesPass, String changelogsPath) {
        this.propertiesPass = propertiesPass;
        this.changelogsPath =changelogsPath;
    }
}
