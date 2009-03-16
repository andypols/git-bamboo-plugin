package uk.co.pols.bamboo.gitplugin;

import junit.framework.TestCase;
import com.atlassian.bamboo.repository.AuthenticationType;
import com.atlassian.bamboo.repository.NameValuePair;

import java.util.List;

public class GitRepositoryTest extends TestCase {
    private GitRepository gitRepository = new GitRepository();

    public void testProvidesANameToAppearInTheGuiRepositoryDrownDown() {
        assertEquals("Git", gitRepository.getName());
    }

    public void testRepositoryProvidesAnShhAuthenticationOption() {
        assertContains(gitRepository.getAuthenticationTypes(), AuthenticationType.SSH.getNameValue());
    }

    public void testRepositoryProvidesAPasswordAuthenticationOption() {
        assertContains(gitRepository.getAuthenticationTypes(), AuthenticationType.PASSWORD.getNameValue());
    }

    public void testProvidesALinkToTheGitHubGuidesPage() {
        assertEquals("http://github.com/guides/home", gitRepository.getUrl());
    }

    private void assertContains(List<NameValuePair> authenticationTypes, NameValuePair expectedNamedValue) {
        for (NameValuePair nameValuePair : authenticationTypes) {
            if(nameValuePair.getLabel().equals(expectedNamedValue.getLabel()))  {
                return;
            }
        }
        fail("Could not find the expected value " + expectedNamedValue.getLabel());
    }
}