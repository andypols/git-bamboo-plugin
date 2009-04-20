package uk.co.pols.bamboo.gitplugin.authentication;

import com.jcraft.jsch.*;
import com.atlassian.bamboo.repository.RepositoryException;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.LogEntry;

import java.io.File;
import java.io.IOException;
import java.util.List;

import uk.co.pols.bamboo.gitplugin.GitClient;
import uk.co.pols.bamboo.gitplugin.CmdLineGitClient;
import uk.co.pols.bamboo.gitplugin.GitRepositoryConfig;

public class SshSession {
    private static final String GIT_HUB_HOST = "github.com";
    private static final String GIT_HUB_USER = "git";
    private static final String USERNAME = GIT_HUB_USER;

    private Session session = null;

    /**
     * Gets a session from the cache or establishes a new session if necessary
     *
     * @param pemFile     File to use for public key authentication
     * @param pemPassword to use for accessing the pemFile (optional)
     * @return session or null if not successful
     */
    public Session getSession(File pemFile, String pemPassword) throws IOException {
        if (session == null || !session.isConnected()) {
            System.out.println("Authenticating with github.com");
            try {
                JSch jsch = new JSch();
                session = jsch.getSession(USERNAME, GIT_HUB_HOST, 22);
                if (pemFile != null) {
                    jsch.addIdentity(pemFile.getAbsolutePath(), pemPassword);
                }
                session.setUserInfo(new SshUser(pemPassword));
                session.connect();
                System.out.println("SSH :: connected to " + GIT_HUB_HOST + " using " + pemFile);
            } catch (JSchException e) {
                IOException ex = new IOException(e.getMessage());
                ex.initCause(e);
                throw ex;
            }
        }
        return session;
    }

    /**
     * feeds in password silently into JSch
     */
    private static class SshUser implements UserInfo {
        private String passphrase;

        public SshUser(String passphrase) {
            this.passphrase = passphrase;
        }

        public void showMessage(String message) {
        }

        public boolean promptYesNo(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return false;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public String getPassword() {
            throw new UnsupportedOperationException("Don't support password authentication");
        }

        public String getPassphrase() {
            return passphrase;
        }
    }

    public static void main(String[] args) throws IOException, RepositoryException {


        SshSession sshSession = new SshSession();
        Session session = sshSession.getSession(new File("/Users/andy/.ssh/bamboo-gbp"), "bamboo-gbp");

        System.out.println("session.isConnected() = " + session.isConnected());

        GitClient client = new CmdLineGitClient();
        GitRepositoryConfig gitRepositoryConfig = new GitRepositoryConfig();
        gitRepositoryConfig.setRepositoryUrl("git://github.com/andypols/git-bamboo-plugin.git");
        client.initialiseRepository(new File("/Users/andy/projects/moocow"), "KEY", null, gitRepositoryConfig, true, new BuildLogger() {
            public List<LogEntry> getBuildLog() {
                return null;
            }

            public List<LogEntry> getErrorLog() {
                return null;
            }

            public List<String> getStringErrorLogs() {
                return null;
            }

            public String addBuildLogEntry(LogEntry logEntry) {
                return null;
            }

            public String addBuildLogEntry(String string) {
                return null;
            }

            public String addBuildLogHeader(String string, boolean b) {
                return null;
            }

            public String addErrorLogEntry(LogEntry logEntry) {
                return null;
            }

            public String addErrorLogEntry(String string) {
                return null;
            }

            public void startStreamingBuildLogs(int i, String string) {
            }

            public void stopStreamingBuildLogs() {
            }

            public void clearBuildLog() {
            }

            public long getTimeOfLastLog() {
                return 0;
            }
        });
//        System.out.println("Now lets try and do something with a git command!");
        session.disconnect();
    }
}
