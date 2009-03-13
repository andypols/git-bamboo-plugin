package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.repository.AbstractRepository;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.repository.RepositoryException;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides GIT and GITHUB support for the Bamboo Build Server
 */
public class GitRepository extends AbstractRepository {
    private static final Log log = LogFactory.getLog(GitRepository.class);

    private static final String REPO_PREFIX = "repository.git.";

    public static final String NAME = "Git";
    public static final String KEY = "git";

    public static final String GIT_REPO_URL = REPO_PREFIX + "repositoryUrl";
    public static final String GIT_USERNAME = REPO_PREFIX + "username";
    public static final String GIT_PASSWORD = REPO_PREFIX + "userPassword";
    public static final String GIT_PASSPHRASE = REPO_PREFIX + "passphrase";
    public static final String GIT_AUTHTYPE = REPO_PREFIX + "authType";
    public static final String GIT_KEYFILE = REPO_PREFIX + "keyFile";

    /**
     * What's the name of the plugin - appears in the GUI dropdown
     *
     * @return The name
     */
    public String getName() {
        return NAME;
    }

    public String getHost() {
        return UNKNOWN_HOST;
    }

    public boolean isRepositoryDifferent(Repository repository) {
        return false;
    }

    /**
     * Where is the documentation and help about using GIT?
     *
     * @return The web url
     */
    public String getUrl() {
        return "http://github.com/guides/home";
    }

    public BuildChanges collectChangesSinceLastBuild(String string, String string1) throws RepositoryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String retrieveSourceCode(String string, String string1) throws RepositoryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void prepareConfigObject(BuildConfiguration buildConfiguration) {
    }
}