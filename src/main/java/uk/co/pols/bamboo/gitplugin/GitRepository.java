package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.repository.*;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.repository.RepositoryEventAware;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Provides GIT and GITHUB support for the Bamboo Build Server
 */
public class GitRepository extends AbstractRepository implements SelectableAuthenticationRepository, WebRepositoryEnabledRepository, InitialBuildAwareRepository, MutableQuietPeriodAwareRepository, RepositoryEventAware {
    public static final String NAME = "Git";
    public static final String KEY = "git";

    private static final String REPO_PREFIX = "repository.git.";
    public static final String GIT_REPO_URL = REPO_PREFIX + "repositoryUrl";
    public static final String GIT_BRANCH = REPO_PREFIX + "branch";

    private String repositoryUrl;
    private String branch;

    public String getName() {
        return NAME;
    }

    public String getUrl() {
        return "http://github.com/guides/home";
    }

    /* -------- Make the properties available to the UI -------- */

    public String getHost() {
        return UNKNOWN_HOST;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = StringUtils.trim(repositoryUrl);
    }

    public List<NameValuePair> getAuthenticationTypes() {
        throw new UnsupportedOperationException("TO DO");
    }

    public ErrorCollection validate(BuildConfiguration buildConfiguration) {
        ErrorCollection errorCollection = super.validate(buildConfiguration);

        validateMandatoryField(buildConfiguration, errorCollection, GIT_REPO_URL, "Please specify where the repository is located");
        validateMandatoryField(buildConfiguration, errorCollection, GIT_BRANCH, "Please specify which branch you want to build");

        return errorCollection;
    }

    public void prepareConfigObject(BuildConfiguration buildConfiguration) {
    }

    public HierarchicalConfiguration toConfiguration() {
        HierarchicalConfiguration configuration = super.toConfiguration();
        configuration.setProperty(GIT_REPO_URL, getRepositoryUrl());
        configuration.setProperty(GIT_BRANCH, getBranch());
        return configuration;
    }


    public void populateFromConfig(HierarchicalConfiguration config) {
        super.populateFromConfig(config);

        setRepositoryUrl(config.getString(GIT_REPO_URL));
        setBranch(config.getString(GIT_BRANCH));
    }

    public boolean hasWebBasedRepositoryAccess() {
        return false;
    }

    public void setWebRepositoryUrl(String string) {
        throw new UnsupportedOperationException("TO DO");
    }

    public void setWebRepositoryUrlRepoName(String string) {
        throw new UnsupportedOperationException("TO DO");
    }

    public String getWebRepositoryUrl() {
        throw new UnsupportedOperationException("TO DO");
    }

    public String getWebRepositoryUrlRepoName() {
        throw new UnsupportedOperationException("TO DO");
    }

    public String getWebRepositoryUrlForFile(CommitFile commitFile) {
        throw new UnsupportedOperationException("TO DO");
    }

    public void onInitialBuild(BuildContext buildContext) {
        throw new UnsupportedOperationException("TO DO");
    }

    public void setQuietPeriodEnabled(boolean b) {
        throw new UnsupportedOperationException("TO DO");
    }

    public void setQuietPeriod(int i) {
        throw new UnsupportedOperationException("TO DO");
    }

    public void setMaxRetries(int i) {
        throw new UnsupportedOperationException("TO DO");
    }

    public boolean isQuietPeriodEnabled() {
        return false;
    }

    public int getQuietPeriod() {
        return 0;
    }

    public boolean isRepositoryDifferent(Repository repository) {
        return false;
    }

    public int getMaxRetries() {
        return 0;
    }

    public void preRetrieveSourceCode(BuildContext buildContext) {
        throw new UnsupportedOperationException("TO DO");
    }

    public void postRetrieveSourceCode(BuildContext buildContext) {
        throw new UnsupportedOperationException("TO DO");
    }

    public BuildChanges collectChangesSinceLastBuild(String string, String string1) throws RepositoryException {
        throw new UnsupportedOperationException("TO DO");
    }

    public String retrieveSourceCode(String string, String string1) throws RepositoryException {
        throw new UnsupportedOperationException("TO DO");
    }

    private void validateMandatoryField(BuildConfiguration buildConfiguration, ErrorCollection errorCollection, String fieldKey, String errorMessage) {
        if (StringUtils.isEmpty(buildConfiguration.getString(fieldKey))) {
            errorCollection.addError(fieldKey, errorMessage);
        }
    }

    
    /*
    cd working directory
    git init
    git remote add origin git@github.com:andypols/polsbusiness.git
    git pull origin master
    */

    /**
     * Need
     * - working directory
     * - path to git client binary
     * - github url
     * - branch to pull from repository
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String gitHome = "/opt/local/bin";
        String gitExe = gitHome + "/git";

        File workingDirectory = new File("/Users/andy/projects/git/temp/newrepo");

        Execute execute = new Execute(new PumpStreamHandler(System.out));
        execute.setWorkingDirectory(workingDirectory);

        if (!workingDirectory.exists()) {
            workingDirectory.mkdirs();
            execute.setCommandline(new String[]{gitExe, "init"});
            execute.execute();

            execute.setCommandline(new String[]{gitExe, "remote", "add", "origin", "git@github.com:andypols/git-bamboo-plugin.git"});
            execute.execute();
        }

        execute.setCommandline(new String[]{gitExe, "pull", "origin", "master"});
        execute.execute();
    }
}