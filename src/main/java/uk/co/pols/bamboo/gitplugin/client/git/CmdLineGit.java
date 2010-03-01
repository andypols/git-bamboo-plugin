package uk.co.pols.bamboo.gitplugin.client.git;

import uk.co.pols.bamboo.gitplugin.client.git.Git;
import uk.co.pols.bamboo.gitplugin.client.git.commands.*;

import java.io.File;

public class CmdLineGit implements Git {
    private GitCommandDiscoverer gitCommandDiscoverer = gitCommandDiscoverer();

    public GitPullCommand pull(File sourceCodeDirectory) {
        return new ExecutorGitPullCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, new AntCommandExecutor());
    }

    public GitLogCommand log(File sourceCodeDirectory, String lastRevisionChecked) {
        return new ExecutorGitLogCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, lastRevisionChecked, new AntCommandExecutor());
    }

    /** @deprecated **/
    public GitInitCommand init(File sourceCodeDirectory) {
        return new ExecutorGitInitCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, new AntCommandExecutor());
    }

    public GitCloneCommand repositoryClone() {
        return new ExecutorGitCloneCommand(gitCommandDiscoverer.gitCommand(), new AntCommandExecutor());
    }

    public GitCheckoutCommand checkout() {
        return new ExecutorGitCheckoutCommand(gitCommandDiscoverer.gitCommand(), new AntCommandExecutor());
    }

    /** @deprecated **/    
    public GitRemoteCommand remote(File sourceCodeDirectory) {
        return new ExecutorGitRemoteCommand(gitCommandDiscoverer.gitCommand(), sourceCodeDirectory, new AntCommandExecutor());
    }

    private GitCommandDiscoverer gitCommandDiscoverer() {
        return new BestGuessGitCommandDiscoverer(new AntCommandExecutor());
    }
}
