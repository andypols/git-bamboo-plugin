package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.commit.Commit;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitLogCommand {
    private String gitExe;
    private File sourceCodeDirectory;

    public GitLogCommand(String gitExe, File sourceCodeDirectory) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
    }

    public List<Commit> extractCommits() throws IOException {
        StringOutputStream stringOutputStream = new StringOutputStream();

        Execute execute = new Execute(new PumpStreamHandler(stringOutputStream));
        execute.setWorkingDirectory(sourceCodeDirectory);
        execute.setCommandline(new String[]{gitExe, "log", "--date=iso8601"});
        execute.execute();

        return new GitLogParser(stringOutputStream.toString()).extractCommits();
    }
}