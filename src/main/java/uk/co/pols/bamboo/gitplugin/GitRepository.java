package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.repository.*;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildChangesImpl;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

import uk.co.pols.bamboo.gitplugin.commands.GitCommandDiscoverer;

/**
 * Provides GIT and GITHUB support for the Bamboo Build Server
 * <p/>
 * TODO Add hook for github callback triggering the build
 * TODO work out if the repository url has changed...
 * TODO can link to the commits using https://github.com/andypols/git-bamboo-plugin/commit/5d87fb199040eb77db5056dd7a2bab435d5f05b0
 * <p/>
 * This is what capistarno does....
 * git reset -q --hard 10e162370493a984c279ffc7ca59e18d7850e844;
 * git checkout -q -b deploy 10e162370493a984c279ffc7ca59e18d7850e844;
 * <p/>
 * So if I can do a remote history I'm laughing...
 */
public class GitRepository extends AbstractRepository implements /*SelectableAuthenticationRepository,*/ WebRepositoryEnabledRepository, InitialBuildAwareRepository {
    private GitRepositoryConfig gitRepositoryConfig = gitRepositoryConfig();

    public synchronized BuildChanges collectChangesSinceLastBuild(String planKey, String lastVcsRevisionKey) throws RepositoryException {
        List<Commit> commits = new ArrayList<Commit>();

        String latestCommitTime = gitClient().getLatestUpdate(
                buildLoggerManager.getBuildLogger(planKey),
                gitRepositoryConfig.getRepositoryUrl(),
                planKey,
                lastVcsRevisionKey,
                commits,
                getSourceCodeDirectory(planKey)
        );

        return new BuildChangesImpl(String.valueOf(latestCommitTime), commits);
    }

    public String retrieveSourceCode(String planKey, String vcsRevisionKey) throws RepositoryException {
        return gitClient().initialiseRepository(
                getSourceCodeDirectory(planKey),
                planKey,
                vcsRevisionKey,
                gitRepositoryConfig,
                isWorkspaceEmpty(getSourceCodeDirectory(planKey)),
                buildLoggerManager.getBuildLogger(planKey));
    }

    @Override
    public ErrorCollection validate(BuildConfiguration buildConfiguration) {
        return gitRepositoryConfig.validate(super.validate(buildConfiguration), buildConfiguration);
    }

    public boolean isRepositoryDifferent(Repository repository) {
        if (repository instanceof GitRepository) {
            GitRepository gitRepository = (GitRepository) repository;
            return !new EqualsBuilder()
                    .append(this.getName(), gitRepository.getName())
                    .append(getRepositoryUrl(), gitRepository.getRepositoryUrl())
                    .isEquals();
        }
        return true;
    }

    public void addDefaultValues(BuildConfiguration buildConfiguration) {
        super.addDefaultValues(buildConfiguration);
        gitRepositoryConfig.addDefaultValues(buildConfiguration);
    }

    public void prepareConfigObject(BuildConfiguration buildConfiguration) {
    }

    @Override
    public void populateFromConfig(HierarchicalConfiguration config) {
        super.populateFromConfig(config);
        gitRepositoryConfig.populateFromConfig(config);
    }

    @Override
    public HierarchicalConfiguration toConfiguration() {
        return gitRepositoryConfig.toConfiguration(super.toConfiguration());
    }

    public void onInitialBuild(BuildContext buildContext) {
        // do nothing
    }

    public String getName() {
        return "GitHub";
    }

    public String getUrl() {
        return "http://github.com/guides/home";
    }

    public void setRepositoryUrl(String repositoryUrl) {
        gitRepositoryConfig.setRepositoryUrl(repositoryUrl);
    }

    public String getRepositoryUrl() {
        return gitRepositoryConfig.getRepositoryUrl();
    }

    public String getBranch() {
        return gitRepositoryConfig.getBranch();
    }

    public void setBranch(String branch) {
        gitRepositoryConfig.setBranch(branch);
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

    @Override
    public String getWebRepositoryUrlForCommit(Commit commit) {
        return "noidea";
    }

    public String getHost() {
        return gitRepositoryConfig.getHost();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(101, 11)
                .append(getKey())
                .append(getRepositoryUrl())
                .append(getTriggerIpAddress())
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GitRepository)) {
            return false;
        }
        GitRepository rhs = (GitRepository) o;
        return new EqualsBuilder()
                .append(getRepositoryUrl(), rhs.getRepositoryUrl())
                .append(getTriggerIpAddress(), rhs.getTriggerIpAddress())
                .isEquals();
    }

    public int compareTo(Object obj) {
        GitRepository o = (GitRepository) obj;
        return new CompareToBuilder()
                .append(getRepositoryUrl(), o.getRepositoryUrl())
                .append(getTriggerIpAddress(), o.getTriggerIpAddress())
                .toComparison();
    }

//    public List<NameValuePair> getAuthenticationTypes() {
//        List<NameValuePair> types = new ArrayList<NameValuePair>();
//        types.add(AuthenticationType.PASSWORD.getNameValue());
//        types.add(AuthenticationType.SSH.getNameValue());
//        return types;
//    }

    protected GitClient gitClient() {
        return new CmdLineGitClient();
    }

    protected GitRepositoryConfig gitRepositoryConfig() {
        return new GitRepositoryConfig();
    }
}