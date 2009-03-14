package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.repository.*;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.repository.RepositoryEventAware;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.utils.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void addDefaultValues(BuildConfiguration buildConfiguration) {
        super.addDefaultValues(buildConfiguration);
        log.info("***************** GitRepository.addDefaultValues");
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
        log.info("*********** GitRepository.prepareConfigObject");
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
        log.info("********** GitRepository.setRepositoryUrl");
        this.repositoryUrl = StringUtils.trim(repositoryUrl);
    }

    /**
     * Which repository URL are we using?
     *
     * @return The subversion repository
     */
    public String getRepositoryUrl() {
        log.info("****** GitRepository.getRepositoryUrl");
        return repositoryUrl;
    }

    public String getAuthType() {
        log.info("****** GitRepository.getAuthType");
        return authType;
    }

    public void setAuthType(String authType) {
        log.info("****** GitRepository.setAuthType");
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
        types.add(AuthenticationType.SSL_CLIENT_CERTIFICATE.getNameValue());
        return types;
    }

    public void preRetrieveSourceCode(BuildContext buildContext) {
        log.info("****************** GitRepository.preRetrieveSourceCode");
    }

    public void postRetrieveSourceCode(BuildContext buildContext) {
        log.info("****************** GitRepository.postRetrieveSourceCode");
    }
}

