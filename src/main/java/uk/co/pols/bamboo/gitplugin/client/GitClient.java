package uk.co.pols.bamboo.gitplugin.client;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.repository.RepositoryException;
import com.atlassian.bamboo.commit.Commit;

import java.io.File;
import java.util.List;

import uk.co.pols.bamboo.gitplugin.GitRepositoryConfig;

public interface GitClient {

    String getLatestUpdate(BuildLogger buildLogger, String repositoryUrl, String branch, String planKey, String lastRevisionChecked, List<Commit> commits, File sourceCodeDirectory) throws RepositoryException;
}
