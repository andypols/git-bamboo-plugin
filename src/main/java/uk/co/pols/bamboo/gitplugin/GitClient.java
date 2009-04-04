package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.repository.RepositoryException;
import com.atlassian.bamboo.commit.Commit;

import java.io.File;
import java.util.List;

public interface GitClient {
    void initialiseRemoteRepository(File sourceDirectory, String repositoryUrl, BuildLogger buildLogger) throws RepositoryException;

    String getLatestUpdate(BuildLogger buildLogger, String repositoryUrl, String planKey, String lastRevisionChecked, List<Commit> commits, File sourceCodeDirectory) throws RepositoryException;
}
