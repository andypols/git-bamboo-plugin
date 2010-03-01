package uk.co.pols.bamboo.gitplugin.client.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.build.logger.BuildLogger;

public class ExecutorGitCheckoutCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = new File("source/directory");
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private final BuildLogger buildLogger = mock(BuildLogger.class);

    public void testCheckoutsAndANewBranch() throws IOException {
        ExecutorGitCheckoutCommand checkoutCommand = new ExecutorGitCheckoutCommand(GIT_EXE, commandExecutor);

        checking(new Expectations() {{
            oneOf(buildLogger).addBuildLogEntry("Running 'git checkout -b BRANCH'");
            one(commandExecutor).execute(new String[]{GIT_EXE, "checkout", "-b", "BRANCH"}, SOURCE_CODE_DIRECTORY);
        }});

        checkoutCommand.checkoutBranch(buildLogger, "BRANCH", true, SOURCE_CODE_DIRECTORY);
    }

    public void testCheckoutsAnExistingBranch() throws IOException {
        ExecutorGitCheckoutCommand checkoutCommand = new ExecutorGitCheckoutCommand(GIT_EXE, commandExecutor);

        checking(new Expectations() {{
            oneOf(buildLogger).addBuildLogEntry("Running 'git checkout BRANCH'");
            one(commandExecutor).execute(new String[]{GIT_EXE, "checkout", "BRANCH"}, SOURCE_CODE_DIRECTORY);
        }});

        checkoutCommand.checkoutBranch(buildLogger, "BRANCH", false, SOURCE_CODE_DIRECTORY);
    }
}