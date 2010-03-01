package uk.co.pols.bamboo.gitplugin.client.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;

import java.io.File;
import java.io.IOException;

public interface GitCloneCommand {
    void cloneUrl(BuildLogger buildLogger, String repositoryUrl, File sourceDirectory) throws IOException;
}
