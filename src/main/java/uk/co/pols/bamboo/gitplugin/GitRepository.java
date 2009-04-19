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

/**
 * Provides GIT and GITHUB support for the Bamboo Build Server
 */
public class GitRepository extends AbstractRepository implements WebRepositoryEnabledRepository, InitialBuildAwareRepository {
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
        return gitRepositoryConfig.hasWebBasedRepositoryAccess(); // test me
    }

    public void setWebRepositoryUrl(String url) {
        gitRepositoryConfig.setWebRepositoryUrl(url);   // test me
    }

    public void setWebRepositoryUrlRepoName(String url) {
    }

    public String getWebRepositoryUrl() {
        return gitRepositoryConfig.getWebRepositoryUrl(); // test me
    }

    public String getWebRepositoryUrlRepoName() {
        return null;
    }

    public String getWebRepositoryUrlForFile(CommitFile commitFile) {
        return gitRepositoryConfig.getWebRepositoryUrlForFile(commitFile);  // test me
    }

    @Override
    public String getWebRepositoryUrlForCommit(Commit commit) {
        return gitRepositoryConfig.getWebRepositoryUrlForCommit(commit);   // test me
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