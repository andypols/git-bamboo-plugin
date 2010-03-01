package uk.co.pols.bamboo.gitplugin.client.git.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

import com.atlassian.bamboo.build.logger.BuildLogger;
import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;

public class ExecutorGitCloneCommand implements GitCloneCommand {
    private static final Log log = LogFactory.getLog(ExecutorGitCloneCommand.class);
    private String gitExe;
    private CommandExecutor commandExecutor;

    public ExecutorGitCloneCommand(String gitExe, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.commandExecutor = commandExecutor;
    }

    public void cloneUrl(BuildLogger buildLogger, String repositoryUrl, File sourceDirectory) throws IOException {
        log.info(buildLogger.addBuildLogEntry("Running '" + gitExe + " clone " + repositoryUrl + "'"));

        // make sure we have a directory in which to execute the clone command
        File parentDirectory = sourceDirectory.getParentFile();
        parentDirectory.mkdirs();

        // Can't clone into an existing directory
        deleteDirectory(sourceDirectory);

        log.info(buildLogger.addBuildLogEntry(commandExecutor.execute(new String[]{gitExe, "clone", repositoryUrl, sourceDirectory.getPath()}, parentDirectory)));
    }

    private void deleteDirectory(File path) {
        if (path.exists()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
            path.delete();
        }

    }
}