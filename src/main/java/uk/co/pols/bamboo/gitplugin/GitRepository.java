package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.repository.*;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.repository.RepositoryEventAware;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.bamboo.command.CommandException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * Provides GIT and GITHUB support for the Bamboo Build Server
 */
public class GitRepository extends AbstractRepository implements SelectableAuthenticationRepository, WebRepositoryEnabledRepository, InitialBuildAwareRepository, MutableQuietPeriodAwareRepository, RepositoryEventAware {

    private static final Log log = LogFactory.getLog(GitRepository.class);

    public static final String NAME = "Git";
    public static final String KEY = "git";

    private static final String REPO_PREFIX = "repository.git.";
    public static final String GIT_REPO_URL = REPO_PREFIX + "repositoryUrl";
    public static final String GIT_USERNAME = REPO_PREFIX + "username";
    public static final String GIT_PASSWORD = REPO_PREFIX + "userPassword";
    public static final String GIT_PASSPHRASE = REPO_PREFIX + "passphrase";
    public static final String GIT_AUTH_TYPE = REPO_PREFIX + "authType";
    public static final String GIT_KEYFILE = REPO_PREFIX + "keyFile";

    private String repositoryUrl;
    private String authType;
    private String keyFile;
    private String passphrase;

    public String getName() {
        return NAME;
    }

    public String getHost() {
        return UNKNOWN_HOST;
    }

    public boolean isRepositoryDifferent(Repository repository) {
        return false;
    }

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
        String repositoryKey = buildConfiguration.getString(SELECTED_REPOSITORY);

        String authType = buildConfiguration.getString(GIT_AUTH_TYPE);
        if (AuthenticationType.PASSWORD.getKey().equals(authType)) {
//            boolean svnPasswordChanged = buildConfiguration.getBoolean(TEMPORARY_SVN_PASSWORD_CHANGE);
//            if (svnPasswordChanged) {
//                String newPassword = buildConfiguration.getString(TEMPORARY_SVN_PASSWORD);
//                if (getKey().equals(repositoryKey)) {
//                    buildConfiguration.setProperty(SvnRepository.SVN_PASSWORD, stringEncrypter.get().encrypt(newPassword));
//                }
//            }
        } else if (AuthenticationType.SSH.getKey().equals(authType)) {
//            boolean passphraseChanged = buildConfiguration.getBoolean(TEMPORARY_SVN_PASSPHRASE_CHANGE);
//            if (passphraseChanged) {
//                String newPassphrase = buildConfiguration.getString(TEMPORARY_SVN_PASSPHRASE);
//                buildConfiguration.setProperty(SvnRepository.SVN_PASSPHRASE, stringEncrypter.get().encrypt(newPassphrase));
//            }
        } else if (AuthenticationType.SSL_CLIENT_CERTIFICATE.getKey().equals(authType)) {
//            if (buildConfiguration.getBoolean(TEMPORARY_SVN_SSL_PASSPHRASE_CHANGE)) {
//                String newPassphrase = buildConfiguration.getString(TEMPORARY_SVN_SSL_PASSPHRASE);
//                buildConfiguration.setProperty(SVN_SSL_PASSPHRASE, stringEncrypter.get().encrypt(newPassphrase));
//            }
        }

