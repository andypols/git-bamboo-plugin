package uk.co.pols.bamboo.gitplugin.client.commands;

import java.io.IOException;

interface WhichCommand {
    String which(String command) throws IOException;
}
