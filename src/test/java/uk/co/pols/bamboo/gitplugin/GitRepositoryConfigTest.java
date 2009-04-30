package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.repository.AbstractRepository;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.jmock.integration.junit3.MockObjectTestCase;
import static uk.co.pols.bamboo.gitplugin.SampleCommitFactory.commitFile;
import static uk.co.pols.bamboo.gitplugin.SampleCommitFactory.commitWithFile;

public class GitRepositoryConfigTest extends MockObjectTestCase {
    private GitRepositoryConfig repositoryConfig = new GitRepositoryConfig();

    public void testEnsuresThatTheUserSpecifiesTheRepositoryUrl() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheBranch");
        buildConfiguration.setProperty(GitRepositoryConfig.PASSPHRASE, "passphrase");
        buildConfiguration.setProperty(GitRepositoryConfig.KEY_FILE, "some/key/file");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, GitRepositoryConfig.GIT_REPO_URL, "Please specify where the repository is located");
    }

    public void testEnsuresThatTheUserSpecifiesTheRepositoryBranch() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(GitRepositoryConfig.PASSPHRASE, "passphrase");
        buildConfiguration.setProperty(GitRepositoryConfig.KEY_FILE, "some/key/file");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, GitRepositoryConfig.GIT_BRANCH, "Please specify which branch you want to build");
    }

    public void testEnsuresThatTheRepositoryUrlIsAWellFormedUrl() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheBranch");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(AbstractRepository.WEB_REPO_URL, "An Invalid Url");
        buildConfiguration.setProperty(GitRepositoryConfig.PASSPHRASE, "passphrase");
        buildConfiguration.setProperty(GitRepositoryConfig.KEY_FILE, "some/key/file");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, AbstractRepository.WEB_REPO_URL, "This is not a valid url");
    }

    public void testEnsuresThatTheUserSpecifiesTheDeploymentKeyFile() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheBranch");
        buildConfiguration.setProperty(GitRepositoryConfig.PASSPHRASE, "passphrase");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, GitRepositoryConfig.KEY_FILE, "Please specify the GitHub deploy keyfile");
    }

    public void testEnsuresThatTheUserSpecifiesTheDeploymentKeyPassphrase() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheBranch");
        buildConfiguration.setProperty(GitRepositoryConfig.KEY_FILE, "some/key/file");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, GitRepositoryConfig.PASSPHRASE, "Please specify the deploy keyfile passphrase");
    }

    public void testAcceptsARepositoryAndBranchWithoutReportingAnyErrors() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "The Branch");
        buildConfiguration.setProperty(AbstractRepository.WEB_REPO_URL, "https://github.com/andypols/git-bamboo-plugin/tree/master");
        buildConfiguration.setProperty(GitRepositoryConfig.PASSPHRASE, "passphrase");
        buildConfiguration.setProperty(GitRepositoryConfig.KEY_FILE, "some/key/file");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertFalse(errorCollection.hasAnyErrors());
    }

    public void testReportsMultipleErrorsAtSameTime() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(4, errorCollection.getTotalErrors());
        assertEquals("Please specify where the repository is located", errorCollection.getFieldErrors().get(GitRepositoryConfig.GIT_REPO_URL));
        assertEquals("Please specify which branch you want to build", errorCollection.getFieldErrors().get(GitRepositoryConfig.GIT_BRANCH));
        assertEquals("Please specify the GitHub deploy keyfile", errorCollection.getFieldErrors().get(GitRepositoryConfig.KEY_FILE));
        assertEquals("Please specify the deploy keyfile passphrase", errorCollection.getFieldErrors().get(GitRepositoryConfig.PASSPHRASE));
    }

    public void testSavesTheRepositorySettingsInTheBuildConfiguration() {
        repositoryConfig.setRepositoryUrl("TheTopSecretBuildRepoUrl");
        repositoryConfig.setWebRepositoryUrl("TheRepoWebUrl");
        repositoryConfig.setBranch("TheBranch");
        repositoryConfig.setKeyFile("the/key/file");
        repositoryConfig.setPassphrase("passphrase");

        HierarchicalConfiguration hierarchicalConfiguration = repositoryConfig.toConfiguration(new HierarchicalConfiguration());

        assertEquals("TheTopSecretBuildRepoUrl", hierarchicalConfiguration.getProperty(GitRepositoryConfig.GIT_REPO_URL));
        assertEquals("TheBranch", hierarchicalConfiguration.getProperty(GitRepositoryConfig.GIT_BRANCH));
        assertEquals("TheRepoWebUrl", hierarchicalConfiguration.getProperty(AbstractRepository.WEB_REPO_URL));
        assertEquals("the/key/file", hierarchicalConfiguration.getProperty(GitRepositoryConfig.KEY_FILE));
        assertEquals("passphrase", hierarchicalConfiguration.getProperty(GitRepositoryConfig.PASSPHRASE));
    }

    public void testLoadsTheRepositorySettingsFromTheBuildConfiguration() {
        HierarchicalConfiguration buildConfiguration = new HierarchicalConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "TheTopSecretBuildRepoUrl");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheSpecialBranch");
        buildConfiguration.setProperty(AbstractRepository.WEB_REPO_URL, "WebRepositoryUrl");
        buildConfiguration.setProperty(GitRepositoryConfig.PASSPHRASE, "ThePassphrase");
        buildConfiguration.setProperty(GitRepositoryConfig.KEY_FILE, "TheKeyFile");

        repositoryConfig.populateFromConfig(buildConfiguration);

        assertEquals("TheSpecialBranch", repositoryConfig.getBranch());
        assertEquals("TheTopSecretBuildRepoUrl", repositoryConfig.getRepositoryUrl());
        assertEquals("WebRepositoryUrl", repositoryConfig.getWebRepositoryUrl());
        assertEquals("ThePassphrase", repositoryConfig.getPassphrase());
        assertEquals("TheKeyFile", repositoryConfig.getKeyFile());
    }

    public void testDefaultsToUsingTheMasterBranchOnNewPlans() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();

        repositoryConfig.addDefaultValues(buildConfiguration);

        assertEquals("master", buildConfiguration.getProperty(GitRepositoryConfig.GIT_BRANCH));
    }

    public void testTrimsWhiteSpaceOffTheRepositoryUrl() {
        repositoryConfig.setRepositoryUrl(" git@github.com:andypols/git-bamboo-plugin.git  ");

        assertEquals("git@github.com:andypols/git-bamboo-plugin.git", repositoryConfig.getRepositoryUrl());
    }

    public void testTrimsWhiteSpaceOffTheWebRepositoryUrl() {
        repositoryConfig.setWebRepositoryUrl(" https://github.com/andypols/git-bamboo-plugin/tree/master  ");

        assertEquals("https://github.com/andypols/git-bamboo-plugin/tree/master", repositoryConfig.getWebRepositoryUrl());
    }

    public void testHasWebBasedRepositoryAccessIfTheUserHasSpecifiedTheWebUrl() {
        repositoryConfig.setWebRepositoryUrl("https://github.com/andypols/git-bamboo-plugin/tree/master");

        assertTrue(repositoryConfig.hasWebBasedRepositoryAccess());
    }

    public void testDoesNotHaveWebBasedRepositoryAccessIfTheUserHasSpecifiedTheWebUrl() {
        assertFalse(repositoryConfig.hasWebBasedRepositoryAccess());
    }

    public void testDerivesTheTheCommitUrlFromTheRepositoryUrl() {
        repositoryConfig.setWebRepositoryUrl("https://github.com/andypols/git-bamboo-plugin");

        assertEquals("https://github.com/andypols/git-bamboo-plugin/commit/71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6", repositoryConfig.getWebRepositoryUrlForCommit(commitWithFile("71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6")));
    }

    public void testDerivesTheTheCommitFileUrlFromTheRepositoryUrl() {
        repositoryConfig.setWebRepositoryUrl("https://github.com/andypols/git-bamboo-plugin");

        assertEquals("https://github.com/andypols/git-bamboo-plugin/blob/71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6/src/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java", repositoryConfig.getWebRepositoryUrlForFile(commitFile("71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6")));
        assertEquals("https://github.com/andypols/git-bamboo-plugin/blob/71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6/src/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java", repositoryConfig.getWebRepositoryUrlForFile(commitFile("71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6")));
    }

    public void testLinksToTheGitHubCommitDiffPage() {
        repositoryConfig.setWebRepositoryUrl("https://github.com/andypols/git-bamboo-plugin");

        assertEquals("https://github.com/andypols/git-bamboo-plugin/commit/71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6", repositoryConfig.getWebRepositoryUrlForDiff(commitFile("71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6")));
    }

    public void testKnowIfUserSpecifiedABasedRepositoryAccess() {
        assertFalse(repositoryConfig.hasWebBasedRepositoryAccess());
    }

    public void testOnlySupportsGitHubWebRepositories() {
        repositoryConfig.setWebRepositoryUrl("https://some.private.repo.com/andypols/git-bamboo-plugin/tree/master");

        assertFalse(repositoryConfig.hasWebBasedRepositoryAccess());
    }

    public void testSupportsGitHubWebRepositoryLinks() {
        repositoryConfig.setWebRepositoryUrl(" https://github.com/andypols/git-bamboo-plugin/tree/master  ");

        assertTrue(repositoryConfig.hasWebBasedRepositoryAccess());
    }

    private void assertHasError(ErrorCollection errorCollection, String fieldKey, String errorMessage) {
        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(1, errorCollection.getTotalErrors());
        assertEquals(errorMessage, errorCollection.getFieldErrors().get(fieldKey));
    }
}