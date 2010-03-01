package uk.co.pols.bamboo.gitplugin.client.git.commands;

import org.apache.tools.ant.taskdefs.condition.Os;

import java.io.IOException;
import java.io.File;

import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;

public class ExecutorWhichCommand implements WhichCommand {
    private CommandExecutor commandExecutor;
    private static final String UNKNOWN = "";

    public ExecutorWhichCommand(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public String which(String command) throws IOException {
        if(isUnixPlatform()) {
            return commandExecutor.execute(new String[]{"which", "git"}, new File(""));
        }
        return UNKNOWN;
    }

    private boolean isUnixPlatform() {
        return Os.isFamily(Os.FAMILY_UNIX);
    }
}