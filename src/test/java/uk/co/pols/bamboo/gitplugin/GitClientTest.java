package uk.co.pols.bamboo.gitplugin;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.repository.RepositoryException;
import uk.co.pols.bamboo.gitplugin.commands.GitPullCommand;
import uk.co.pols.bamboo.gitplugin.commands.GitLogCommand;

public class GitClientTest extends MockObjectTestCase {
    private static final String GIT_EXE = "git";
    private static final String LAST_REVISION_CHECKED = "2009-03-22 01:09:25 +0000";
    private static final String REPOSITORY_URL = "repository.url";

    private BuildLogger buildLogger = mock(BuildLogger.class);
    private GitPullCommand gitPullCommand = mock(GitPullCommand.class);
    private GitLogCommand gitLogCommand = mock(GitLogCommand.class);
    private static final String NO_PREVIOUS_LATEST_UPDATE_TIME = null;

    public void testSetsTheLatestUpdateToTheMostRecentCommitTheFirstTimeTheBuildIsRun() throws RepositoryException, IOException {
        GitClient gitClient = gitClient();

        checking(new Expectations() {{
            one(gitPullCommand).pullUpdatesFromRemoteRepository(buildLogger, REPOSITORY_URL);
            one(gitLogCommand).extractCommits(); will(returnValue(new ArrayList<Commit>()));
            one(gitLogCommand).getLastRevisionChecked(); will(returnValue(LAST_REVISION_CHECKED));
        }});

        String latestUpdate = gitClient.getLatestUpdate(buildLogger, REPOSITORY_URL, "plankey", NO_PREVIOUS_LATEST_UPDATE_TIME, new ArrayList<Commit>(), new File("src"));

        assertEquals(LAST_REVISION_CHECKED, latestUpdate);

        /*

    public String getLatestUpdate(BuildLogger buildLogger, String repositoryUrl, String planKey, String lastRevisionChecked, List<Commit> commits, File sourceCodeDirectory) throws RepositoryException {
        try {
            pullCommand(sourceCodeDirectory).pullUpdatesFromRemoteRepository(buildLogger, repositoryUrl);

            ExtractorGitLogCommand gitLogCommand = logCommand(sourceCodeDirectory, lastRevisionChecked);
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
*/
    }

    private GitClient gitClient() {
        return new GitClient(GIT_EXE) {
            protected GitPullCommand pullCommand(File sourceCodeDirectory) {
                return gitPullCommand;
            }

            protected GitLogCommand logCommand(File sourceCodeDirectory, String lastRevisionChecked) {
                return gitLogCommand;
            }
        };
    }
}
