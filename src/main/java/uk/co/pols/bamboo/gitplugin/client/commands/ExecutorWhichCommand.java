package uk.co.pols.bamboo.gitplugin.client.commands;

import java.io.IOException;
import java.io.File;

public class ExecutorWhichCommand implements WhichCommand {
    private CommandExecutor commandExecutor;

    public ExecutorWhichCommand(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public String which(String command) throws IOException {
        return commandExecutor.execute(new String[]{"which", "git"}, new File(""));
    }
}
