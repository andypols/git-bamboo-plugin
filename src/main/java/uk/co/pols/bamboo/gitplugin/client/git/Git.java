package uk.co.pols.bamboo.gitplugin.client.git;

import uk.co.pols.bamboo.gitplugin.client.git.commands.*;
import uk.co.pols.bamboo.gitplugin.client.utils.GitRepositoryDetector;

import java.io.File;

public interface Git {
    /** @deprecated */
    GitInitCommand init(File sourceCodeDirectory);

    boolean isValidRepo(File sourceDirectory);

    /** @deprecated */
    GitRemoteCommand remote(File sourceCodeDirectory);

    GitCloneCommand repositoryClone();

    GitCheckoutCommand checkout();

    GitLogCommand log(File sourceCodeDirectory, String lastRevisionChecked);

    GitPullCommand pull(File sourceCodeDirectory);
}