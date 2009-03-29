package uk.co.pols.bamboo.gitplugin.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitImpl;

public class GitLogCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = new File("source/directory");
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private final BuildLogger buildLogger = mock(BuildLogger.class);

    public void testGetsTheMostRecentLogItemIfNotCheckedLogBefore() throws IOException {
        GitLogCommand gitLogCommand = new GitLogCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, null, commandExecutor);

//        checking(new Expectations() {{
//            one(commandExecutor).execute(new String[]{GIT_EXE, "log", "-1", "--date=iso8601"}, SOURCE_CODE_DIRECTORY); will(returnValue(mostRecentCommitLog));
//            one(buildLogger).addBuildLogEntry(mostRecentCommitLog);
//        }});
//
//        List<Commit> commits = gitLogCommand.extractCommits();
//
//        assertEquals(1, commits.size());
    }

    private String mostRecentCommitLog =
            "commit 60f6a6cabe727b14897b4d98bca91ce646a07d3d\n" +
                    "Author: Andy Pols <andy@pols.co.uk>\n" +
                    "Date:   2009-03-13 01:27:52 +0000\n" +
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

/*
package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.build.logger.BuildLogger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitLogCommand {
    private String gitExe;
    private File sourceCodeDirectory;
    private String lastRevisionChecked;
    private CommandExecutor commandExecutor;

    public GitLogCommand(String gitExe, File sourceCodeDirectory, String lastRevisionChecked, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.lastRevisionChecked = lastRevisionChecked;
        this.commandExecutor = commandExecutor;
    }

    public List<Commit> extractCommits(BuildLogger buildLogger) throws IOException {
        String textLog = commandExecutor.execute(getCommandLine(), sourceCodeDirectory);

        GitLogParser logParser = new GitLogParser(textLog);
        buildLogger.addBuildLogEntry(textLog);
        List<Commit> commits = logParser.extractCommits();

        lastRevisionChecked = logParser.getMostRecentCommitDate();

        return commits;
    }

    public String getLastRevisionChecked() {
        return lastRevisionChecked;
    }

    private String[] getCommandLine() {
        if (lastRevisionChecked != null) {
            return new String[]{gitExe, "log", "--date=iso8601", "--since=\"" + lastRevisionChecked + "\""};
        }
        return ;
    }
}
*/