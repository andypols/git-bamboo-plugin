package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class GitPullCommand {
    private static final Log log = LogFactory.getLog(GitPullCommand.class);

    private String gitExe;
    private File sourceCodeDirectory;
    private CommandExecutor commandExecutor;

    public GitPullCommand(String gitExe, File sourceCodeDirectory, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.commandExecutor = commandExecutor;
    }

    public void pullUpdatesFromRemoteRepository(BuildLogger buildLogger, String repositoryUrl) throws IOException {
        log.info(buildLogger.addBuildLogEntry("Pulling source from '" + repositoryUrl + "' into '" + sourceCodeDirectory.getAbsolutePath() + "'."));

        commandExecutor.execute(new String[]{gitExe, "pull", "origin", "master"}, sourceCodeDirectory);
    }
}