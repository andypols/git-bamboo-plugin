package uk.co.pols.bamboo.gitplugin;

import junit.framework.TestCase;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.bamboo.utils.error.ErrorCollection;

public class GitRepositoryTest extends TestCase {
    private GitRepository gitRepository = new GitRepository();

    public void testProvidesANameToAppearInTheGuiRepositoryDrownDown() {
        assertEquals("Git", gitRepository.getName());
    }

    public void testProvidesALinkToTheGitHubGuidesPage() {
        assertEquals("http://github.com/guides/home", gitRepository.getUrl());
    }

    public void testEnsuresThatTheUserSpecifiesTheRepositoryUrl() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepository.GIT_BRANCH, "TheBranch");

        ErrorCollection errorCollection = gitRepository.validate(buildConfiguration);

        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(1, errorCollection.getTotalErrors());
        assertEquals("Please specify the build's Git Repository", errorCollection.getFieldErrors().get(GitRepository.GIT_REPO_URL));
    }

    public void testEnsuresThatTheUserSpecifiesTheRepositoryBranch() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepository.GIT_REPO_URL, "The Rep Url");

        ErrorCollection errorCollection = gitRepository.validate(buildConfiguration);

        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(1, errorCollection.getTotalErrors());
        assertEquals("Please specify which branch you want to build", errorCollection.getFieldErrors().get(GitRepository.GIT_BRANCH));
    }
}