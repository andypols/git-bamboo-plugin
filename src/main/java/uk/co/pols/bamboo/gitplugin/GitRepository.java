package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.repository.AbstractRepository;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.repository.RepositoryException;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildChangesImpl;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import uk.co.pols.bamboo.gitplugin.client.CmdLineGitClient;
import uk.co.pols.bamboo.gitplugin.client.GitClient;
import uk.co.pols.bamboo.gitplugin.client.git.CmdLineGit;

import java.util.ArrayList;
import java.util.List;

import static com.atlassian.bamboo.plan.PlanKeys.getPlanKey;
import static uk.co.pols.bamboo.gitplugin.GitRepositoryConfig.AvailableConfig.REPOSITORY;

public class GitRepository extends AbstractRepository {
    private GitRepositoryConfig gitRepositoryConfig = gitRepositoryConfig();

    public String getName() {
        return "Git";
    }

    public boolean isRepositoryDifferent(Repository repository) {
        if (repository instanceof GitRepository) {
            GitRepository gitRepository = (GitRepository) repository;
            return !new EqualsBuilder()
                    .append(this.getName(), gitRepository.getName())
                    .append(this.getBranch(), gitRepository.getBranch())
                    .append(getRepositoryUrl(), gitRepository.getRepositoryUrl())
                    .isEquals();
        }
        return true;
    }

    /*
     * This is called by bamboo when a build has been triggered to calculate the changes since the previous build.
     * It is executed on the server.  It does not get run on the initial build, so may have to handle an empty git repo
     * when the second build is triggered
     */
    public synchronized BuildChanges collectChangesSinceLastBuild(final String planKey, final String lastVcsRevisionKey) throws RepositoryException {
        List<Commit> commits = new ArrayList<Commit>();

        String latestCommitTime = gitClient().getLatestUpdate(
                buildLoggerManager.getBuildLogger(getPlanKey(planKey)),
                gitRepositoryConfig.getRepositoryUrl(),
                gitRepositoryConfig.getBranch(),
                planKey,
                lastVcsRevisionKey,
                commits,
                getSourceCodeDirectory(planKey)
        );

        return new BuildChangesImpl(String.valueOf(latestCommitTime), commits);
    }


    /**
     * This is called by the agent to get the latest code.
     */
    public String retrieveSourceCode(BuildContext buildContext, String targetRevision) throws RepositoryException {
        final String planKey = buildContext.getPlanKey();

        return gitClient().getLatestUpdate(
                buildLoggerManager.getBuildLogger(getPlanKey(planKey)),
                gitRepositoryConfig.getRepositoryUrl(),
                gitRepositoryConfig.getBranch(),
                planKey,
                targetRevision,
                new ArrayList<Commit>(),
                getSourceCodeDirectory(planKey));
    }

    /**
     * @deprecated not used in the latest version of Bamboo
     */
    public String retrieveSourceCode(final String planKey, final String vcsRevisionKey) throws RepositoryException {
        throw new UnsupportedOperationException("Deprecated call");
    }

    public void prepareConfigObject(BuildConfiguration buildConfiguration) {
    }

    @Override
    public void populateFromConfig(HierarchicalConfiguration config) {
        super.populateFromConfig(config);
        gitRepositoryConfig.populateFromConfig(config, REPOSITORY);
    }

    @Override
    public HierarchicalConfiguration toConfiguration() {
        return gitRepositoryConfig.toConfiguration(super.toConfiguration(), REPOSITORY);
    }

    @Override
    public ErrorCollection validate(BuildConfiguration buildConfiguration) {
        return gitRepositoryConfig.validate(super.validate(buildConfiguration), buildConfiguration, REPOSITORY);
    }

    public void addDefaultValues(BuildConfiguration buildConfiguration) {
        super.addDefaultValues(buildConfiguration);
        gitRepositoryConfig.addDefaultValues(buildConfiguration, REPOSITORY);
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

    protected GitClient gitClient() {
        return new CmdLineGitClient(new CmdLineGit());
    }

    protected GitRepositoryConfig gitRepositoryConfig() {
        return new GitRepositoryConfig();
    }
}