/*
package com.atlassian.bamboo.repository.svn;

import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.author.AuthorImpl;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.commit.CommitFileImpl;
import com.atlassian.bamboo.commit.CommitImpl;
import com.atlassian.bamboo.repository.*;
import com.atlassian.bamboo.security.EncryptionException;
import com.atlassian.bamboo.security.StringEncrypter;
import com.atlassian.bamboo.utils.SystemProperty;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildChangesImpl;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.repository.RepositoryEventAware;
import com.opensymphony.util.UrlUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSLAuthentication;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.dav.http.DefaultHTTPConnectionFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.SVNExternal;
import org.tmatesoft.svn.core.internal.wc.admin.SVNAdminAreaFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class provides a Subversion repository for Bamboo.
public class SvnRepository extends AbstractRepository implements SelectableAuthenticationRepository, WebRepositoryEnabledRepository, InitialBuildAwareRepository, MutableQuietPeriodAwareRepository, RepositoryEventAware
{
    private static final Log log = LogFactory.getLog(SvnRepository.class);

    /**
     * Defines depth of resolving svn:externals
    private static final SVNDepth externalResolutionDepth = SVNDepth.IMMEDIATES;

    /**
     * Nasty singleton initialiser block to configure the library to work with a repository either via svn:// (and
     * svn+ssh://) or via http:// (and https://)
    static
    {
        /* for DAV (over http and https) *
        DAVRepositoryFactory.setup(
                new DefaultHTTPConnectionFactory(
                        null/*File spoolDrirectory,
                        SystemProperty.SVN_SPOOL_TO_FILE.getValue(true),
                        null/* default HTTP charset, US-ASCII *));

        /* for SVN (over svn and svn+ssh)
        SVNRepositoryFactoryImpl.setup();

        FSRepositoryFactory.setup();

        // ensure old wc are not upgraded to 1.5
        SVNAdminAreaFactory.setUpgradeEnabled(false);

        // check for flag, if exists wc will get created as 1.4
        boolean use14compatability = SystemProperty.SVN_14_COMPATIBLE.getValue(false);
        if (use14compatability)
        {
            SVNAdminAreaFactory.setSelector(new SVN14Selector());
        }

    }

    // ------------------------------------------------------------------------------------------------------- Constants
    public static final String NAME = "Subversion";
    public static final String KEY = "svn";

    private static final String REPO_PREFIX       = "repository.svn.";
    public static final String SVN_REPO_URL       = REPO_PREFIX + "repositoryUrl";
    public static final String SVN_USERNAME       = REPO_PREFIX + "username";
    public static final String SVN_AUTH_TYPE      = REPO_PREFIX + "authType";
    public static final String SVN_PASSWORD       = REPO_PREFIX + "userPassword";
    public static final String SVN_KEYFILE        = REPO_PREFIX + "keyFile";
    public static final String SVN_PASSPHRASE     = REPO_PREFIX + "passphrase";
    public static final String SVN_SSL_KEYFILE    = REPO_PREFIX + "sslKeyFile";
    public static final String SVN_SSL_PASSPHRASE = REPO_PREFIX + "sslPassphrase";


    private static final String USE_EXTERNALS = REPO_PREFIX + "useExternals";

    private static final String TEMPORARY_SVN_ADVANCED              = "temporary.svn.advanced";
    private static final String TEMPORARY_SVN_PASSWORD              = "temporary.svn.password";
    private static final String TEMPORARY_SVN_PASSWORD_CHANGE       = "temporary.svn.passwordChange";
    private static final String TEMPORARY_SVN_PASSPHRASE            = "temporary.svn.passphrase";
    private static final String TEMPORARY_SVN_PASSPHRASE_CHANGE     = "temporary.svn.passphraseChange";
    private static final String TEMPORARY_SVN_SSL_PASSPHRASE        = "temporary.svn.sslPassphrase";
    private static final String TEMPORARY_SVN_SSL_PASSPHRASE_CHANGE = "temporary.svn.sslPassphraseChange";

    private static final String EXTERNAL_PATH_MAPPINGS2 = REPO_PREFIX + "externalsToRevisionMappings";

    public static final ISVNOptions DEFAULT_SVN_OPTIONS = SVNWCUtil.createDefaultOptions(true);

    // ------------------------------------------------------------------------------------------------- Type Properties
    private String repositoryUrl;
    private String webRepositoryUrl;
    private String username;
    private String password;
    private String webRepositoryUrlRepoName;
    private String authType;
    private boolean useExternals;

    // Quiet Period
    private final QuietPeriodHelper quietPeriodHelper = new QuietPeriodHelper(REPO_PREFIX);
    private boolean quietPeriodEnabled = false;
    private int quietPeriod = QuietPeriodHelper.DEFAULT_QUIET_PERIOD;
    private int maxRetries = QuietPeriodHelper.DEFAULT_MAX_RETRIES;

    /**
     * Maps the path to the latest checked revision
     *
    private Map<String, Long> externalPathRevisionMappings = new HashMap<String, Long>();

    // ---------------------------------------------------------------------------------------------------- Dependencies

    private static final ThreadLocal<StringEncrypter> stringEncrypter = new ThreadLocal<StringEncrypter>() {

        protected StringEncrypter initialValue()
        {
            return new StringEncrypter();
        }

    };

    private transient SVNClientManagerFactory svnClientManagerFactory;

    /**
     * Cached externals
     *
    private transient Map<String, SVNExternal> svnExternals;
    private transient final Lock externalsLock = new ReentrantLock();

    // ---------------------------------------------------------------------------------------------------- Constructors

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public void addDefaultValues(@NotNull BuildConfiguration buildConfiguration)
    {
        super.addDefaultValues(buildConfiguration);
        quietPeriodHelper.addDefaultValues(buildConfiguration);
    }

    @NotNull
    public synchronized BuildChanges collectChangesSinceLastBuild(@NotNull String planKey, @NotNull String lastVcsRevisionKey) throws RepositoryException
    {
        try
        {
            Long lastRevisionChecked = Long.valueOf(lastVcsRevisionKey);

            SVNURL repositorySvnUrl = getSubstitutedSvnUrl();

            final List<Commit> commits = new ArrayList<Commit>();

            lastRevisionChecked = detectCommitsForUrl(repositorySvnUrl, lastRevisionChecked, commits, planKey);

            // If check externals
            if (isUseExternals())
            {
                // Collect further changes
                for (Map.Entry<String, SVNExternal> externalEntry : getExternals().entrySet())
                {
                    final String path = externalEntry.getKey();
                    final SVNExternal external = externalEntry.getValue();
                    final SVNURL svnUrl = external.getResolvedURL();

                    final Long latestRevisionCheckedForExternal = externalPathRevisionMappings.get(path);

                    // detect commits for external URL when one of the following is true:
                    //   latestRevisionCheckedForExternal == null
                    //   external.getRevision() == SVNRevision.HEAD
                    //   external.getRevision() > latestRevisionCheckedForExternal
                    if (null == latestRevisionCheckedForExternal ||
                        external.getRevision() == SVNRevision.HEAD ||
                        (external.isRevisionExplicit() && external.getRevision().getNumber() > latestRevisionCheckedForExternal))
                    {
                        log.info("Checking externals at path '" + path + "' with SVN URL '" + svnUrl + "' from revision " + latestRevisionCheckedForExternal);
                        final Long revisionOfExternal = detectCommitsForUrl(svnUrl, latestRevisionCheckedForExternal, commits, planKey);

                        externalPathRevisionMappings.put(path, revisionOfExternal);
                    }
                }
            }

            return new BuildChangesImpl(String.valueOf(lastRevisionChecked), commits);
        }
        catch (SVNException e)
        {
            throw new RepositoryException("Build '" + planKey + "' failed to check SVN repository", e);
        }
    }

    /**
     *
     * @return
     * @throws SVNException
     *
    private Map<String, SVNExternal> getExternals() throws SVNException
    {
        try
        {
            externalsLock.lock();
            if (svnExternals == null)
            {
                svnExternals = new HashMap<String, SVNExternal>();
                discoverExternals(getSubstitutedSvnUrl(), "");
            }
        }
        finally
        {
            externalsLock.unlock();
        }

        return svnExternals;
    }

    /**
     * Detects the commits for the given repositpry since the lastRevisionChecked revision and HEAD for that URL
     *
     * @param repositorySvnUrl - the SVN URL to check
     * @param lastRevisionChecked - latest revision checked for this URL. Null if never checked
     * @param commits - the commits are added to this list
     * @param planKey - used for debugging only
     * @return
     * @throws SVNException
     *
    @NotNull
    private Long detectCommitsForUrl(@NotNull SVNURL repositorySvnUrl, @Nullable final Long lastRevisionChecked, @NotNull final List<Commit> commits, @NotNull String planKey) throws SVNException
    {
        SVNClientManager clientManager = null;

        try
        {
            clientManager = getSvnClientManager();

            final SVNRepository repository = clientManager.createRepository(repositorySvnUrl, true);
            long latestRevisionOnServer = repository.getLatestRevision();
            //log.info("latestRevisionOnServer: " + latestRevisionOnServer + " (" + repositorySvnUrl + ")");
            if (lastRevisionChecked == null)
            {
                log.info("Never checked logs for '" + planKey + "' on path '" + repositorySvnUrl + "'  setting latest revision to " + latestRevisionOnServer);
                return latestRevisionOnServer;
            }

            if (latestRevisionOnServer != lastRevisionChecked)
            {
                long startRevision = lastRevisionChecked + 1; // so that we don't get dupe logs
                log.info("Collecting changes for '" + planKey + "' on path '" + repositorySvnUrl + "' from version " + startRevision + " to " + latestRevisionOnServer);

                SVNLogClient logClient = clientManager.getLogClient();

                // Needs to be HEAD due to BAM-2239
                final SVNRevision endRevision = SVNRevision.HEAD;
                logClient.doLog(repositorySvnUrl, null, SVNRevision.create(startRevision), SVNRevision.create(startRevision), endRevision, true, true, 0, new ISVNLogEntryHandler()
                {
                    public void handleLogEntry(SVNLogEntry logEntry)
                    {
                        CommitImpl commit = new CommitImpl();
                        String authorName = logEntry.getAuthor();

                        // it is possible to have commits with empty committer. BAM-2945
                        if (StringUtils.isBlank(authorName))
                        {
                            log.info("Author name is empty for " + logEntry.toString());
                            authorName = Author.UNKNOWN_AUTHOR;
                        }
                        commit.setAuthor(new AuthorImpl(authorName));
                        commit.setDate(logEntry.getDate());
                        commit.setComment(logEntry.getMessage());
                        // @TODO add a revision for the commit

                        List<CommitFile> files = new ArrayList<CommitFile>();
                        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
                        final String revision = String.valueOf(logEntry.getRevision());

                        for (Map.Entry<String, SVNLogEntryPath> entry : changedPaths.entrySet())
                        {
                            String path = entry.getKey();
                            SVNLogEntryPath logEntryPath = entry.getValue();// @TODO we can really do something with the status hear
                            CommitFileImpl commitFile = new CommitFileImpl();
                            commitFile.setName(path);
                            commitFile.setRevision(revision);

                            files.add(commitFile);
                        }
                        commit.setFiles(files);

                        commits.add(commit);
                    }
                });
            }


            return latestRevisionOnServer;
        }
        finally
        {
            dispose(clientManager);
        }
    }

    @NotNull
    public String retrieveSourceCode(@NotNull String planKey, @Nullable String vcsRevisionKey) throws RepositoryException
    {
        SVNClientManager clientManager = null;
        try
        {
            clientManager = getSvnClientManager();
            return retrieveSourceCodeWithException(planKey, vcsRevisionKey, clientManager);
        }
        catch (SVNException e)
        {
            if (isSvnLockException(e) && clientManager != null)
            {
                try
                {
                    final BuildLogger buildLogger = buildLoggerManager.getBuildLogger(planKey);
                    log.warn(buildLogger.addErrorLogEntry("Subversion repository for " + planKey + " is locked, attempting a subversion clean up."));

                    cleanBuildRepository(planKey, buildLogger, getSourceCodeDirectory(planKey), e);

                    return retrieveSourceCodeWithException(planKey, vcsRevisionKey, clientManager);
                }
                catch (SVNException e1)
                {
                    throw new RepositoryException("Unable to retrieve source code to '" + vcsRevisionKey + "' for '" + planKey + "': " + e.getMessage(), e);
                }
            }
            else if (isRecoverableException(e) && clientManager != null)
            {
                try
                {
                    final BuildLogger buildLogger = buildLoggerManager.getBuildLogger(planKey);
                    log.warn(buildLogger.addErrorLogEntry("Subversion repository for " + planKey + " failed to update with: " + e.getMessage() + ". Attempting a clean checkout..."));

                    // Clean directory
                    FileUtils.cleanDirectory(getSourceCodeDirectory(planKey));

                    return retrieveSourceCodeWithException(planKey, vcsRevisionKey, clientManager);
                }
                catch (Exception e1)
                {
                    throw new RepositoryException("Unable to retrieve source code to '" + vcsRevisionKey + "' for '" + planKey + "': " + e.getMessage(), e);
                }
            }
            else
            {
                throw new RepositoryException("Unable to retrieve source code to '" + vcsRevisionKey + "' for '" + planKey + "': " + e.getMessage(), e);
            }
        }
        finally
        {
            dispose(clientManager);
        }
    }

    String retrieveSourceCodeWithException(String planKey, String vcsRevisionKey, SVNClientManager svnClientManager) throws SVNException, RepositoryException
    {
        final SVNRevision revision;
        if (vcsRevisionKey != null)
        {
            Long vcsRevision = Long.parseLong(vcsRevisionKey);
            revision = SVNRevision.create(vcsRevision);
        }
        else
        {
            // Checkout latest source
            SVNURL repositorySvnUrl = getSubstitutedSvnUrl();
            long latestRevisionOnServer = svnClientManager.createRepository(repositorySvnUrl, true).getLatestRevision();
            vcsRevisionKey = String.valueOf(latestRevisionOnServer);
            revision = SVNRevision.HEAD;
        }

        File sourceDirectory = getSourceCodeDirectory(planKey);

        BuildLogger buildLogger = buildLoggerManager.getBuildLogger(planKey);


        if (isWorkspaceEmpty(sourceDirectory))
        {
            SVNURL svnUrl = getSubstitutedSvnUrl();
            log.info(buildLogger.addBuildLogEntry("Working directory '" + sourceDirectory.getAbsolutePath() + "' is empty. Checking out SVN URL '" + svnUrl +"'"));
            checkout(svnUrl, revision, sourceDirectory, true, buildLogger);
        }
        else
        {
            log.info(buildLogger.addBuildLogEntry("Source found at  '" + sourceDirectory.getAbsolutePath() + "'. Updating source..."));
            update(sourceDirectory, revision, true, buildLogger);
        }

        return vcsRevisionKey;
    }

    /**
     * Checks if the SVN URL has changed
     *
     * @param buildContext
     *
    public void preRetrieveSourceCode(@NotNull final BuildContext buildContext)
    {
        SVNClientManager clientManager = null;
        try
        {
            final String planKey = buildContext.getPlanKey();
            final File directory = getSourceCodeDirectory(planKey);
            if (!isWorkspaceEmpty(directory))
            {
                // Has files
                clientManager = getSvnClientManager();
                final SVNWCClient wcClient = clientManager.getWCClient();

                final SVNInfo svnInfo = wcClient.doInfo(directory, null);
                final SVNURL localSvnRepositoryPath = svnInfo.getURL();
                final SVNURL actualUrl = getSubstitutedSvnUrl();
                if (localSvnRepositoryPath != null && !localSvnRepositoryPath.equals(actualUrl))
                {
                    final BuildLogger buildLogger = buildLoggerManager.getBuildLogger(planKey);
                    log.info(buildLogger.addBuildLogEntry(
                            "Existing source path at '" + directory.getAbsolutePath() + "' is '" + localSvnRepositoryPath + "'" +
                            " and differs from '" + actualUrl + "'"));
                    setReferencesDifferentRepository(true);
                }
            }
        }
        catch (Exception e)
        {
            log.warn("Exception while detecting whether source code differs. Ignoring...", e);
        }
        finally
        {
            dispose(clientManager);
        }

    }

    public void postRetrieveSourceCode(@NotNull final BuildContext buildContext)
    {
    }

    /**
     * Check if provided exception object is related to svn locking problem.
     *
     * @param e Exception to be analyzed
     * @return True if SVNException e seem to be related to locking problem.
     *
    private boolean isSvnLockException(@NotNull SVNException e)
    {
        final String [] exceptionMsg = {
                "locked; try performing 'cleanup'"
        };
        return StringUtils.indexOfAny(e.getMessage(), exceptionMsg) != -1;
    }

    /**
     * Check if provided exception object identifies recoverable exception.
     *
     * @param e Exception to be analyzed.
     * @return True if exception is recognized as the one we can recover from.
     *
    protected boolean isRecoverableException(@NotNull SVNException e)
    {
        final String [] exceptionMsg = {
                "object of the same name already exists",
                "containing working copy admin area is missing",
                "Failed to load properties from disk",
                "Checksum mismatch",
                "Can't open file",
                "is not a working copy"
        };
        return StringUtils.indexOfAny(e.getMessage(), exceptionMsg) != -1;
    }

    /**
     * Recursively discover all the externals (svn:externals) in the given SVN path.
     * Operation is executed on a remote repository not on the local copy.
     *
     * @param rootUrl      SVN repository URL to be analyzed
     * @param relativePath Path relative to the repository root
     * @throws SVNException
     *
    private void discoverExternals(final SVNURL rootUrl, @NotNull final String relativePath) throws SVNException
    {
        SVNClientManager clientManager = null;

        try
        {
            clientManager = getSvnClientManager();
            SVNWCClient svnClient = clientManager.getWCClient();

            log.info("Fetching externals data from '" + rootUrl + "'. This may take some time.");

            // recursively gather svn:external properties
            svnClient.doGetProperty(rootUrl, SVNProperty.EXTERNALS, SVNRevision.HEAD, SVNRevision.HEAD, externalResolutionDepth,
                    new ISVNPropertyHandler() {
                        public void handleProperty(File file, SVNPropertyData svnPropertyData) throws SVNException
                        {
                        }

                        public void handleProperty(SVNURL svnUrl, SVNPropertyData svnPropertyData) throws SVNException
                        {
                            List<SVNExternal> externals = new ArrayList<SVNExternal>(Arrays.asList(SVNExternal.parseExternals(svnUrl.getPath(), svnPropertyData.getValue().getString())));
                            for (SVNExternal external : externals)
                            {
                                external.resolveURL(rootUrl, svnUrl);

                                String wcPath = StringUtils.substring(svnUrl.getPath(), rootUrl.getPath().length() + 1);
                                String cumulatedExternalPath = relativePath;
                                if (!StringUtils.isBlank(cumulatedExternalPath))
                                {
                                    cumulatedExternalPath = cumulatedExternalPath + "/";
                                }
                                if (!StringUtils.isBlank(wcPath))
                                {
                                    cumulatedExternalPath = cumulatedExternalPath + wcPath + "/";
                                }
                                cumulatedExternalPath = cumulatedExternalPath + external.getPath();

                                svnExternals.put(cumulatedExternalPath, external);

                                if (externalResolutionDepth.equals(SVNDepth.INFINITY))
                                {
                                    discoverExternals(external.getResolvedURL(), cumulatedExternalPath);
                                }
                            }
                        }

                        public void handleProperty(long l, SVNPropertyData svnPropertyData) throws SVNException
                        {
                        }
                    });
        }
        finally
        {
            dispose(clientManager);
        }
    }


    /**
     *
     * @param planKey
     * @param buildLogger
     * @param sourceCode
     * @param svnException
     * @throws RepositoryException
     *
    private void cleanBuildRepository(final String planKey, final BuildLogger buildLogger, File sourceCode, SVNException svnException) throws RepositoryException
    {
        log.info(buildLogger.addBuildLogEntry("Cleaning up subversion repository lock at '" + sourceCode.getPath() + "'"));
        SVNClientManager clientManager = null;
        try
        {
            clientManager = getSvnClientManager();
            SVNWCClient wcClient = clientManager.getWCClient();
            wcClient.setIgnoreExternals(false);
            wcClient.doCleanup(sourceCode);
            log.info(buildLogger.addBuildLogEntry("Clean up of '" + sourceCode.getPath() + "' completed"));

            if (svnException instanceof ExternalsLockException)
            {
                ExternalsLockException externalsLockException = (ExternalsLockException)svnException;
                final File affectedLockedDirectory = externalsLockException.getAffectedLockedDirectory();
                wcClient.doCleanup(affectedLockedDirectory);
                log.info(buildLogger.addBuildLogEntry("Clean up of externals at '" + affectedLockedDirectory.getPath() + "' completed"));
            }
        }
        catch (SVNException e)
        {
            final String msg = "Failed to clean up '" + planKey + "'";
            log.error(msg, e);
            throw new RepositoryException(msg, e);
        }
        finally
        {
            dispose(clientManager);
        }
    }

    /**
     * SVNClientManager factory.
     *
     * @return SVNClientManager appropriate for authorization mode
     *
    protected SVNClientManager getSvnClientManager()
    {
        ISVNAuthenticationManager authManager;

        if (StringUtils.isBlank(authType) || authType.equals(AuthenticationType.PASSWORD.getKey()))
        {
            authManager = getStandardAuthManager(username, getUserPassword());
        }
        else if (authType.equals(AuthenticationType.SSH.getKey()))
        {
            authManager = getSshAuthManager(username, getSubstitutedKeyFile(), getPassphrase());
        }
        else if (authType.equals(AuthenticationType.SSL_CLIENT_CERTIFICATE.getKey()))
        {
            authManager = getSslAuthManager(getSubstitutedKeyFile(), getPassphrase());
        }
        else
        {
            throw new IllegalArgumentException("Unexpected authorization type [" + authType + "]");
        }

        return svnClientManagerFactory.getSVNClientManager(DEFAULT_SVN_OPTIONS, authManager);
    }


    @Override
    @NotNull
    public ErrorCollection validate(@NotNull BuildConfiguration buildConfiguration)
    {
        ErrorCollection errorCollection = super.validate(buildConfiguration);

        String repoUrl = buildConfiguration.getString(SVN_REPO_URL);
        repoUrl = variableSubstitutionBean.substituteBambooVariables(repoUrl);
        if (StringUtils.isBlank(repoUrl))
        {
            errorCollection.addError(SVN_REPO_URL, "Please specify the build's Subversion Repository");
        }
        else
        {
            SVNClientManager clientManager = null;
            try
            {
                String authType = buildConfiguration.getString(SVN_AUTH_TYPE);
                String username = buildConfiguration.getString(SVN_USERNAME);
                SVNRepository svnRepository = null;
                if (StringUtils.isBlank(authType) || AuthenticationType.PASSWORD.getKey().equals(authType))
                {
                    String password = stringEncrypter.get().decrypt(buildConfiguration.getString(SVN_PASSWORD));
                    // BAM-1085, BAM-890, BAM-1028 - don't try to create a new {@link SVNRepositoryImpl} object for getting the latest revision number.
                    // Use the interface provided by SVNKit.
                    clientManager = svnClientManagerFactory.getSVNClientManager(DEFAULT_SVN_OPTIONS, getStandardAuthManager(username, password));
                    svnRepository = clientManager.createRepository(SVNURL.parseURIEncoded(repoUrl), true);
                }
                else if (AuthenticationType.SSH.getKey().equals(authType))
                {
                    String keyFile = variableSubstitutionBean.substituteBambooVariables(buildConfiguration.getString(SVN_KEYFILE));
                    String passphrase = stringEncrypter.get().decrypt(buildConfiguration.getString(SVN_PASSPHRASE));

                    // BAM-1085, BAM-890, BAM-1028 - don't try to create a new {@link SVNRepositoryImpl} object for getting the latest revision number.
                    // Use the interface provided by SVNKit.
                    clientManager = svnClientManagerFactory.getSVNClientManager(DEFAULT_SVN_OPTIONS, getSshAuthManager(username, keyFile, passphrase));
                    svnRepository = clientManager.createRepository(SVNURL.parseURIEncoded(repoUrl), true);

                    // Validate that the key file exists
                    File file = new File(keyFile);
                    if (!file.exists())
                    {
                        errorCollection.addError(SVN_KEYFILE, textProvider.getText("repository.keyFile.error"));
                    }
                }
                else if (AuthenticationType.SSL_CLIENT_CERTIFICATE.getKey().equals(authType))
                {
                    String keyFile = variableSubstitutionBean.substituteBambooVariables(buildConfiguration.getString(SVN_SSL_KEYFILE));
                    String passphrase = stringEncrypter.get().decrypt(buildConfiguration.getString(SVN_SSL_PASSPHRASE));

                    // BAM-1085, BAM-890, BAM-1028 - don't try to create a new {@link SVNRepositoryImpl} object for getting the latest revision number.
                    // Use the interface provided by SVNKit.
                    clientManager = svnClientManagerFactory.getSVNClientManager(DEFAULT_SVN_OPTIONS, getSslAuthManager(keyFile, passphrase));
                    svnRepository = clientManager.createRepository(SVNURL.parseURIEncoded(repoUrl), true);

                    // Validate that the key file exists
                    File file = new File(keyFile);
                    if (!file.exists())
                    {
                        errorCollection.addError(SVN_SSL_KEYFILE, textProvider.getText("repository.keyFile.error"));
                    }
                }

                svnRepository.testConnection();
            }
            catch (SVNException e)
            {
                log.info("Failed to validate the subversion url", e);
                errorCollection.addError(SVN_REPO_URL, "This is not a valid Subversion Repository: " + (e.getMessage()));
            }
            finally
            {
                dispose(clientManager);
            }
        }

        String webRepoUrl = buildConfiguration.getString(WEB_REPO_URL);
        webRepoUrl = variableSubstitutionBean.substituteBambooVariables(webRepoUrl);
        if (!StringUtils.isBlank(webRepoUrl) && !UrlUtils.verifyHierachicalURI(webRepoUrl))
        {
            errorCollection.addError(WEB_REPO_URL, "This is not a valid url");
        }

        quietPeriodHelper.validate(buildConfiguration, errorCollection);

        return errorCollection;
    }

    @NotNull
    private ISVNAuthenticationManager getStandardAuthManager(String userName, String password)
    {
        return SVNWCUtil.createDefaultAuthenticationManager(userName, password);
    }

    @NotNull
    private ISVNAuthenticationManager getSshAuthManager(String userName, String privateKeyFile, String passphrase)
    {
        return SVNWCUtil.createDefaultAuthenticationManager(null, userName, null, new File(privateKeyFile), passphrase, false);
    }

    @NotNull
    private ISVNAuthenticationManager getSslAuthManager(String sslClientCert, final String sslClientCertPassword)
    {
        final File clientCertFile = sslClientCert != null ? new File(sslClientCert) : null;

        ISVNAuthenticationManager authManager = new BasicAuthenticationManager(new SVNAuthentication[] {
                new SVNSSLAuthentication(clientCertFile, sslClientCertPassword, false)
        });
        return authManager;
    }


    public boolean isRepositoryDifferent(@NotNull Repository repository)
    {
        if (repository instanceof SvnRepository)
        {
            SvnRepository svn = (SvnRepository) repository;
            return !new EqualsBuilder()
                    .append(this.getName(), svn.getName())
                    .append(getRepositoryUrl(), svn.getRepositoryUrl())
                    .isEquals();
        }
        else
        {
            return true;
        }
    }


    /**
     * Checks out a working copy from a repository. Like 'svn checkout URL[@REV] PATH (-r..)' command; It's done by
     * invoking
     *
     * @param url         repository location where a working copy is to be checked out from;
     * @param revision    a revision at which a working copy being checked out is to be;
     * @param dstPath     a local path where the working copy will be fetched into;
     * @param isRecursive if true and url corresponds to a directory then doCheckout(..) recursively
     * @param buildLogger build specific logger
     * @return the number of the revision at which the working copy is
     * @throws RepositoryException Failed!
     *
    private long checkout(SVNURL url, SVNRevision revision, File dstPath, boolean isRecursive, BuildLogger buildLogger) throws RepositoryException
    {
        SVNClientManager clientManager = getSvnClientManager();
        try
        {
            SVNUpdateClient updateClient = clientManager.getUpdateClient();
            updateClient.setEventHandler(new UpdateEventHandler(buildLogger));
            updateClient.setIgnoreExternals(false);
            SVNDepth depth = isRecursive ? SVNDepth.INFINITY : SVNDepth.IMMEDIATES;
            // TODO: check allowUnversionedObstructions argument to SVNUpdateClient.doCheckout
            boolean allowUnversionedObstructions = true;
            return updateClient.doCheckout(url, dstPath, revision, revision, depth, allowUnversionedObstructions);
        }
        catch (SVNException e)
        {
            throw new RepositoryException("Failed to checkout source code to revision '" + revision + "' for " + url, e);
        }
        finally
        {
            dispose(clientManager);
        }
    }


    /**
     * Updates a working copy (brings changes from the repository into the working copy).
     *
     * @param wcPath            Workspace path
     * @param updateToRevision  Revision workspace shall be updated to
     * @param isRecursive       Flag for recursive update
     * @param buildLogger       Build specific log object
     * @return
     * @throws SVNException
     *
    private long update(File wcPath, SVNRevision updateToRevision, boolean isRecursive, BuildLogger buildLogger) throws SVNException
    {
        SVNClientManager clientManager = getSvnClientManager();
        try
        {
            SVNUpdateClient updateClient = clientManager.getUpdateClient();
            updateClient.setEventHandler(new UpdateEventHandler(buildLogger));
            updateClient.setIgnoreExternals(false);
            SVNDepth depth = isRecursive ? SVNDepth.INFINITY : SVNDepth.IMMEDIATES;
            // TODO: check allowUnversionedObstructions argument to SVNUpdateClient.doUpdate
            boolean allowUnversionedObstructions = true;
            boolean depthIsSticky = true;
            return updateClient.doUpdate(wcPath, updateToRevision, depth, allowUnversionedObstructions, depthIsSticky);
        }
        finally
        {
            dispose(clientManager);
        }
    }


    public void onInitialBuild(BuildContext buildContext)
    {
        if (isUseExternals() && externalPathRevisionMappings.isEmpty())
        {
            initExternalsRevisionMapping();
        }
    }

    /**
     * Collect latest revision for each external path
     *
    private void initExternalsRevisionMapping()
    {
        SVNClientManager clientManager = null;

        try
        {
            clientManager = getSvnClientManager();
            log.info("Initialising externals... ");
            // Collect latest revision for each path
            final Map<String, SVNExternal> externals = getExternals();
            for (final Map.Entry<String, SVNExternal> stringSVNExternalEntry : externals.entrySet()) {

                final SVNExternal external = stringSVNExternalEntry.getValue();

                final SVNURL svnUrl = external.getResolvedURL();
                final SVNRepository repository = clientManager.createRepository(svnUrl, true);
                long latestRevisionOnServer = repository.getLatestRevision();
                log.info("Setting externals path '" + stringSVNExternalEntry.getKey() + "' with SVN URL '" + svnUrl + "' to revision '" + latestRevisionOnServer + "'");
                externalPathRevisionMappings.put(stringSVNExternalEntry.getKey(), latestRevisionOnServer);
            }
        }
        catch (Exception e)
        {
            log.warn("Unable to initialise externals.", e);
        }
        finally
        {
            dispose(clientManager);
        }
    }

    private void dispose(SVNClientManager clientManager)
    {
        svnClientManagerFactory.dispose(clientManager);
    }

    public boolean isAdvancedOptionEnabled(@NotNull BuildConfiguration buildConfiguration)
    {
        final boolean useExternals = buildConfiguration.getBoolean(USE_EXTERNALS, false);
        final boolean quietPeriodEnabled = quietPeriodHelper.isEnabled(buildConfiguration);
        return useExternals || quietPeriodEnabled;
    }

    // -------------------------------------------------------------------------------------- Basic accessors & mutators
    /**
     * What's the name of the plugin - appears in the GUI dropdown
     *
     * @return The name
     *
    @NotNull
    public String getName()
    {
        return NAME;
    }

    public String getPassphrase()
    {
        try
        {
            StringEncrypter stringEncrypter = new StringEncrypter();
            return stringEncrypter.decrypt(passphrase);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public void setPassphrase(String passphrase)
    {
        try
        {
            if (StringUtils.isNotEmpty(passphrase))
            {
                StringEncrypter stringEncrypter = new StringEncrypter();
                this.passphrase = stringEncrypter.encrypt(passphrase);
            }
            else
            {
                this.passphrase = passphrase;
            }
        }
        catch (EncryptionException e)
        {
            log.error("Failed to encrypt password", e);
            this.passphrase = null;
        }
    }



    public String getAuthType()
    {
        return authType;
    }

    public void setAuthType(String authType)
    {
        this.authType = authType;
    }

    /**
     * Where is the documentation and help about using Subversion?
     *
     * @return The web url
    public String getUrl()
    {
        return "http://subversion.tigris.org/";
    }

    /**
     * Specify the subversion repository we are using
     *
     * @param repositoryUrl The subversion repository
    public void setRepositoryUrl(String repositoryUrl)
    {
        this.repositoryUrl = StringUtils.trim(repositoryUrl);
    }

    /**
     * Which repository URL are we using?
     *
     * @return The subversion repository
    public String getRepositoryUrl()
    {
        return repositoryUrl;
    }

    /**
     * Return repository URL with extrapolated Bamboo variables
     *
     * @return Repository URL with extrapolated Bamboo variables
    public String getSubstitutedRepositoryUrl()
    {
        return variableSubstitutionBean.substituteBambooVariables(repositoryUrl);
    }

    /**
     * What's the username (if any) we are using to acces the repository?
     *
     * @param username The user name, null if there is no user
    public void setUsername(String username)
    {
        this.username = StringUtils.trim(username);
    }

    /**
     * What username are we using to access the repository?
     *
     * @return The username, null if we are not using user authentication
     *
    public String getUsername()
    {
        return username;
    }

    /**
     * Specify the password required to access the resposotory
     *
     * @param password The password (null if we are not using user authentication)
     *
    public void setUserPassword(String password)
    {
        try
        {
            if (StringUtils.isNotEmpty(password))
            {
                StringEncrypter stringEncrypter = new StringEncrypter();
                this.password = stringEncrypter.encrypt(password);
            }
            else
            {
                this.password = password;
            }
        }
        catch (EncryptionException e)
        {
            log.error("Failed to encrypt password", e);
            this.password = null;
        }
    }

    /**
     * What password are we using to access the repository
     *
     * @return The password (null if we are not using user authentication)
    public String getUserPassword()
    {
        try
        {
            StringEncrypter stringEncrypter = new StringEncrypter();
            return stringEncrypter.decrypt(password);
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public String getEncryptedPassword()
    {
        return password;
    }

    public void setEncryptedPassword(String encryptedPassword)
    {
        password = encryptedPassword;
    }


    private SVNURL getSubstitutedSvnUrl() throws SVNException
    {
        return SVNURL.parseURIEncoded(getSubstitutedRepositoryUrl());
    }


    public boolean hasWebBasedRepositoryAccess()
    {
        return StringUtils.isNotBlank(webRepositoryUrl);
    }

    /**
     * Return web repository URL
     *
     * @return Web repository URL
     *
    public String getWebRepositoryUrl()
    {
        return webRepositoryUrl;
    }

    /**
     * Return web repository URL with extrapolated Bamboo variables
     *
     * @return Web repository URL with extrapolated Bamboo variables
     *
    public String getSubstitutedWebRepositoryUrl()
    {
        return variableSubstitutionBean.substituteBambooVariables(webRepositoryUrl);
    }

    public void setWebRepositoryUrl(String url)
    {
        webRepositoryUrl = StringUtils.trim(url);
    }

    public String getWebRepositoryUrlRepoName()
    {
        return webRepositoryUrlRepoName;
    }

    public void setWebRepositoryUrlRepoName(String repoName)
    {
        webRepositoryUrlRepoName = StringUtils.trim(repoName);
    }

    public String getWebRepositoryUrlForFile(CommitFile file)
    {
        ViewCvsFileLinkGenerator fileLinkGenerator = new ViewCvsFileLinkGenerator(getSubstitutedWebRepositoryUrl());
        return fileLinkGenerator.getWebRepositoryUrlForFile(file, webRepositoryUrlRepoName, ViewCvsFileLinkGenerator.SVN_REPO_TYPE);
    }

    public String getWebRepositoryUrlForDiff(CommitFile file)
    {
        ViewCvsFileLinkGenerator fileLinkGenerator = new ViewCvsFileLinkGenerator(getSubstitutedWebRepositoryUrl());
        return fileLinkGenerator.getWebRepositoryUrlForDiff(file, webRepositoryUrlRepoName, ViewCvsFileLinkGenerator.SVN_REPO_TYPE);
    }

    public String getWebRepositoryUrlForRevision(CommitFile file)
    {
        ViewCvsFileLinkGenerator fileLinkGenerator = new ViewCvsFileLinkGenerator(getSubstitutedWebRepositoryUrl());
        return fileLinkGenerator.getWebRepositoryUrlForRevision(file, webRepositoryUrlRepoName, ViewCvsFileLinkGenerator.SVN_REPO_TYPE);
    }

    @Nullable
    @Override
    public String getWebRepositoryUrlForCommit(@NotNull Commit commit)
    {
        ViewCvsFileLinkGenerator fileLinkGenerator = new ViewCvsFileLinkGenerator(getSubstitutedWebRepositoryUrl());
        return fileLinkGenerator.getWebRepositoryUrlForCommit(commit, webRepositoryUrlRepoName, ViewCvsFileLinkGenerator.SVN_REPO_TYPE);
    }

    public String getHost()
    {
        if (repositoryUrl == null)
        {
            return UNKNOWN_HOST;
        }

        try
        {
            SVNURL svnurl = getSubstitutedSvnUrl();
            return svnurl.getHost();
        }
        catch (SVNException e)
        {
            return UNKNOWN_HOST;
        }
    }

    public boolean isQuietPeriodEnabled()
    {
        return quietPeriodEnabled;
    }

    public void setQuietPeriodEnabled(boolean quietPeriodEnabled)
    {
        this.quietPeriodEnabled = quietPeriodEnabled;
    }

    public int getQuietPeriod()
    {
        return quietPeriod;
    }

    public void setQuietPeriod(int quietPeriod)
    {
        this.quietPeriod = quietPeriod;
    }

    public int getMaxRetries()
    {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries)
    {
        this.maxRetries = maxRetries;
    }

    public boolean isUseExternals()
    {
        return useExternals;
    }

    public void setUseExternals(boolean useExternals)
    {
        this.useExternals = useExternals;
    }

    public void setSvnClientManagerFactory(SVNClientManagerFactory svnClientManagerFactory)
    {
        this.svnClientManagerFactory = svnClientManagerFactory;
    }

    public Map<String, Long> getExternalPathRevisionMappings()
    {
        return externalPathRevisionMappings;
    }

    public SortedMap<String, Long> getExternalPathRevisionMappingsSorted()
    {
        return new TreeMap<String, Long>(externalPathRevisionMappings);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(101, 11)
                .append(getKey())
                .append(getRepositoryUrl())
                .append(getUsername())
                .append(getEncryptedPassword())
                .append(getWebRepositoryUrl())
                .append(getWebRepositoryUrlRepoName())
                .append(getTriggerIpAddress())
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SvnRepository))
        {
            return false;
        }
        SvnRepository rhs = (SvnRepository) o;
        return new EqualsBuilder()
                .append(getRepositoryUrl(), rhs.getRepositoryUrl())
                .append(getUsername(), rhs.getUsername())
                .append(getEncryptedPassword(), rhs.getEncryptedPassword())
                .append(getWebRepositoryUrl(), rhs.getWebRepositoryUrl())
                .append(getWebRepositoryUrlRepoName(), rhs.getWebRepositoryUrlRepoName())
                .append(getTriggerIpAddress(), rhs.getTriggerIpAddress())
                .isEquals();
    }

    public int compareTo(Object obj) {
        SvnRepository o = (SvnRepository) obj;
        return new CompareToBuilder()
                .append(getRepositoryUrl(), o.getRepositoryUrl())
                .append(getUsername(), o.getUsername())
                .append(getEncryptedPassword(), o.getEncryptedPassword())
                .append(getWebRepositoryUrl(), o.getWebRepositoryUrl())
                .append(getWebRepositoryUrlRepoName(), o.getWebRepositoryUrlRepoName())
                .append(getTriggerIpAddress(), o.getTriggerIpAddress())
                .toComparison();
    }

    public List<NameValuePair> getAuthenticationTypes() {
        List<NameValuePair> types = new ArrayList<NameValuePair>();
        types.add(AuthenticationType.PASSWORD.getNameValue());
        types.add(AuthenticationType.SSH.getNameValue());
        types.add(AuthenticationType.SSL_CLIENT_CERTIFICATE.getNameValue());
        return types;
    }
}
*/