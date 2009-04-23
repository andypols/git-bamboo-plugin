package uk.co.pols.bamboo.gitplugin.authentication;

import uk.co.pols.bamboo.gitplugin.GitRepositoryConfig;
import com.atlassian.bamboo.repository.RepositoryException;

public interface AuthenticationRunner {
    void run(GitRepositoryConfig config) throws RepositoryException;

    void task() throws RepositoryException;    
}
