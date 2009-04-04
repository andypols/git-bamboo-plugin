package uk.co.pols.bamboo.gitplugin;

import junit.framework.TestCase;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.repository.AbstractRepository;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.repository.RepositoryException;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.commit.Commit;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.util.ArrayList;

public class GitRepositoryTest extends MockObjectTestCase {
    private static final String PLAN_KEY = "plan-key";
    private static final File SRC_CODE_DIR = new File("test/src/code/directory");
    private static final String RESPOSITORY_URL = "RepositoryUrl";

    private GitClient gitClient = mock(GitClient.class);
    private BuildLoggerManager buildLoggerManager = mock(BuildLoggerManager.class);
    private BuildLogger buildLogger = mock(BuildLogger.class);
    private GitRepository gitRepository;

    protected void setUp() throws Exception {
        gitRepository = new GitRepository() {
            protected GitClient gitClient() {
                return gitClient;
            }

            public File getSourceCodeDirectory(String projectKey) {
                return SRC_CODE_DIR;
            }
        };
        gitRepository.setBuildLoggerManager(buildLoggerManager);
        gitRepository.setRepositoryUrl(RESPOSITORY_URL);
    }

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

        assertHasError(errorCollection, GitRepository.GIT_REPO_URL, "Please specify where the repository is located");
    }

    public void testEnsuresThatTheUserSpecifiesTheRepositoryBranch() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepository.GIT_REPO_URL, "The Rep Url");

        ErrorCollection errorCollection = gitRepository.validate(buildConfiguration);

        assertHasError(errorCollection, GitRepository.GIT_BRANCH, "Please specify which branch you want to build");
    }

    public void testAcceptsARepositoryAndBranchWithoutReportingAnyErrors() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();
        buildConfiguration.setProperty(GitRepository.GIT_REPO_URL, "The Rep Url");
        buildConfiguration.setProperty(GitRepository.GIT_BRANCH, "The Branch");

        ErrorCollection errorCollection = gitRepository.validate(buildConfiguration);

        assertFalse(errorCollection.hasAnyErrors());
    }

    public void testReportsMultipleErrorsAtSameTime() {
        BuildConfiguration buildConfiguration = new BuildConfiguration();

        ErrorCollection errorCollection = gitRepository.validate(buildConfiguration);

        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(2, errorCollection.getTotalErrors());
        assertEquals("Please specify where the repository is located", errorCollection.getFieldErrors().get(GitRepository.GIT_REPO_URL));
        assertEquals("Please specify which branch you want to build", errorCollection.getFieldErrors().get(GitRepository.GIT_BRANCH));
    }

    public void testSavesTheRepositorySettingsInTheBuildConfiguration() {
        gitRepository.setRepositoryUrl("TheTopSecretBuildRepoUrl");
        gitRepository.setBranch("TheBranch");

        HierarchicalConfiguration hierarchicalConfiguration = gitRepository.toConfiguration();

        assertEquals("TheTopSecretBuildRepoUrl", hierarchicalConfiguration.getProperty(GitRepository.GIT_REPO_URL));
        assertEquals("TheBranch", hierarchicalConfiguration.getProperty(GitRepository.GIT_BRANCH));
    }

    public void testLoadsTheRepositorySettingsFromTheBuildConfiguration() {
        HierarchicalConfiguration buildConfiguration = new HierarchicalConfiguration();
        buildConfiguration.setProperty(GitRepository.GIT_REPO_URL, "TheTopSecretBuildRepoUrl");
        buildConfiguration.setProperty(GitRepository.GIT_BRANCH, "TheSpecialBranch");

        gitRepository.populateFromConfig(buildConfiguration);

        assertEquals("TheSpecialBranch", gitRepository.getBranch());
        assertEquals("TheTopSecretBuildRepoUrl", gitRepository.getRepositoryUrl());
    }

    public void testClassesARepositoryOfADifferentTypeAsDifferent() {
        assertTrue(gitRepository.isRepositoryDifferent(new TestRepository()));
    }

    public void testClassesANullRepositoryAsDifferent() {
        assertTrue(gitRepository.isRepositoryDifferent(null));
    }

    public void testClassesAGitRepositoryWithADifferentUrlAsDifferent() {
        GitRepository repositoryToCompare = new GitRepository();
        repositoryToCompare.setRepositoryUrl("repositoryToCompareURL");

        gitRepository.setRepositoryUrl("gitRepositoryURL");

        assertTrue(gitRepository.isRepositoryDifferent(repositoryToCompare));
    }

    public void testClassesAGitRepositoryWithTheSameUrlAsBeingTheSame() {
        GitRepository repositoryToCompare = new GitRepository();
        repositoryToCompare.setRepositoryUrl("repositoryUrl");

        gitRepository.setRepositoryUrl("repositoryUrl");

        assertFalse(gitRepository.isRepositoryDifferent(repositoryToCompare));
    }

    public void testUsesAGitClientToDetectTheChangesSinceTheLastBuild() throws RepositoryException {
        checking(new Expectations() {{
            one(buildLoggerManager).getBuildLogger(PLAN_KEY); will(returnValue(buildLogger));
            one(gitClient).getLatestUpdate(buildLogger, RESPOSITORY_URL, PLAN_KEY, "time of previous build", new ArrayList<Commit>(), SRC_CODE_DIR); will(returnValue("time of this build"));
        }});

        BuildChanges buildChanges = gitRepository.collectChangesSinceLastBuild(PLAN_KEY, "time of previous build");

        assertEquals("time of this build", buildChanges.getVcsRevisionKey());
    }

    private void assertHasError(ErrorCollection errorCollection, String fieldKey, String errorMessage) {
        assertTrue(errorCollection.hasAnyErrors());
        assertEquals(1, errorCollection.getTotalErrors());
        assertEquals(errorMessage, errorCollection.getFieldErrors().get(fieldKey));
    }

    class TestRepository extends AbstractRepository {

        public String getName() {
            return null;
        }

        public String getHost() {
            return null;
        }

        public boolean isRepositoryDifferent(Repository repository) {
            return false;
        }

        public BuildChanges collectChangesSinceLastBuild(String string, String string1) throws RepositoryException {
            return null;
        }

        public String retrieveSourceCode(String string, String string1) throws RepositoryException {
            return null;
        }

        public void prepareConfigObject(BuildConfiguration buildConfiguration) {
        }
    }
}