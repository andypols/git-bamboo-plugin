package uk.co.pols.bamboo.gitplugin;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;

import java.io.Serializable;

public class GitRepositoryConfig implements Serializable {
    public static final String REPO_PREFIX = "repository.github.";

    public static final String GIT_REPO_URL = REPO_PREFIX + "repositoryUrl";
    public static final String GIT_BRANCH = REPO_PREFIX + "branch";
    private static final String DEFAULT_BRANCH = "master";

    private String repositoryUrl;
    private String branch;

    public void populateFromConfig(HierarchicalConfiguration config) {
        repositoryUrl = config.getString(GIT_REPO_URL);
        branch = config.getString(GIT_BRANCH);
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = StringUtils.trim(repositoryUrl);
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getHost() {
        if (repositoryUrl == null) {
            return Repository.UNKNOWN_HOST;
        }

        return "github.com";
    }

    public HierarchicalConfiguration toConfiguration(HierarchicalConfiguration configuration) {
        configuration.setProperty(GIT_REPO_URL, getRepositoryUrl());
        configuration.setProperty(GIT_BRANCH, getBranch());
        return configuration;
    }

    public ErrorCollection validate(ErrorCollection errorCollection, BuildConfiguration buildConfiguration) {
        validateMandatoryField(buildConfiguration, errorCollection, GitRepositoryConfig.GIT_REPO_URL, "Please specify where the repository is located");
        validateMandatoryField(buildConfiguration, errorCollection, GitRepositoryConfig.GIT_BRANCH, "Please specify which branch you want to build");

        return errorCollection;
    }

    private void validateMandatoryField(BuildConfiguration buildConfiguration, ErrorCollection errorCollection, String fieldKey, String errorMessage) {
        if (StringUtils.isEmpty(buildConfiguration.getString(fieldKey))) {
            errorCollection.addError(fieldKey, errorMessage);
        }
    }

    public void addDefaultValues(BuildConfiguration buildConfiguration) {
        buildConfiguration.setProperty(GIT_BRANCH, DEFAULT_BRANCH);
    }
}