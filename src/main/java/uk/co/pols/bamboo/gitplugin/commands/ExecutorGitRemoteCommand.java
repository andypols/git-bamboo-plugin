package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class ExecutorGitRemoteCommand implements GitRemoteCommand {
    private static final Log log = LogFactory.getLog(ExecutorGitRemoteCommand.class);

    private String gitExe;
    private File sourceCodeDirectory;
    private CommandExecutor commandExecutor;

    public ExecutorGitRemoteCommand(String gitExe, File sourceCodeDirectory, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.commandExecutor = commandExecutor;
    }

    public void add_origin(String repositoryUrl, BuildLogger buildLogger) throws IOException {
        log.info(buildLogger.addBuildLogEntry("Running '" + gitExe + " remote add origin '" + repositoryUrl));
        commandExecutor.execute(new String[]{gitExe, "remote", "add", "origin", repositoryUrl}, sourceCodeDirectory);
    }
}