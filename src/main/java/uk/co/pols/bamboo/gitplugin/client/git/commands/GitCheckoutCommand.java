package uk.co.pols.bamboo.gitplugin.client.git.commands;

import com.atlassian.bamboo.build.logger.BuildLogger;

import java.io.File;
import java.io.IOException;

public interface GitCheckoutCommand {
    void checkoutBranch(BuildLogger buildLogger, String branch, boolean create, File sourceDirectory) throws IOException;
}

