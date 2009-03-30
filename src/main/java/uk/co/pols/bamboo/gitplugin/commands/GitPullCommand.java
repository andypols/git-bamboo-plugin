package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;

import java.io.IOException;

public interface GitPullCommand {
    void pullUpdatesFromRemoteRepository(BuildLogger buildLogger, String repositoryUrl) throws IOException;
}
