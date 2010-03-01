package uk.co.pols.bamboo.gitplugin.client.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atlassian.bamboo.build.logger.BuildLogger;

import java.io.File;
import java.io.IOException;

public class ExecutorGitCheckoutCommand implements GitCheckoutCommand {
    private static final Log log = LogFactory.getLog(ExecutorGitCheckoutCommand.class);
    private String gitExe;
    private CommandExecutor commandExecutor;

    public ExecutorGitCheckoutCommand(String gitExe, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.commandExecutor = commandExecutor;
    }

    public void checkoutBranch(BuildLogger buildLogger, String branch, boolean create, File sourceDirectory) throws IOException {
        if(create) {
            log.info(buildLogger.addBuildLogEntry("Running '" + gitExe + " checkout -b " + branch + "'"));
            commandExecutor.execute(new String[]{"git", "checkout", "-b", branch}, sourceDirectory);
        } else {
            log.info(buildLogger.addBuildLogEntry("Running '" + gitExe + " checkout " + branch + "'"));
            commandExecutor.execute(new String[]{"git", "checkout", branch}, sourceDirectory);
        }
    }
}