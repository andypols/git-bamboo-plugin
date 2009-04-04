package uk.co.pols.bamboo.gitplugin;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitImpl;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.repository.RepositoryException;
import uk.co.pols.bamboo.gitplugin.commands.GitPullCommand;
import uk.co.pols.bamboo.gitplugin.commands.GitLogCommand;

public class CmdLineGitClientTest extends MockObjectTestCase {
    private static final String GIT_EXE = "git";
    private static final String LAST_REVISION_CHECKED = "2009-03-22 01:09:25 +0000";
    private static final String REPOSITORY_URL = "repository.url";

    private BuildLogger buildLogger = mock(BuildLogger.class);
    private GitPullCommand gitPullCommand = mock(GitPullCommand.class);
    private GitLogCommand gitLogCommand = mock(GitLogCommand.class);
    private static final String NO_PREVIOUS_LATEST_UPDATE_TIME = null;
    private ArrayList<Commit> commits = new ArrayList<Commit>();
    private static final File SOURCE_CODE_DIRECTORY = new File("src");
    private CmdLineGitClient gitClient = gitClient();

    public void testSetsTheLatestUpdateToTheMostRecentCommitTheFirstTimeTheBuildIsRun() throws RepositoryException, IOException {
        checking(new Expectations() {{
            one(gitPullCommand).pullUpdatesFromRemoteRepository(buildLogger, REPOSITORY_URL);
            one(gitLogCommand).extractCommits(); will(returnValue(new ArrayList<Commit>()));
            one(gitLogCommand).getLastRevisionChecked(); will(returnValue(LAST_REVISION_CHECKED));
        }});

        String latestUpdate = gitClient.getLatestUpdate(buildLogger, REPOSITORY_URL, "plankey", NO_PREVIOUS_LATEST_UPDATE_TIME, commits, SOURCE_CODE_DIRECTORY);

        assertEquals(LAST_REVISION_CHECKED, latestUpdate);
        assertTrue(commits.isEmpty());
    }

    public void testDoesNotReturnAnyCommitsIfThereHaveNotBeenAnyCommentsSinceTheLastCheck() throws RepositoryException, IOException {
        checking(new Expectations() {{
            one(gitPullCommand).pullUpdatesFromRemoteRepository(buildLogger, REPOSITORY_URL);
            one(gitLogCommand).extractCommits(); will(returnValue(new ArrayList<Commit>()));
            one(gitLogCommand).getLastRevisionChecked(); will(returnValue(LAST_REVISION_CHECKED));
        }});

        String latestUpdate = gitClient.getLatestUpdate(buildLogger, REPOSITORY_URL, "plankey", LAST_REVISION_CHECKED, commits, SOURCE_CODE_DIRECTORY);

        assertEquals(LAST_REVISION_CHECKED, latestUpdate);
        assertTrue(commits.isEmpty());
    }

    public void testReturnsTheNewCommitsAndLatestCommitTimeIfThereAreNewCommentsSinceTheLastCheck() throws RepositoryException, IOException {
        final ArrayList<Commit> latestCommits = new ArrayList<Commit>();
        latestCommits.add(new CommitImpl());
        latestCommits.add(new CommitImpl());

        checking(new Expectations() {{
            one(gitPullCommand).pullUpdatesFromRemoteRepository(buildLogger, REPOSITORY_URL);
            one(gitLogCommand).extractCommits(); will(returnValue(latestCommits));
            one(gitLogCommand).getLastRevisionChecked(); will(returnValue("2009-03-25 01:09:25 +0000"));
        }});

        String latestUpdate = gitClient.getLatestUpdate(buildLogger, REPOSITORY_URL, "plankey", LAST_REVISION_CHECKED, commits, SOURCE_CODE_DIRECTORY);

        assertEquals("2009-03-25 01:09:25 +0000", latestUpdate);
        assertEquals(2, commits.size());
    }

    public void testWrapsIoExceptionsInRepositoryExceptions() throws RepositoryException, IOException {
        final IOException ioException = new IOException("EXPECTED EXCEPTION");

        checking(new Expectations() {{
            one(gitPullCommand).pullUpdatesFromRemoteRepository(buildLogger, REPOSITORY_URL); will(throwException(ioException));
        }});

        try {
            gitClient.getLatestUpdate(buildLogger, REPOSITORY_URL, "plankey", LAST_REVISION_CHECKED, commits, SOURCE_CODE_DIRECTORY);
        } catch (RepositoryException e) {
            assertEquals("Failed to get latest update", e.getMessage());
            assertSame(ioException, e.getCause());
        }
    }

    private CmdLineGitClient gitClient() {
        return new CmdLineGitClient(GIT_EXE) {
            protected GitPullCommand pullCommand(File sourceCodeDirectory) {
                return gitPullCommand;
            }

            protected GitLogCommand logCommand(File sourceCodeDirectory, String lastRevisionChecked) {
                return gitLogCommand;
            }
        };
    }
}