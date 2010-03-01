package uk.co.pols.bamboo.gitplugin.client.git.commands;

import java.io.IOException;

interface WhichCommand {
    String which(String command) throws IOException;
}
