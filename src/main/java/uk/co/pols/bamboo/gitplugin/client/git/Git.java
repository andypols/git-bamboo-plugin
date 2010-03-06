package uk.co.pols.bamboo.gitplugin.client.git;

import uk.co.pols.bamboo.gitplugin.client.git.commands.*;
import uk.co.pols.bamboo.gitplugin.client.utils.GitRepositoryDetector;

import java.io.File;

public interface Git {

    boolean isValidRepo(File sourceDirectory);

    GitCloneCommand repositoryClone();

    GitCheckoutCommand checkout();

    GitLogCommand log(File sourceCodeDirectory, String lastRevisionChecked);

    GitPullCommand pull(File sourceCodeDirectory);
}