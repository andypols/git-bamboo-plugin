package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.webrepository.CommitUrlProvider;
import com.atlassian.bamboo.webrepository.DefaultWebRepositoryViewer;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

import static uk.co.pols.bamboo.gitplugin.GitRepositoryConfig.AvailableConfig.WEB_REPOSITORY;

public class GitHubWebRepositoryViewer extends DefaultWebRepositoryViewer implements CommitUrlProvider {
    private static Logger log = Logger.getLogger(GitHubWebRepositoryViewer.class);
    private GitRepositoryConfig gitRepositoryConfig = gitRepositoryConfig();

    @Override
    public Collection<String> getSupportedRepositories() {
        log.info("********************* GitHubWebRepositoryViewer.getSupportedRepositories");
        return Arrays.asList("uk.co.pols.bamboo.gitplugin:github");
    }

    @Override
    public void populateFromConfig(HierarchicalConfiguration config) {
        log.info("********************* GitHubWebRepositoryViewer.populateFromConfig");
        super.populateFromConfig(config);
        gitRepositoryConfig.populateFromConfig(config, WEB_REPOSITORY);
    }

    @Override
    public HierarchicalConfiguration toConfiguration() {
        log.info("********************  GitHubWebRepositoryViewer.toConfiguration");
        return gitRepositoryConfig.toConfiguration(super.toConfiguration(), WEB_REPOSITORY);
    }

    @Override
    public ErrorCollection validate(BuildConfiguration buildConfiguration) {
        log.info("********************  GitHubWebRepositoryViewer.validate");
        return gitRepositoryConfig.validate(super.validate(buildConfiguration), buildConfiguration, WEB_REPOSITORY);
    }

    public String getHtmlForCommitsFull(ResultsSummary resultsSummary, Repository repository) {
        log.info("****************  GitHubWebRepositoryViewer.getHtmlForCommitsFull");
        String html = super.getHtmlForCommitsFull(resultsSummary,repository);
        log.info("************* resultsSummary = " + html);

        return html;
    }

    public String getHtmlForCommitsSummary(ResultsSummary resultsSummary, Repository repository) {
        log.info("****************** GitHubWebRepositoryViewer.getHtmlForCommitsSummary");
        String html = super.getHtmlForCommitsSummary(resultsSummary, repository);
        log.info("resultsSummary = " + html);
        return html;
    }

    public void setWebRepositoryUrl(String url) {
        log.info("*****************  GitHubWebRepositoryViewer.setWebRepositoryUrl");
        gitRepositoryConfig.setWebRepositoryUrl(url);
    }

    public String getWebRepositoryUrl() {
        log.info("****************  GitHubWebRepositoryViewer.getWebRepositoryUrl");
        return gitRepositoryConfig.getWebRepositoryUrl();
    }

    public String getWebRepositoryUrlForFile(CommitFile commitFile) {
        log.info("**************** GitHubWebRepositoryViewer.getWebRepositoryUrlForFile");

        return gitRepositoryConfig.getWebRepositoryUrlForFile(commitFile);
    }

    /*
        Rendering links in emails too
    */
    public String getWebRepositoryUrlForCommit(Commit commit, Repository repository) {
        log.info("************** GitHubWebRepositoryViewer.getWebRepositoryUrlForCommit");
        return gitRepositoryConfig.getWebRepositoryUrlForCommit(commit);
    }

    protected GitRepositoryConfig gitRepositoryConfig() {
        return new GitRepositoryConfig();
    }
}