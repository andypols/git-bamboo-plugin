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
        checking(new Expectations() {{
            one(buildLogger).addBuildLogEntry("Pulling source from branch 'some-branch' @ 'gitRepositoryUrl' into '" + SOURCE_CODE_DIRECTORY.getAbsolutePath() + "'.");
            one(commandExecutor).execute(new String[]{GIT_EXE, "pull", "gitRepositoryUrl", "some-branch:some-branch"}, SOURCE_CODE_DIRECTORY); will(returnValue("PULL COMMAND OUTPUT"));
            one(buildLogger).addBuildLogEntry("PULL COMMAND OUTPUT");
            one(commandExecutor).execute(new String[]{GIT_EXE, "submodule", "init"}, SOURCE_CODE_DIRECTORY); will(returnValue("SUBMODULE INIT COMMAND OUTPUT"));
            one(buildLogger).addBuildLogEntry("SUBMODULE INIT COMMAND OUTPUT");
            one(commandExecutor).execute(new String[]{GIT_EXE, "submodule", "update"}, SOURCE_CODE_DIRECTORY); will(returnValue("SUBMODULE UPDATE COMMAND OUTPUT"));
            one(buildLogger).addBuildLogEntry("SUBMODULE UPDATE COMMAND OUTPUT");
        }});

        ExecutorGitPullCommand gitPullCommand = new ExecutorGitPullCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);
        gitPullCommand.pullUpdatesFromRemoteRepository(buildLogger, "gitRepositoryUrl", "some-branch");
    }

    public void testThrowsAnIOExceptionIfGitPullReturnsAnError() throws IOException {
        checking(new Expectations() {{
            one(buildLogger).addBuildLogEntry("Pulling source from branch 'some-branch' @ 'gitRepositoryUrl' into '" + SOURCE_CODE_DIRECTORY.getAbsolutePath() + "'.");
            one(commandExecutor).execute(new String[]{GIT_EXE, "pull", "gitRepositoryUrl", "some-branch:some-branch"}, SOURCE_CODE_DIRECTORY); will(returnValue("fatal: something bad happened"));
        }});

        ExecutorGitPullCommand gitPullCommand = new ExecutorGitPullCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);
        try {
            gitPullCommand.pullUpdatesFromRemoteRepository(buildLogger, "gitRepositoryUrl", "some-branch");
            fail("Should throw an IOException");
        } catch (IOException e) {
            assertEquals("Could not pull from 'gitRepositoryUrl'. git-pull: fatal: something bad happened", e.getMessage());
        }
    }

    public void testThrowsAnIOExceptionIfGitSubmoduleInitReturnsAnError() throws IOException {
        checking(new Expectations() {
            {
                one(buildLogger).addBuildLogEntry("Pulling source from branch 'some-branch' @ 'gitRepositoryUrl' into '" + SOURCE_CODE_DIRECTORY.getAbsolutePath() + "'.");
                one(commandExecutor).execute(new String[]{GIT_EXE, "pull", "gitRepositoryUrl", "some-branch:some-branch"}, SOURCE_CODE_DIRECTORY);
                will(returnValue("PULL COMMAND OUTPUT"));
                one(buildLogger).addBuildLogEntry("PULL COMMAND OUTPUT");
                one(commandExecutor).execute(new String[]{GIT_EXE, "submodule", "init"}, SOURCE_CODE_DIRECTORY);
                will(returnValue("fatal: could not submodule init"));
            }
        });

        ExecutorGitPullCommand gitPullCommand = new ExecutorGitPullCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);
        try {
            gitPullCommand.pullUpdatesFromRemoteRepository(buildLogger, "gitRepositoryUrl", "some-branch");
            fail("Should throw an IOException");
        } catch (IOException e) {
            assertEquals("Could not pull from 'gitRepositoryUrl'. git-pull: fatal: could not submodule init", e.getMessage());
        }
    }

    public void testThrowsAnIOExceptionIfGitSubmoduleUpdateReturnsAnError() throws IOException {
        checking(new Expectations() {
            {
                one(buildLogger).addBuildLogEntry("Pulling source from branch 'some-branch' @ 'gitRepositoryUrl' into '" + SOURCE_CODE_DIRECTORY.getAbsolutePath() + "'.");
                one(commandExecutor).execute(new String[]{GIT_EXE, "pull", "gitRepositoryUrl", "some-branch:some-branch"}, SOURCE_CODE_DIRECTORY);
                will(returnValue("PULL COMMAND OUTPUT"));
                one(buildLogger).addBuildLogEntry("PULL COMMAND OUTPUT");
                one(commandExecutor).execute(new String[]{GIT_EXE, "submodule", "init"}, SOURCE_CODE_DIRECTORY);
                will(returnValue("SUBMODULE INIT COMMAND OUTPUT"));
                one(buildLogger).addBuildLogEntry("SUBMODULE INIT COMMAND OUTPUT");
                one(commandExecutor).execute(new String[]{GIT_EXE, "submodule", "update"}, SOURCE_CODE_DIRECTORY);
                will(returnValue("fatal: could not submodule update"));
            }
        });

        ExecutorGitPullCommand gitPullCommand = new ExecutorGitPullCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);
        try {
            gitPullCommand.pullUpdatesFromRemoteRepository(buildLogger, "gitRepositoryUrl", "some-branch");
            fail("Should throw an IOException");
        } catch (IOException e) {
            assertEquals("Could not pull from 'gitRepositoryUrl'. git-pull: fatal: could not submodule update", e.getMessage());
        }
    }
}