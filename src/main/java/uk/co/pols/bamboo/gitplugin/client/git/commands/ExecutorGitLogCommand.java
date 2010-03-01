package uk.co.pols.bamboo.gitplugin.client.git.commands;

import com.atlassian.bamboo.commit.Commit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import uk.co.pols.bamboo.gitplugin.client.git.commands.CommandExecutor;

public class ExecutorGitLogCommand implements GitLogCommand {
    private static final Log log = LogFactory.getLog(ExecutorGitLogCommand.class);

    private String gitExe;
    private File sourceCodeDirectory;
    private String lastRevisionChecked;
    private CommandExecutor commandExecutor;

    public ExecutorGitLogCommand(String gitExe, File sourceCodeDirectory, String lastRevisionChecked, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.lastRevisionChecked = "null".equals(lastRevisionChecked) ? null : lastRevisionChecked;
        this.commandExecutor = commandExecutor;
    }

    public List<Commit> extractCommits() throws IOException {
        String logText = commandExecutor.execute(getCommandLine(), sourceCodeDirectory);

        log.info(logText);
        GitLogParser logParser = new GitLogParser(logText);

        List<Commit> commits = logParser.extractCommits(lastRevisionChecked);
        lastRevisionChecked = logParser.getMostRecentCommitDate();

        return commits;
    }

    public String getLastRevisionChecked() {
        return lastRevisionChecked;
    }

    private String[] getCommandLine() {
        if (lastRevisionChecked == null) {
            return new String[]{gitExe, "log", "-1", "--numstat", "--date=iso8601"};
        }
        return new String[]{gitExe, "log", "--numstat", "--date=iso8601", "--since=\"" + lastRevisionChecked + "\""};
    }
}