package org.example;

import org.example.command.cli.*;
import picocli.*;

public class MainCLI {
    public static void main(String[] args) {
        new CommandLine(new MigrationCommands()).execute("--help");
    }
}
