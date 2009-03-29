package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.build.logger.BuildLogger;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitLogCommand {
    private String gitExe;
    private File sourceCodeDirectory;
    private String lastRevisionChecked;
    private CommandExecutor commandExecutor;

    public GitLogCommand(String gitExe, File sourceCodeDirectory, String lastRevisionChecked, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.lastRevisionChecked = lastRevisionChecked;
        this.commandExecutor = commandExecutor;
    }

    public List<Commit> extractCommits(BuildLogger buildLogger) throws IOException {
        StringOutputStream stringOutputStream = new StringOutputStream();

        String logText = commandExecutor.execute(getCommandLine(), sourceCodeDirectory);
        GitLogParser logParser = new GitLogParser(buildLogger.addBuildLogEntry(logText));

        List<Commit> commits = logParser.extractCommits();
        lastRevisionChecked = logParser.getMostRecentCommitDate();

        stringOutputStream.close();
        return commits;
    }

    public String getLastRevisionChecked() {
        return lastRevisionChecked;
    }

    private String[] getCommandLine() {
        if (lastRevisionChecked != null) {
            return new String[]{gitExe, "log", "--date=iso8601", "--since=\"" + lastRevisionChecked + "\""};
        }
        return new String[]{gitExe, "log", "-1", "--date=iso8601"};
    }
}