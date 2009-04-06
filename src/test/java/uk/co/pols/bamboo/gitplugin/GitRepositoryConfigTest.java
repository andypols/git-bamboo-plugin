package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.jmock.integration.junit3.MockObjectTestCase;

public class GitRepositoryConfigTest extends MockObjectTestCase {
    private GitRepositoryConfig repositoryConfig = new GitRepositoryConfig();

    public void testEnsuresThatTheUserSpecifiesTheRepositoryUrl() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheBranch");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, GitRepositoryConfig.GIT_REPO_URL, "Please specify where the repository is located");
    }

    public void testEnsuresThatTheUserSpecifiesTheRepositoryBranch() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, GitRepositoryConfig.GIT_BRANCH, "Please specify which branch you want to build");
    }

    public void testAcceptsARepositoryAndBranchWithoutReportingAnyErrors() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "The Branch");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertFalse(errorCollection.hasAnyErrors());
    }

    public void testReportsMultipleErrorsAtSameTime() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(2, errorCollection.getTotalErrors());
        assertEquals("Please specify where the repository is located", errorCollection.getFieldErrors().get(GitRepositoryConfig.GIT_REPO_URL));
        assertEquals("Please specify which branch you want to build", errorCollection.getFieldErrors().get(GitRepositoryConfig.GIT_BRANCH));
    }

    public void testSavesTheRepositorySettingsInTheBuildConfiguration() {
        repositoryConfig.setRepositoryUrl("TheTopSecretBuildRepoUrl");
        repositoryConfig.setBranch("TheBranch");

        HierarchicalConfiguration hierarchicalConfiguration = repositoryConfig.toConfiguration(new HierarchicalConfiguration());

        assertEquals("TheTopSecretBuildRepoUrl", hierarchicalConfiguration.getProperty(GitRepositoryConfig.GIT_REPO_URL));
        assertEquals("TheBranch", hierarchicalConfiguration.getProperty(GitRepositoryConfig.GIT_BRANCH));
    }

    public void testLoadsTheRepositorySettingsFromTheBuildConfiguration() {
        HierarchicalConfiguration buildConfiguration = new HierarchicalConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "TheTopSecretBuildRepoUrl");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheSpecialBranch");

        repositoryConfig.populateFromConfig(buildConfiguration);

        assertEquals("TheSpecialBranch", repositoryConfig.getBranch());
        assertEquals("TheTopSecretBuildRepoUrl", repositoryConfig.getRepositoryUrl());
    }

    public void testDefaultsToUsingTheMasterBranchOnNewPlans() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();

        repositoryConfig.addDefaultValues(buildConfiguration);

        assertEquals("master", buildConfiguration.getProperty(GitRepositoryConfig.GIT_BRANCH));
    }

    private void assertHasError(ErrorCollection errorCollection, String fieldKey, String errorMessage) {
        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(1, errorCollection.getTotalErrors());
        assertEquals(errorMessage, errorCollection.getFieldErrors().get(fieldKey));
    }
}