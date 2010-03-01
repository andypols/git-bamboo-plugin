package uk.co.pols.bamboo.gitplugin.client.git.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;

import com.atlassian.bamboo.build.logger.BuildLogger;
import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;
import uk.co.pols.bamboo.gitplugin.client.git.commands.ExecutorGitInitCommand;
import uk.co.pols.bamboo.gitplugin.client.git.commands.GitInitCommand;

public class ExecutorGitInitCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = new File("source/directory");
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private BuildLogger buildLogger = mock(BuildLogger.class);

    public void testInitialisesANewGitRepository() throws IOException {
        GitInitCommand gitInitCommand = new ExecutorGitInitCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);

        checking(new Expectations() {{
            one(buildLogger).addBuildLogEntry("Running 'git init'");
            one(commandExecutor).execute(new String[]{GIT_EXE, "init"}, SOURCE_CODE_DIRECTORY);
        }});

        gitInitCommand.init(buildLogger);
    }
}