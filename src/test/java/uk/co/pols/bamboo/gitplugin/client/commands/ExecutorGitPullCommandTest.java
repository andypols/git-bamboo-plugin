package uk.co.pols.bamboo.gitplugin.client.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class ExecutorGitPullCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = new File("source/directory");
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private final BuildLogger buildLogger = mock(BuildLogger.class);

    public void testExecuteAPullCommandAgainstTheRemoteBranch() throws IOException {
        ExecutorGitPullCommand gitPullCommand = new ExecutorGitPullCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);

        checking(new Expectations() {{
            one(buildLogger).addBuildLogEntry("Pulling source from branch 'some-branch' @ 'gitReopsitoryUrl' into '" + SOURCE_CODE_DIRECTORY.getAbsolutePath() + "'.");
            one(commandExecutor).execute(new String[]{GIT_EXE, "pull", "origin", "some-branch"}, SOURCE_CODE_DIRECTORY); will(returnValue("COMMAND OUTPUT"));
            one(buildLogger).addBuildLogEntry("COMMAND OUTPUT");
        }});

        gitPullCommand.pullUpdatesFromRemoteRepository(buildLogger, "gitReopsitoryUrl", "some-branch");
    }
}