        // Disabling advanced will clear all advanced
//        if (!buildConfiguration.getBoolean(TEMPORARY_SVN_ADVANCED, false)) {
//            quietPeriodHelper.clearFromBuildConfiguration(buildConfiguration);
//            buildConfiguration.clearTree(USE_EXTERNALS);
//        }
    }

    public void populateFromConfig(HierarchicalConfiguration config) {
        super.populateFromConfig(config);

        setRepositoryUrl(config.getString(GIT_REPO_URL));
//        setUsername(config.getString(GIT_USERNAME));
        setAuthType(config.getString(GIT_AUTH_TYPE));

        if (AuthenticationType.PASSWORD.getKey().equals(authType)) {
//            setEncryptedPassword(config.getString(GIT_PASSWORD));
        } else if (AuthenticationType.SSH.getKey().equals(authType)) {
            setKeyFile(config.getString(GIT_KEYFILE));
            setEncryptedPassphrase(config.getString(GIT_PASSPHRASE));
        } else if (AuthenticationType.SSL_CLIENT_CERTIFICATE.getKey().equals(authType)) {
//            setKeyFile(config.getString(GIT_SSL_KEYFILE));
//            setEncryptedPassphrase(config.getString(GIT_SSL_PASSPHRASE));
        }

        setWebRepositoryUrl(config.getString(WEB_REPO_URL));
        setWebRepositoryUrlRepoName(config.getString(WEB_REPO_MODULE_NAME));

//        setUseExternals(config.getBoolean(USE_EXTERNALS, false));

//        final Map<String, String> stringMaps = ConfigUtils.getMapFromConfiguration(EXTERNAL_PATH_MAPPINGS2, config);
//        externalPathRevisionMappings = ConfigUtils.toLongMap(stringMaps);
//
//        quietPeriodHelper.populateFromConfig(config, this);
    }

    public HierarchicalConfiguration toConfiguration() {
        log.info("**************** GitRepository.toConfiguration");
        HierarchicalConfiguration configuration = super.toConfiguration();
        configuration.setProperty(GIT_REPO_URL, getRepositoryUrl());
//        configuration.setProperty(GIT_USERNAME, getUsername());
        configuration.setProperty(GIT_AUTH_TYPE, getAuthType());

        if (AuthenticationType.PASSWORD.getKey().equals(authType)) {
//            configuration.setProperty(GIT_PASSWORD, getEncryptedPassword());
        } else if (AuthenticationType.SSH.getKey().equals(authType)) {
            configuration.setProperty(GIT_KEYFILE, getKeyFile());
            configuration.setProperty(GIT_PASSPHRASE, getEncryptedPassphrase());
        } else if (AuthenticationType.SSL_CLIENT_CERTIFICATE.getKey().equals(authType)) {
//            configuration.setProperty(GIT_SSL_KEYFILE, getKeyFile());
//            configuration.setProperty(SVN_SSL_PASSPHRASE, getEncryptedPassphrase());
        }

        configuration.setProperty(WEB_REPO_URL, getWebRepositoryUrl());
        configuration.setProperty(WEB_REPO_MODULE_NAME, getWebRepositoryUrlRepoName());

//        configuration.setProperty(USE_EXTERNALS, isUseExternals());

        // If check externals && the externals map is empty, then inite it
//        if (isUseExternals() && externalPathRevisionMappings.isEmpty()) {
//            initExternalsRevisionMapping();
//        }

//        final Map<String, String> stringMap = ConfigUtils.toStringMap(externalPathRevisionMappings);
//        ConfigUtils.addMapToBuilConfiguration(EXTERNAL_PATH_MAPPINGS2, stringMap, configuration);

        // Quiet period
//        quietPeriodHelper.toConfiguration(configuration, this);
        return configuration;
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

    public String getKeyFile() {
        return keyFile;
    }

    public String getSubstitutedKeyFile() {
        return variableSubstitutionBean.substituteBambooVariables(keyFile);
    }

    public void setKeyFile(String myKeyFile) {
        this.keyFile = myKeyFile;
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

    public String getEncryptedPassphrase() {
        return passphrase;
    }

    public void setEncryptedPassphrase(String encryptedPassphrase) {
        passphrase = encryptedPassphrase;
    }

    public List<NameValuePair> getAuthenticationTypes() {
        List<NameValuePair> types = new ArrayList<NameValuePair>();
        types.add(AuthenticationType.SSH.getNameValue());
        types.add(AuthenticationType.PASSWORD.getNameValue());
        return types;
    }

    public void preRetrieveSourceCode(BuildContext buildContext) {
    }

    public void postRetrieveSourceCode(BuildContext buildContext) {
    }


 /*
cd working directory
git init
git remote add origin git@github.com:andypols/polsbusiness.git
git pull origin master
*/

    public static void main(String[] args) throws CommandException, IOException {
        File workingDirectory = new File("/Users/andy/projects/git/temp/newrepo");
        workingDirectory.mkdirs();

        Execute execute = new Execute(new PumpStreamHandler(System.out));
        execute.setWorkingDirectory(workingDirectory);
        execute.setCommandline(new String[] { "/opt/local/bin/git", "init" });
        execute.execute();

        execute.setCommandline(new String[] { "/opt/local/bin/git", "remote", "add", "origin", "git@github.com:andypols/git-bamboo-plugin.git" });
        execute.execute();

        execute.setCommandline(new String[] { "/opt/local/bin/git", "pull", "origin", "master" });
        execute.execute();
    }
}