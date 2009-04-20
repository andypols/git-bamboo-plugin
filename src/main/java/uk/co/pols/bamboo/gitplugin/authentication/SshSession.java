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
     * @param passFile    to store credentials
     * @return session or null if not successful
     */
    public Session getSession(File pemFile, String pemPassword, File passFile) throws IOException {
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
                if (passFile != null && passFile.exists()) {
                    passFile.delete();
                }
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
    private static class SshUser implements UserInfo, UIKeyboardInteractive {
        private String pemPassword;

        public SshUser(String pemPassword) {
            this.pemPassword = pemPassword;
        }

        public void showMessage(String message) {
            System.out.println("message = " + message);
        }

        public boolean promptYesNo(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public String getPassword() {
            // not used as we use the passphrase
            return null;
        }

        public String getPassphrase() {
            return pemPassword;
        }

        public String[] promptKeyboardInteractive(String destination, String name,
                                                  String instruction, String[] prompt, boolean[] echo) {
            return new String[]{getPassword()};
        }
    }

    public static void main(String[] args) throws IOException {
        SshSession sshSession = new SshSession();
        Session session = sshSession.getSession(new File("/Users/andy/.ssh/bamboo-gbp"), "bamboo-gbp", new File("passfile.txt"));

        session.disconnect();
    }
}
