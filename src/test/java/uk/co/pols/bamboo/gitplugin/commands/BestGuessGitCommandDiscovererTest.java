package uk.co.pols.bamboo.gitplugin.commands;

import junit.framework.TestCase;

public class BestGuessGitCommandDiscovererTest extends TestCase {
    private BestGuessGitCommandDiscoverer commandDiscoverer = new BestGuessGitCommandDiscoverer();

    public void testLooksForTheCorrectPropertyName() {
        assertEquals("GIT_HOME", BestGuessGitCommandDiscoverer.GIT_HOME);    
    }

    public void testUsesTheGitHomeEnvironmentVariableIfItExists() {
        System.setProperty(BestGuessGitCommandDiscoverer.GIT_HOME, "ENV/DEFINED/GIT/BIN");

        assertEquals("ENV/DEFINED/GIT/BIN", commandDiscoverer.gitCommand());
    }

    public void testUsesADefaultGitBinaryLocationIfAllElseFails() {
        System.clearProperty(BestGuessGitCommandDiscoverer.GIT_HOME);

        assertEquals(BestGuessGitCommandDiscoverer.DEFAULT_GIT_EXE, commandDiscoverer.gitCommand());
    }
}
