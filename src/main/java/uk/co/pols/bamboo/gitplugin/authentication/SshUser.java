package uk.co.pols.bamboo.gitplugin.authentication;

import com.jcraft.jsch.UserInfo;

/**
 * feeds in password silently into the SshSession
 */
class SshUser implements UserInfo {
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
