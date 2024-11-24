package org.example.command.cli;

import picocli.*;

@CommandLine.Command(name="migrate",version = "1.0.0", mixinStandardHelpOptions = true)
public class MigrationCommands implements Runnable{

    @CommandLine.Option(names={"-h", "--help"}, usageHelp = true)
    boolean help;


    @Override
    public void run() {
        System.out.println("sd,s");
    }
}