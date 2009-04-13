package uk.co.pols.bamboo.gitplugin.commands;

import uk.co.pols.bamboo.gitplugin.commands.GitCommandDiscoverer;

public class BestGuessGitCommandDiscoverer implements GitCommandDiscoverer {
    private static final String GIT_HOME = "/opt/local/bin";
    private static final String GIT_EXE = GIT_HOME + "/git";

    public String gitCommand() {
        return GIT_EXE;
    }
}
