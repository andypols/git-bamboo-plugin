package uk.co.pols.bamboo.gitplugin.authentication;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.JSch;
import com.atlassian.bamboo.repository.RepositoryException;
import uk.co.pols.bamboo.gitplugin.GitRepositoryConfig;

import java.util.Hashtable;

/**
 * Forces the private key into the ssh identity file so that github will authenticate with the repository
 */
public abstract class SshAuthenticatedRunner implements AuthenticationRunner {
    private static final String GIT_HUB_HOST = "github.com";
    private static final String GIT_HUB_USER = "git";

    public void run(GitRepositoryConfig config) throws RepositoryException {
        JSch jsch = new JSch();

        try {
            jsch.addIdentity(config.getKeyFile(), config.getPassphrase().getBytes("ISO-8859-1"));
            Session session = aNoneHostKeyCheckingSession(jsch);
            try {
                task();
            } finally {
                session.disconnect();
            }
        } catch (Exception e) {
            throw new RepositoryException("failed to run authenticated task", e);
        }
    }

    private static Session aNoneHostKeyCheckingSession(JSch jsch) throws JSchException {
        Session session = jsch.getSession(GIT_HUB_USER, GIT_HUB_HOST, 22);
        sshConfiguration(session);
        session.connect();

        return session;
    }

    private static void sshConfiguration(Session session) {
        Hashtable<String,String> hashtable = new Hashtable<String, String>();
        hashtable.put("StrictHostKeyChecking", "no");
        session.setConfig(hashtable);
    }
}