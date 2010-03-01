package uk.co.pols.bamboo.gitplugin.client.git.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.IOException;
import java.io.File;

import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;
import uk.co.pols.bamboo.gitplugin.client.git.commands.ExecutorWhichCommand;

public class ExecutorWhichCommandTest extends MockObjectTestCase {
    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);

    public void testExecuteWhichCommandToDiscoverTheGitBinary() throws IOException {
        ExecutorWhichCommand whichCommand = new ExecutorWhichCommand(commandExecutor);

        checking(new Expectations() {{
            one(commandExecutor).execute(with(equal(new String[]{"which", "git"})), with(any(File.class))); will(returnValue("/opt/local/bin/git"));
        }});

        assertEquals("/opt/local/bin/git", whichCommand.which("git"));
    }
}
