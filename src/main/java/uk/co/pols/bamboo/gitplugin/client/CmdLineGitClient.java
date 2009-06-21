package uk.co.pols.bamboo.gitplugin.client;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.repository.RepositoryException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.co.pols.bamboo.gitplugin.client.commands.*;
import uk.co.pols.bamboo.gitplugin.GitRepositoryConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CmdLineGitClient implements GitClient {
    private static final Log log = LogFactory.getLog(CmdLineGitClient.class);
    private GitCommandDiscoverer gitCommandDiscoverer = gitCommandDiscoverer();

    public String getLatestUpdate(BuildLogger buildLogger, String repositoryUrl, String branch, String planKey, String lastRevisionChecked, List<Commit> commits, File sourceCodeDirectory) throws RepositoryException {
        try {
            pullCommand(sourceCodeDirectory).pullUpdatesFromRemoteRepository(buildLogger, repositoryUrl, branch);

            GitLogCommand gitLogCommand = logCommand(sourceCodeDirectory, lastRevisionChecked);
            List<Commit> gitCommits = gitLogCommand.extractCommits();
            String latestRevisionOnServer = gitLogCommand.getLastRevisionChecked();

            if (lastRevisionChecked == null) {
                log.info(buildLogger.addBuildLogEntry("Never checked logs for '" + planKey + "' on path '" + repositoryUrl + "'  setting latest revision to " + latestRevisionOnServer));
                return latestRevisionOnServer;
            }
            if (!lastRevisionChecked.equals(latestRevisionOnServer)) {
                log.info(buildLogger.addBuildLogEntry("Collecting changes for '" + planKey + "' on path '" + repositoryUrl + "' since " + lastRevisionChecked));
                commits.addAll(gitCommits);
            }

            return latestRevisionOnServer;
        } catch (IOException e) {
            throw new RepositoryException("Failed to get latest update", e);
        }
    }

    public String initialiseRepository(File sourceCodeDirectory, String planKey, String vcsRevisionKey, GitRepositoryConfig gitRepositoryConfig, boolean isWorkspaceEmpty, BuildLogger buildLogger) throws RepositoryException {
        if (isWorkspaceEmpty) {
            initialiseRemoteRepository(sourceCodeDirectory, gitRepositoryConfig.getRepositoryUrl(), gitRepositoryConfig.getBranch(), buildLogger);
        }

        return getLatestUpdate(buildLogger, gitRepositoryConfig.getRepositoryUrl(), gitRepositoryConfig.getBranch(), planKey, vcsRevisionKey, new ArrayList<Commit>(), sourceCodeDirectory);
    }

    protected GitPullCommand pullCommand(File sourceCodeDirectory) {
        return new ExecutorGitPullCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, new AntCommandExecutor());
    }

    protected GitLogCommand logCommand(File sourceCodeDirectory, String lastRevisionChecked) {
        return new ExecutorGitLogCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, lastRevisionChecked, new AntCommandExecutor());
    }

    protected GitInitCommand initCommand(File sourceCodeDirectory) {
        return new ExecutorGitInitCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, new AntCommandExecutor());
    }

    protected GitRemoteCommand remoteCommand(File sourceCodeDirectory) {
        return new ExecutorGitRemoteCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, new AntCommandExecutor());
    }

    private void initialiseRemoteRepository(File sourceDirectory, String repositoryUrl, String branch, BuildLogger buildLogger) throws RepositoryException {
        log.info(buildLogger.addBuildLogEntry(sourceDirectory.getAbsolutePath() + " is empty. Creating new git repository"));
        try {
            sourceDirectory.mkdirs();
            initCommand(sourceDirectory).init(buildLogger);
            remoteCommand(sourceDirectory).add_origin(repositoryUrl, branch, buildLogger);
        } catch (IOException e) {
            throw new RepositoryException("Failed to initialise repository", e);
        }
    }

    protected GitCommandDiscoverer gitCommandDiscoverer() {
        return new BestGuessGitCommandDiscoverer(new AntCommandExecutor());
    }
}