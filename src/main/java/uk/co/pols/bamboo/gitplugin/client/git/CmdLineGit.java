package uk.co.pols.bamboo.gitplugin.client.git;

import uk.co.pols.bamboo.gitplugin.client.git.commands.*;
import uk.co.pols.bamboo.gitplugin.client.utils.FileBasedGitRepositoryDetector;

import java.io.File;

public class CmdLineGit implements Git {
    private GitCommandDiscoverer gitCommandDiscoverer = gitCommandDiscoverer();

    public boolean isValidRepo(File sourceDirectory) {
        return new FileBasedGitRepositoryDetector().containsValidRepo(sourceDirectory);
    }

    public GitPullCommand pull(File sourceCodeDirectory) {
        return new ExecutorGitPullCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, new AntCommandExecutor());
    }

    public GitLogCommand log(File sourceCodeDirectory, String lastRevisionChecked) {
        return new ExecutorGitLogCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, lastRevisionChecked, new AntCommandExecutor());
    }

    public GitCloneCommand repositoryClone() {
        return new ExecutorGitCloneCommand(gitCommandDiscoverer.gitCommand(), new AntCommandExecutor());
    }

    public GitCheckoutCommand checkout() {
        return new ExecutorGitCheckoutCommand(gitCommandDiscoverer.gitCommand(), new AntCommandExecutor());
    }

    private GitCommandDiscoverer gitCommandDiscoverer() {
        return new BestGuessGitCommandDiscoverer(new AntCommandExecutor());
    }
}
