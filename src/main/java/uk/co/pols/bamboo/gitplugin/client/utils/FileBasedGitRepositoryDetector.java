package uk.co.pols.bamboo.gitplugin.client.utils;

import uk.co.pols.bamboo.gitplugin.client.utils.GitRepositoryDetector;

import java.io.File;
import java.io.IOException;

public class FileBasedGitRepositoryDetector implements GitRepositoryDetector {
    public boolean containsValidRepo(File sourceDir) {
        try {
            return sourceDir.exists() && (new File(sourceDir.getCanonicalPath() + File.separator + ".git").exists() || new File(sourceDir.getCanonicalPath() + File.separator + "HEAD").exists());
        } catch (IOException e) {
            return false;
        }
    }
}
