package uk.co.pols.bamboo.gitplugin.client;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.repository.RepositoryException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.co.pols.bamboo.gitplugin.client.git.Git;
import uk.co.pols.bamboo.gitplugin.client.git.commands.GitLogCommand;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CmdLineGitClient implements GitClient {
    private static final Log log = LogFactory.getLog(CmdLineGitClient.class);
    private Git git;

    public CmdLineGitClient(Git git) {
        this.git = git;
    }

    public String getLatestUpdate(BuildLogger buildLogger, String repositoryUrl, String branch, String planKey, String lastRevisionChecked, List<Commit> commits, File sourceCodeDirectory) throws RepositoryException {
        try {
            if (!git.isValidRepo(sourceCodeDirectory)) {
                initialiseRemoteRepository(sourceCodeDirectory, repositoryUrl, branch, buildLogger);
            } else {
                git.checkout().checkoutBranch(buildLogger, branch, false, sourceCodeDirectory);
            }

            git.pull(sourceCodeDirectory).pullUpdatesFromRemoteRepository(buildLogger, repositoryUrl, branch);

            GitLogCommand gitLogCommand = git.log(sourceCodeDirectory, lastRevisionChecked);
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

    private void initialiseRemoteRepository(File sourceDirectory, String repositoryUrl, String branch, BuildLogger buildLogger) throws RepositoryException {
        log.info(buildLogger.addBuildLogEntry(sourceDirectory.getAbsolutePath() + " is empty. Creating new git repository"));
        try {
            git.repositoryClone().cloneUrl(buildLogger, repositoryUrl, sourceDirectory);
            git.checkout().checkoutBranch(buildLogger, branch, true, sourceDirectory);
        } catch (IOException e) {
            throw new RepositoryException("Failed to initialise repository", e);
        }
    }
}