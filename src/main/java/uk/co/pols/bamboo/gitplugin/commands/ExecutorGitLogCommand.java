package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.commit.Commit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class ExecutorGitLogCommand implements GitLogCommand {
    private static final Log log = LogFactory.getLog(ExecutorGitLogCommand.class);

    private String gitExe;
    private File sourceCodeDirectory;
    private String lastRevisionChecked;
    private CommandExecutor commandExecutor;

    public ExecutorGitLogCommand(String gitExe, File sourceCodeDirectory, String lastRevisionChecked, CommandExecutor commandExecutor) {
        this.gitExe = gitExe;
        this.sourceCodeDirectory = sourceCodeDirectory;
        this.lastRevisionChecked = lastRevisionChecked;
        this.commandExecutor = commandExecutor;
    }

    public List<Commit> extractCommits() throws IOException {
        String logText = commandExecutor.execute(getCommandLine(), sourceCodeDirectory);

        log.info("**************>>" + logText + "<<**************");
        GitLogParser logParser = new GitLogParser(logText);

        List<Commit> commits = logParser.extractCommits(lastRevisionChecked
        );
        lastRevisionChecked = logParser.getMostRecentCommitDate();

        for (Commit commit : commits) {
            log.info("commit.getComment() = " + commit.getComment());
        }
//        Collections.sort(commits, new Comparator<Commit>() {
//            public int compare(Commit o1, Commit o2) {
//                return o1.getDate().compareTo(o2.getDate());
//            }
//        });

        return commits;
    }

    public String getLastRevisionChecked() {
        return lastRevisionChecked;
    }

    private String[] getCommandLine() {
        if (lastRevisionChecked != null) {
            log.info("RUNNING ***** " + gitExe + " log --numstat --date=iso8601 --since=\"" + lastRevisionChecked + "\"");
            return new String[]{gitExe, "log", "--numstat", "--date=iso8601", "--since=\"" + lastRevisionChecked + "\""};
        }
        log.info("RUNNING ***** " + gitExe + " log -1 --numstat --date=iso8601");
        return new String[]{gitExe, "log", "-1", "--numstat", "--date=iso8601"};
    }
}