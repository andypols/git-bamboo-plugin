package uk.co.pols.bamboo.gitplugin.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.build.logger.BuildLogger;

public class GitPullCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = new File("source/directory");
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private final BuildLogger buildLogger = mock(BuildLogger.class);

    public void testExecuteAPullCommandAgainstTheRemoteBranch() throws IOException {
        GitPullCommand gitPullCommand = new GitPullCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);

        checking(new Expectations() {{
            one(buildLogger).addBuildLogEntry("Pulling source from 'gitReopsitoryUrl' into '/Users/andy/projects/git/git-bamboo-plugin/source/directory'.");
            one(commandExecutor).execute(new String[]{GIT_EXE, "pull", "origin", "master"}, SOURCE_CODE_DIRECTORY); will(returnValue(""));
        }});

        gitPullCommand.pullUpdatesFromRemoteRepository(buildLogger, "gitReopsitoryUrl");
    }
}