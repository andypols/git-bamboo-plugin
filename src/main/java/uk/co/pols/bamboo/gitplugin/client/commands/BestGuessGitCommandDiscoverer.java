package uk.co.pols.bamboo.gitplugin.client.commands;

public class BestGuessGitCommandDiscoverer implements GitCommandDiscoverer {
    public static final String DEFAULT_GIT_EXE = "/opt/local/bin/git";
    public static final String GIT_HOME = "GIT_HOME";

    public String gitCommand() {
        if(System.getProperty(GIT_HOME) != null) {
            return System.getProperty(GIT_HOME);
        }

        return DEFAULT_GIT_EXE;
    }
}
