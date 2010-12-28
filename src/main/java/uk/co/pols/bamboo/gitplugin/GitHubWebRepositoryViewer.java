package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.webrepository.AbstractWebRepositoryViewer;
import com.atlassian.bamboo.webrepository.CommitUrlProvider;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.Arrays;
import java.util.Collection;

import static uk.co.pols.bamboo.gitplugin.GitRepositoryConfig.AvailableConfig.WEB_REPOSITORY;

public class GitHubWebRepositoryViewer extends AbstractWebRepositoryViewer implements CommitUrlProvider {
    private GitRepositoryConfig gitRepositoryConfig = gitRepositoryConfig();

    @Override
    public Collection<String> getSupportedRepositories() {
        return Arrays.asList("uk.co.pols.bamboo.gitplugin:github");
    }

    @Override
    public void populateFromParams(ActionParametersMap params) {
        super.populateFromParams(params);
    }

    @Override
    public void populateFromConfig(HierarchicalConfiguration config) {
        super.populateFromConfig(config);
        gitRepositoryConfig.populateFromConfig(config, WEB_REPOSITORY);
    }

    @Override
    public HierarchicalConfiguration toConfiguration() {
        return gitRepositoryConfig.toConfiguration(super.toConfiguration(), WEB_REPOSITORY);
    }

    @Override
    public ErrorCollection validate(BuildConfiguration buildConfiguration) {
        return gitRepositoryConfig.validate(super.validate(buildConfiguration), buildConfiguration, WEB_REPOSITORY);
    }

    public String getHtmlForCommitsFull(ResultsSummary resultsSummary, Repository repository) {
        return "<p>getHtmlForCommitsFull</p>";
    }

    public String getHtmlForCommitsSummary(ResultsSummary resultsSummary, Repository repository) {
        return "<p>getHtmlForCommitsSummary</p>";
    }

    public void setWebRepositoryUrl(String url) {
        gitRepositoryConfig.setWebRepositoryUrl(url);
    }

    public String getWebRepositoryUrl() {
        return gitRepositoryConfig.getWebRepositoryUrl();
    }

    public String getWebRepositoryUrlForFile(CommitFile commitFile) {
        return gitRepositoryConfig.getWebRepositoryUrlForFile(commitFile);
    }

    /*
        Rendering links in emails too
    */
    public String getWebRepositoryUrlForCommit(Commit commit, Repository repository) {
        return gitRepositoryConfig.getWebRepositoryUrlForCommit(commit);
    }

//    public String getWebRepositoryUrlForDiff(CommitFile file) {
//        return gitRepositoryConfig.getWebRepositoryUrlForDiff(file);
//    }
//
//    public String getWebRepositoryUrlForRevision(CommitFile file) {
//        return gitRepositoryConfig.getWebRepositoryUrlForRevision(file);
//    }

    protected GitRepositoryConfig gitRepositoryConfig() {
        return new GitRepositoryConfig();
    }

//    public boolean hasWebBasedRepositoryAccess() {
//        return gitRepositoryConfig.hasWebBasedRepositoryAccess();
//    }

}