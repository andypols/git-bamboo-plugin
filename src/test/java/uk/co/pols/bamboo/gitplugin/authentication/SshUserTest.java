package uk.co.pols.bamboo.gitplugin.authentication;

import junit.framework.TestCase;
import com.jcraft.jsch.UserInfo;

public class SshUserTest extends TestCase {
    private SshUser sshUser = new SshUser("passphrase");

    public void testImplementsJcshUser() {
        assertTrue("Must implement this for Jcsh to work", sshUser instanceof UserInfo);
    }

    public void testFeedsThePassphraseSilentlyIntoshSession() {
        assertEquals("passphrase", sshUser.getPassphrase());
    }

    public void testPromptsForYesNo() {
        assertTrue(sshUser.promptYesNo("Some message"));
    }

    public void testDoesNotSupportPasswordAuthentication() {
        assertFalse(sshUser.promptPassword("Some message"));

        try {
            sshUser.getPassword();
            fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            assertEquals("Don't support password authentication", e.getMessage());
        }
    }

    public void testDoesSupportPassphraseAuthentication() {
        assertTrue(sshUser.promptPassphrase("Some message"));
    }
}
