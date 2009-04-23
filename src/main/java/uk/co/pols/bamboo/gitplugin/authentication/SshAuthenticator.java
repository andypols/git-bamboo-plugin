package uk.co.pols.bamboo.gitplugin.authentication;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.JSch;
import uk.co.pols.bamboo.gitplugin.GitRepositoryConfig;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * Forces the private key into the ssh identity file so that github will authenticate with the repository
 */
public class SshAuthenticator {
    private static final String GIT_HUB_HOST = "github.com";
    private static final String GIT_HUB_USER = "git";

    public static Session authenticate(GitRepositoryConfig config) throws JSchException, UnsupportedEncodingException {
        JSch jsch = new JSch();
        jsch.addIdentity(config.getKeyFile(), config.getPassphrase().getBytes("ISO-8859-1"));

        return aNoneHostKeyCheckingSession(jsch);
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