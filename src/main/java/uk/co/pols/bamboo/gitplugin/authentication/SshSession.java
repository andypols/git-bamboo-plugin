package uk.co.pols.bamboo.gitplugin.authentication;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;

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

    public static void main(String[] args) throws IOException {
        SshSession sshSession = new SshSession();
        Session session = sshSession.getSession(new File("/Users/andy/.ssh/bamboo-gbp"), "bamboo-gbp");

        session.disconnect();
    }
}
