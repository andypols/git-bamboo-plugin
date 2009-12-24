package uk.co.pols.bamboo.gitplugin.client.utils;

import java.io.File;

public interface GitRepositoryDetector {
    boolean containsValidRepo(File sourceDir);
}
