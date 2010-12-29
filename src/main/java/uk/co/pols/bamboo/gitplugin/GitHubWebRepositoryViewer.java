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
import java.util.List;
import java.util.Set;

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
        return gitRepositoryConfig.toConfiguration(super.toConfiguration(), WEB_REPOSITORY);
    }

    @Override
    public ErrorCollection validate(BuildConfiguration buildConfiguration) {
        return gitRepositoryConfig.validate(super.validate(buildConfiguration), buildConfiguration, WEB_REPOSITORY);
    }

    public String getHtmlForCommitsFull(ResultsSummary resultsSummary, Repository repository) {
        String html = super.getHtmlForCommitsFull(resultsSummary, repository);

        log.info("**********************************************************************************");
        log.info("**********************************************************************************");
        log.info("**********************************************************************************");
        log.info("**********************************************************************************");
        log.info("Original resultsSummary = " + html);

        for (Commit commit : resultsSummary.getCommits()) {
            for (CommitFile commitFile : commit.getFiles()) {
                if (commitFile.isRevisionKnown()) {
                    String revision = commitFile.getRevision();
                    String name = commitFile.getName();

                    log.info("file name: " + name);
                    html.replaceAll(name + "\n" + "(version " + revision + ")",
                                    name + "\n" + "(version <a href=\"" + getWebRepositoryUrlForFile(commitFile) + "\">" + revision + "</a>)");
                }
            }
        }

        log.info("tweaked resultsSummary = " + html);

        return html;
    }

    public String getHtmlForCommitsSummary(ResultsSummary resultsSummary, Repository repository) {
        return super.getHtmlForCommitsSummary(resultsSummary, repository);
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

    protected GitRepositoryConfig gitRepositoryConfig() {
        return new GitRepositoryConfig();
    }
}