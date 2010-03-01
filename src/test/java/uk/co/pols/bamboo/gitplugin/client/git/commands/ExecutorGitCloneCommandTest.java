package uk.co.pols.bamboo.gitplugin.client.git.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.atlassian.bamboo.build.logger.BuildLogger;
import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;
import uk.co.pols.bamboo.gitplugin.client.git.commands.ExecutorGitCloneCommand;

public class ExecutorGitCloneCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = createTempDir();
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private final BuildLogger buildLogger = mock(BuildLogger.class);

    public void testExecutesTheCloneCommandInTheSourceCodesParentDirectoryAsCannot() throws IOException {
        ExecutorGitCloneCommand cloneCommand = new ExecutorGitCloneCommand(GIT_EXE, commandExecutor);

        checking(new Expectations() {{
            oneOf(buildLogger).addBuildLogEntry("Running 'git clone REPOSITORY_URL'");
            one(commandExecutor).execute(new String[]{GIT_EXE, "clone", "REPOSITORY_URL", SOURCE_CODE_DIRECTORY.getPath()}, SOURCE_CODE_DIRECTORY.getParentFile()); will(returnValue("clone output..."));
            oneOf(buildLogger).addBuildLogEntry("clone output...");
        }});

        cloneCommand.cloneUrl(buildLogger, "REPOSITORY_URL", SOURCE_CODE_DIRECTORY);
    }

    public void testRemovesTheSourceDirectoryBeforeCloningAsCannotCloneIntoAnExistingDirectory() throws IOException {
        ExecutorGitCloneCommand cloneCommand = new ExecutorGitCloneCommand(GIT_EXE, commandExecutor);
        SOURCE_CODE_DIRECTORY.mkdirs();

        checking(new Expectations() {{
            allowing(buildLogger);
            allowing(commandExecutor);
        }});

        cloneCommand.cloneUrl(buildLogger, "REPOSITORY_URL", SOURCE_CODE_DIRECTORY);

        assertFalse(SOURCE_CODE_DIRECTORY.exists());
    }

    private static File createTempDir() {
        final String baseTempPath = System.getProperty("java.io.tmpdir");

        Random rand = new Random();
        int randomInt = 1 + rand.nextInt();

        File tempDir = new File(baseTempPath + File.separator + "tempDir" + randomInt);

        tempDir.deleteOnExit();
        return tempDir;
    }
}
