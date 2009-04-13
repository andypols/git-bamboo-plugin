package uk.co.pols.bamboo.gitplugin;

import junit.framework.TestCase;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.repository.AbstractRepository;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.repository.RepositoryException;
import com.atlassian.bamboo.repository.cvsimpl.CVSRepository;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.commit.Commit;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.Expectations;

import java.io.File;
import java.util.ArrayList;

import uk.co.pols.bamboo.gitplugin.commands.GitCommandDiscoverer;

public class GitRepositoryTest extends MockObjectTestCase {
    private static final String PLAN_KEY = "plan-key";
    private static final File SRC_CODE_DIR = new File("test/src/code/directory");
    private static final String RESPOSITORY_URL = "RepositoryUrl";

    private GitRepositoryConfig gitRepositoryConfig = new GitRepositoryConfig();
    private GitClient gitClient = mock(GitClient.class);
    private BuildLoggerManager buildLoggerManager = mock(BuildLoggerManager.class);
    private BuildLogger buildLogger = mock(BuildLogger.class);
    private GitCommandDiscoverer commandDiscoverer = mock(GitCommandDiscoverer.class);
    private GitRepository gitRepository = gitRepository(false);

    public void testProvidesANameToAppearInTheGuiRepositoryDrownDown() {
        assertEquals("GitHub", gitRepository.getName());
    }

    public void testProvidesALinkToTheGitHubGuidesPage() {
        assertEquals("http://github.com/guides/home", gitRepository.getUrl());
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
            one(gitClient).getLatestUpdate(buildLogger, RESPOSITORY_URL, PLAN_KEY, "time of previous build", new ArrayList<Commit>(), SRC_CODE_DIR);
            will(returnValue("time of this build"));
        }});

        BuildChanges buildChanges = gitRepository.collectChangesSinceLastBuild(PLAN_KEY, "time of previous build");

        assertEquals("time of this build", buildChanges.getVcsRevisionKey());
    }

    public void testInitialisesTheRepositoryIfTheWorkspaceIsEmpty() throws RepositoryException {
        checking(new Expectations() {{
            one(buildLoggerManager).getBuildLogger(PLAN_KEY); will(returnValue(buildLogger));
            one(gitClient).initialiseRepository(SRC_CODE_DIR, PLAN_KEY, null, gitRepositoryConfig, true, buildLogger);
            will(returnValue("time of this build"));
        }});

        String timeOfLastCommmit = gitRepository(true).retrieveSourceCode(PLAN_KEY, null);

        assertEquals("time of this build", timeOfLastCommmit);
    }

    public void testChecksOutTheSourceCodeIfTheIfTheWorkspaceIsNotEmpty() throws RepositoryException {
        checking(new Expectations() {{
            one(buildLoggerManager).getBuildLogger(PLAN_KEY); will(returnValue(buildLogger));
            one(gitClient).initialiseRepository(SRC_CODE_DIR, PLAN_KEY, null, gitRepositoryConfig, false, buildLogger);
            will(returnValue("time of this build"));
        }});

        String timeOfLastCommmit = gitRepository(false).retrieveSourceCode(PLAN_KEY, null);

        assertEquals("time of this build", timeOfLastCommmit);
    }

    public void testARepositoryThatIsNotAGitRepositoryIsClearlyDifferent() {
        assertTrue(gitRepository(false).isRepositoryDifferent(new CVSRepository()));
    }

    public void testARepositoryWithADifferentRepositoryIsDifferent() {
        GitRepository gitRepository = gitRepository(false);
        gitRepository.setRepositoryUrl("one/url");

        GitRepository differentRepository = new GitRepository();
        differentRepository.setRepositoryUrl("other/url");

        assertTrue(gitRepository.isRepositoryDifferent(differentRepository));
    }

//    public void testStoresTheRepositoryUrlInTheProjectConfig() {
//        fail("TODO - the UI lets you add it, but then forgets as we're not saving it! ");
//        fail("Looks like all the other extra repo options may be broken too!??");
//    }
//
//    public void testInfersTheUrlFromTheProjectInformation() {
//        fail("TODO - may have to be some wizzy javascript??");
//    }

    private GitRepository gitRepository(final boolean isWorkspaceEmpty) {
        gitRepository = new GitRepository() {
            protected GitClient gitClient() {
                return gitClient;
            }

            public File getSourceCodeDirectory(String projectKey) {
                return SRC_CODE_DIR;
            }

            protected boolean isWorkspaceEmpty(File file) {
                return isWorkspaceEmpty;
            }

            protected GitRepositoryConfig gitRepositoryConfig() {
                return gitRepositoryConfig;
            }

            protected GitCommandDiscoverer gitCommandDiscoverer() {
                return commandDiscoverer;
            }
        };

        gitRepository.setBuildLoggerManager(buildLoggerManager);
        gitRepository.setRepositoryUrl(RESPOSITORY_URL);
        return gitRepository;
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