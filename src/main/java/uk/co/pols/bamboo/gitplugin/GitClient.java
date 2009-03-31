package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.repository.RepositoryException;

import java.util.List;
import java.io.IOException;
import java.io.File;

import uk.co.pols.bamboo.gitplugin.commands.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

public class GitClient {
    private static final Log log = LogFactory.getLog(GitClient.class);
    private String gitExe;

    public GitClient(String gitExe) {
        this.gitExe = gitExe;
    }

    public void initialiseRemoteRepository(File sourceDirectory, String repositoryUrl, BuildLogger buildLogger) throws RepositoryException {
        try {
            Execute execute = new Execute(new PumpStreamHandler(System.out));
            execute.setWorkingDirectory(sourceDirectory);

            sourceDirectory.mkdirs();
            log.info(buildLogger.addBuildLogEntry(sourceDirectory.getAbsolutePath() + " is empty. Creating new git repository '" + gitExe + " init'"));
            log.info(buildLogger.addBuildLogEntry("'" + gitExe + " init'"));
            execute.setCommandline(new String[]{gitExe, "init"});
            execute.execute();

            log.info(buildLogger.addBuildLogEntry("'" + gitExe + " remote add origin '" + repositoryUrl));
            execute.setCommandline(new String[]{gitExe, "remote", "add", "origin", repositoryUrl});
            execute.execute();
        } catch (IOException e) {
            throw new RepositoryException("Failed to initialise repository", e);
        }
    }

    public String getLatestUpdate(BuildLogger buildLogger, String repositoryUrl, String planKey, String lastRevisionChecked, List<Commit> commits, File sourceCodeDirectory) throws RepositoryException {
        try {
            pullCommand(sourceCodeDirectory).pullUpdatesFromRemoteRepository(buildLogger, repositoryUrl);

            GitLogCommand gitLogCommand = logCommand(sourceCodeDirectory, lastRevisionChecked);
            List<Commit> gitCommits = gitLogCommand.extractCommits();
            String latestRevisionOnServer = gitLogCommand.getLastRevisionChecked();
            if (lastRevisionChecked == null) {
                log.info("Never checked logs for '" + planKey + "' on path '" + repositoryUrl + "'  setting latest revision to " + latestRevisionOnServer);
                return latestRevisionOnServer;
            }
            if (!latestRevisionOnServer.equals(lastRevisionChecked)) {
                log.info("Collecting changes for '" + planKey + "' on path '" + repositoryUrl + "' since " + lastRevisionChecked);
                commits.addAll(gitCommits);
            }

            return latestRevisionOnServer;
        } catch (IOException e) {
            throw new RepositoryException("Failed to get latest update", e);
        }
    }

    protected GitPullCommand pullCommand(File sourceCodeDirectory) {
        return new ExecutorGitPullCommand(gitExe, sourceCodeDirectory, new AntCommandExecutor());
    }

    protected GitLogCommand logCommand(File sourceCodeDirectory, String lastRevisionChecked) {
        return new ExtractorGitLogCommand(gitExe, sourceCodeDirectory, lastRevisionChecked, new AntCommandExecutor());
    }
}
