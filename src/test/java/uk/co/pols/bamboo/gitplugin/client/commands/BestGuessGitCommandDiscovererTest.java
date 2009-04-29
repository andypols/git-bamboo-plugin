package uk.co.pols.bamboo.gitplugin.client.commands;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;

public class BestGuessGitCommandDiscovererTest extends MockObjectTestCase {
    private CommandExecutor commandExecutor = mock(CommandExecutor.class);
    private BestGuessGitCommandDiscoverer commandDiscoverer = new BestGuessGitCommandDiscoverer(commandExecutor);

    public void testLooksForTheCorrectPropertyName() {
        assertEquals("GIT_HOME", BestGuessGitCommandDiscoverer.GIT_HOME);
    }

    public void testUsesTheGitHomeEnvironmentVariableIfItExists() {
        System.setProperty(BestGuessGitCommandDiscoverer.GIT_HOME, "ENV/DEFINED/GIT/BIN");

        assertEquals("ENV/DEFINED/GIT/BIN", commandDiscoverer.gitCommand());
    }

    public void testUsesAWhichCommandToFindTheGitBinary() throws IOException {
        System.clearProperty(BestGuessGitCommandDiscoverer.GIT_HOME);

        checking(new Expectations() {{
            one(commandExecutor).execute(with(equal(new String[] {"which", "git"})), with(any(File.class))); will(returnValue("/some/bin/git                   "));
        }});

        assertEquals("Should trim whitespace off what unix returns", "/some/bin/git", commandDiscoverer.gitCommand());
    }

    public void testUsesADefaultGitBinaryLocationIfAllElseFails() throws IOException {
        System.clearProperty(BestGuessGitCommandDiscoverer.GIT_HOME);

        checking(new Expectations() {{
            one(commandExecutor).execute(with(equal(new String[] {"which", "git"})), with(any(File.class))); will(returnValue(""));
        }});

        assertEquals(BestGuessGitCommandDiscoverer.DEFAULT_GIT_EXE, commandDiscoverer.gitCommand());
    }
}
