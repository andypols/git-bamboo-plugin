package uk.co.pols.bamboo.gitplugin.client.git.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;

import com.atlassian.bamboo.build.logger.BuildLogger;
import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;
import uk.co.pols.bamboo.gitplugin.client.git.commands.ExecutorGitRemoteCommand;
import uk.co.pols.bamboo.gitplugin.client.git.commands.GitRemoteCommand;

public class ExecutorGitRemoteCommandTest extends MockObjectTestCase {
    private static final File SOURCE_CODE_DIRECTORY = new File("source/directory");
    private static final String REPOSITORY_URL = "RemoteRepositoryUrl";
    private static final String REPOSITORY_BRANCH = "master";
    private static final String GIT_EXE = "git";

    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private BuildLogger buildLogger = mock(BuildLogger.class);

    public void testTracksARemoteRepository() throws IOException {
        GitRemoteCommand gitRemoteCommand = new ExecutorGitRemoteCommand(GIT_EXE, SOURCE_CODE_DIRECTORY, commandExecutor);

        checking(new Expectations() {{
            one(buildLogger).addBuildLogEntry("Running 'git remote add -t master origin 'RemoteRepositoryUrl'");
            one(commandExecutor).execute(new String[]{GIT_EXE, "remote", "add", "-t", REPOSITORY_BRANCH, "origin", REPOSITORY_URL}, SOURCE_CODE_DIRECTORY);
        }});

        gitRemoteCommand.add_origin(REPOSITORY_URL, REPOSITORY_BRANCH, buildLogger);
    }
}