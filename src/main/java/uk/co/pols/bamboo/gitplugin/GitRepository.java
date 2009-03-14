package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.repository.*;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.bamboo.commit.CommitFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides GIT and GITHUB support for the Bamboo Build Server
 */
public class GitRepository extends AbstractRepository implements WebRepositoryEnabledRepository, InitialBuildAwareRepository, MutableQuietPeriodAwareRepository {
    private static final Log log = LogFactory.getLog(GitRepository.class);

    public static final String NAME = "Git";
    public static final String KEY = "git";

    private static final String REPO_PREFIX = "repository.git.";
    public static final String GIT_REPO_URL = REPO_PREFIX + "repositoryUrl";
//    public static final String GIT_USERNAME = REPO_PREFIX + "username";
//    public static final String GIT_PASSWORD = REPO_PREFIX + "userPassword";
//    public static final String GIT_PASSPHRASE = REPO_PREFIX + "passphrase";
    public static final String GIT_AUTHTYPE = REPO_PREFIX + "authType";
//    public static final String GIT_KEYFILE = REPO_PREFIX + "keyFile";

    private String repositoryUrl;
    private String authType;

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

    /**
     * Specify the subversion repository we are using
     *
     * @param repositoryUrl The subversion repository
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = StringUtils.trim(repositoryUrl);
    }

    /**
     * Which repository URL are we using?
     *
     * @return The subversion repository
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public boolean hasWebBasedRepositoryAccess() {
        return false;
    }

    public void setWebRepositoryUrl(String string) {
    }

    public void setWebRepositoryUrlRepoName(String string) {
    }

    public String getWebRepositoryUrl() {
        return null;
    }

    public String getWebRepositoryUrlRepoName() {
        return null;
    }

    public String getWebRepositoryUrlForFile(CommitFile commitFile) {
        return null;
    }

    public void onInitialBuild(BuildContext buildContext) {
    }

    public void setQuietPeriodEnabled(boolean b) {
    }

    public void setQuietPeriod(int i) {
    }

    public void setMaxRetries(int i) {
    }

    public boolean isQuietPeriodEnabled() {
        return false;
    }

    public int getQuietPeriod() {
        return 0;
    }

    public int getMaxRetries() {
        return 0;
    }

    public List<NameValuePair> getAuthenticationTypes() {
        List<NameValuePair> types = new ArrayList<NameValuePair>();
        types.add(AuthenticationType.PASSWORD.getNameValue());
        types.add(AuthenticationType.SSH.getNameValue());
        types.add(AuthenticationType.SSL_CLIENT_CERTIFICATE.getNameValue());
        return types;
    }
}