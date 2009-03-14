package uk.co.pols.bamboo.gitplugin;

import junit.framework.TestCase;

public class GitRepositoryTest extends TestCase {
    GitRepository gitRepository = new GitRepository();

    public void testProvidesANameToAppearInTheGuiRepositoryDrownDown() {
        assertEquals("Git", gitRepository.getName());
    }
}
