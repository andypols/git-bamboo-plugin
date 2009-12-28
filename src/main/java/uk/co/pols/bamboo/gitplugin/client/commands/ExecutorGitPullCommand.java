package uk.co.pols.bamboo.gitplugin.client.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class ExecutorGitPullCommand implements GitPullCommand {
    private static final Log log = LogFactory.getLog(ExecutorGitPullCommand.class);

    private String gitExe;
    private File sourceCodeDirectory;
    private CommandExecutor commandExecutor;

    public ExecutorGitPullCommand(String gitExe, File sourceCodeDirectory, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.commandExecutor = commandExecutor;
    }

    public void pullUpdatesFromRemoteRepository(BuildLogger buildLogger, String repositoryUrl, String branch) throws IOException {
        log.info(buildLogger.addBuildLogEntry("Pulling source from branch '" + branch + "' @ '" + repositoryUrl + "' into '" + sourceCodeDirectory.getAbsolutePath() + "'."));

        String output = commandExecutor.execute(new String[]{gitExe, "pull", repositoryUrl, branch + ":" + branch}, sourceCodeDirectory);

        // FIXME this really should check the exit code rather than looking for errors in the text
        if (output.contains("fatal:")) {
            throw new IOException("Could not pull from '" + repositoryUrl + "'. git-pull: " + output);
        } else {
            log.info(buildLogger.addBuildLogEntry(output));
        }
    }
}