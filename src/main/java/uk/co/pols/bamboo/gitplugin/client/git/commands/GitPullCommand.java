package uk.co.pols.bamboo.gitplugin.client.git.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;

import java.io.IOException;

public interface GitPullCommand {
    void pullUpdatesFromRemoteRepository(BuildLogger buildLogger, String repositoryUrl, String branch) throws IOException;
}
