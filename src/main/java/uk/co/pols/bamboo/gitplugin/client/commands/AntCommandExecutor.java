package uk.co.pols.bamboo.gitplugin.client.commands;

import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import java.io.File;
import java.io.IOException;

public class AntCommandExecutor implements CommandExecutor {

    public String execute(String[] commandLine, File sourceCodeDirectory) throws IOException {
        StringOutputStream stringOutputStream = new StringOutputStream();

        Execute execute = new Execute(new PumpStreamHandler(stringOutputStream));
        execute.setWorkingDirectory(sourceCodeDirectory);
        execute.setCommandline(commandLine);
        execute.execute();

        String output = stringOutputStream.toString();
        stringOutputStream.close();
        return output;
    }
}