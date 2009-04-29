package uk.co.pols.bamboo.gitplugin.client.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * First look for the "GIT_HOME" environment variable. Set -DGIT_HOME=/your/path/git when starting Bamboo.
 *
 * Then, iff you are running bamboo on a unix, if will try and find it by running a "which git" command.
 *
 * If all else fails it assumes it's in "/opt/local/bin/git" (cos that's where it's locatted on my MacBookPro!)
 */
public class BestGuessGitCommandDiscoverer implements GitCommandDiscoverer {
    private static final Log log = LogFactory.getLog(BestGuessGitCommandDiscoverer.class);

    public static final String DEFAULT_GIT_EXE = "/opt/local/bin/git";
    public static final String GIT_HOME = "GIT_HOME";
    private CommandExecutor executor;

    public BestGuessGitCommandDiscoverer(CommandExecutor executor) {
        this.executor = executor;
    }

    public String gitCommand() {
        if(System.getProperty(GIT_HOME) != null) {
            return System.getProperty(GIT_HOME);
        }

        try {
            WhichCommand whichCommand = new ExecutorWhichCommand(executor);
            String gitBinary = whichCommand.which("git");

            if(StringUtils.isNotBlank(gitBinary)) {
                return gitBinary.trim();
            }
        } catch (IOException e) {
            log.error("Failed to execute the git command", e);
        }

        return DEFAULT_GIT_EXE;
    }
}
