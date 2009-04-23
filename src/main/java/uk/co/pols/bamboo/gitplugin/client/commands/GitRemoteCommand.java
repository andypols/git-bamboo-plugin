package uk.co.pols.bamboo.gitplugin.client.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;

import java.io.IOException;

public interface GitRemoteCommand {
    void add_origin(String repositoryUrl, BuildLogger buildLogger) throws IOException;
}
