package uk.co.pols.bamboo.gitplugin.client.git.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * First look for the "GIT_HOME" environment variable. Set -DGIT_HOME=/your/path/git when starting Bamboo.
 * <p/>
 * Then, iff you are running bamboo on a unix, if will try and find it by running a "which git" command.
 * <p/>
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
        return discoverLocationOfGitCommandLineBinary();
    }

    private String discoverLocationOfGitCommandLineBinary() {
        if (System.getProperty(GIT_HOME) != null) {
            return System.getProperty(GIT_HOME);
        }

        String gitPath = whichGitIsInThePath();
        if (StringUtils.isNotBlank(gitPath)) {
            return gitPath.trim();
        }

        return DEFAULT_GIT_EXE;
    }

    private String whichGitIsInThePath() {
        try {
            WhichCommand whichCommand = new ExecutorWhichCommand(executor);
            return whichCommand.which("git");
        } catch (IOException e) {
            log.error("Failed to execute the git command", e);
            return null;
        }
    }
}