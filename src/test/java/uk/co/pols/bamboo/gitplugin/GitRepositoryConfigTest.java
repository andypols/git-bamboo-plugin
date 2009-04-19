package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.bamboo.repository.AbstractRepository;
import com.atlassian.bamboo.commit.CommitImpl;
import com.atlassian.bamboo.commit.CommitFileImpl;
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

    public void testEnsuresThatTheRepositoryUrlIsAWellFormedUrl() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheBranch");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(AbstractRepository.WEB_REPO_URL, "An Invalid Url");

        ErrorCollection errorCollection = repositoryConfig.validate(new SimpleErrorCollection(), buildConfiguration);

        assertHasError(errorCollection, AbstractRepository.WEB_REPO_URL, "This is not a valid url");
    }

    public void testAcceptsARepositoryAndBranchWithoutReportingAnyErrors() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "The Branch");
        buildConfiguration.setProperty(AbstractRepository.WEB_REPO_URL, "https://github.com/andypols/git-bamboo-plugin/tree/master");

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
        repositoryConfig.setWebRepositoryUrl("TheRepoWebUrl");
        repositoryConfig.setBranch("TheBranch");

        HierarchicalConfiguration hierarchicalConfiguration = repositoryConfig.toConfiguration(new HierarchicalConfiguration());

        assertEquals("TheTopSecretBuildRepoUrl", hierarchicalConfiguration.getProperty(GitRepositoryConfig.GIT_REPO_URL));
        assertEquals("TheBranch", hierarchicalConfiguration.getProperty(GitRepositoryConfig.GIT_BRANCH));
        assertEquals("TheRepoWebUrl", hierarchicalConfiguration.getProperty(AbstractRepository.WEB_REPO_URL));
    }

    public void testLoadsTheRepositorySettingsFromTheBuildConfiguration() {
        HierarchicalConfiguration buildConfiguration = new HierarchicalConfiguration();
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_REPO_URL, "TheTopSecretBuildRepoUrl");
        buildConfiguration.setProperty(GitRepositoryConfig.GIT_BRANCH, "TheSpecialBranch");
        buildConfiguration.setProperty(AbstractRepository.WEB_REPO_URL, "WebRepositoryUrl");

        repositoryConfig.populateFromConfig(buildConfiguration);

        assertEquals("TheSpecialBranch", repositoryConfig.getBranch());
        assertEquals("TheTopSecretBuildRepoUrl", repositoryConfig.getRepositoryUrl());
        assertEquals("WebRepositoryUrl", repositoryConfig.getWebRepositoryUrl());
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

        assertEquals("https://github.com/andypols/git-bamboo-plugin/tree/master/commit/71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6", repositoryConfig.getWebRepositoryUrlForCommit(commitWithFile("71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6")));
    }

    public void testDerivesTheTheCommitFileUrlFromTheRepositoryUrl() {
        repositoryConfig.setWebRepositoryUrl("https://github.com/andypols/git-bamboo-plugin");

        assertEquals("https://github.com/andypols/git-bamboo-plugin/blob/71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6/src/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java", repositoryConfig.getWebRepositoryUrlForFile(commitFile("71b2bf41fb82a12ca3d4d34bd62568d9167dc6d6")));
    }

    private CommitImpl commitWithFile(String revision) {
        CommitImpl commit = new CommitImpl();
        commit.addFile(commitFile(revision));
        return commit;
    }

    private CommitFileImpl commitFile(String revision) {
        CommitFileImpl commitFile = new CommitFileImpl("src/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java");
        commitFile.setRevision(revision);
        return commitFile;
    }

    private void assertHasError(ErrorCollection errorCollection, String fieldKey, String errorMessage) {
        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(1, errorCollection.getTotalErrors());
        assertEquals(errorMessage, errorCollection.getFieldErrors().get(fieldKey));
    }
}