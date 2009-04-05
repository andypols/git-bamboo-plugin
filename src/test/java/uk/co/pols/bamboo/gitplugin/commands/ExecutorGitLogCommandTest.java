package uk.co.pols.bamboo.gitplugin.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.atlassian.bamboo.commit.Commit;

public class ExecutorGitLogCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = new File("source/directory");
    private static final String DATE_OF_LAST_BUILD = "2009-03-13 01:27:52 +0000";
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);

    public void testGetsTheMostRecentLogItemIfNotCheckedLogBefore() throws IOException {
        GitLogCommand gitLogCommand = new ExtractorGitLogCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, null, commandExecutor);

        checking(new Expectations() {{
            one(commandExecutor).execute(new String[]{GIT_EXE, "log", "-1", "--numstat", "--date=iso8601"}, SOURCE_CODE_DIRECTORY); will(returnValue(mostRecentCommitLog));
        }});

        List<Commit> commits = gitLogCommand.extractCommits();

        assertEquals(1, commits.size());
        assertEquals("2008-03-13 01:27:52 +0000", gitLogCommand.getLastRevisionChecked());
    }

    public void testGetsTheLogsSinceTheLastBuild() throws IOException {
        GitLogCommand gitLogCommand = new ExtractorGitLogCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, DATE_OF_LAST_BUILD, commandExecutor);

        checking(new Expectations() {{
            one(commandExecutor).execute(new String[]{GIT_EXE, "log", "--numstat", "--date=iso8601", "--since=\"" + DATE_OF_LAST_BUILD + "\""}, SOURCE_CODE_DIRECTORY); will(returnValue(sampleLog));
        }});

        List<Commit> commits = gitLogCommand.extractCommits();

        assertEquals(3, commits.size());
        assertEquals("2009-03-13 01:27:52 +0000", gitLogCommand.getLastRevisionChecked());
    }

    private String mostRecentCommitLog =
            "commit 60f6a6cabe727b14897b4d98bca91ce646a07d3d\n" +
                    "Author: Andy Pols <andy@pols.co.uk>\n" +
                    "Date:   2008-03-13 01:27:52 +0000\n" +
                    "\n" +
                    "    Initial plugin - just Adds Git to the repository dropdown... does not actually do anything just yet!\n" +
                    "\n";

    private String sampleLog =
            "commit 60f6a6cabe727b14897b4d98bca91ce646a07d3d\n" +
                    "Author: Andy Pols <andy@pols.co.uk>\n" +
                    "Date:   2009-03-13 01:27:52 +0000\n" +
                    "\n" +
                    "    Initial plugin - just Adds Git to the repository dropdown... does not actually do anything just yet!\n" +
                    "\n" +
                    "commit 0ba53eb47ee4c79612fdf55f072952d8ef80b957\n" +
                    "Author: Andy Pols <andy@pols.co.uk>\n" +
                    "Date:   2009-03-13 01:26:14 +0000\n" +
                    "\n" +
                    "    ignore the java build files\n" +
                    "\n" +
                    "commit 1b014a81da79573aa960e7f0b22493da9fdb9310\n" +
                    "Author: Andy Pols <andy@pols.co.uk>\n" +
                    "Date:   2009-03-13 01:24:44 +0000\n" +
                    "\n" +
                    "    first commit\n";

}