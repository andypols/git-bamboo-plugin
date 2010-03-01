package uk.co.pols.bamboo.gitplugin.client.git.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;

public class ExecutorGitInitCommand implements GitInitCommand {
    private static final Log log = LogFactory.getLog(ExecutorGitInitCommand.class);

    private String gitExe;
    private File sourceCodeDirectory;
    private CommandExecutor commandExecutor;

    public ExecutorGitInitCommand(String gitExe, File sourceCodeDirectory, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.commandExecutor = commandExecutor;
    }

    public void init(BuildLogger buildLogger) throws IOException {
        log.info(buildLogger.addBuildLogEntry("Running '" + gitExe + " init'"));
        commandExecutor.execute(new String[]{gitExe, "init"}, sourceCodeDirectory);
    }
}