package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitImpl;
import com.atlassian.bamboo.author.AuthorImpl;
import com.atlassian.bamboo.author.Author;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;

public class GitLogParser {
    private static final String AUTHOR_LINE_PREFIX = "Author:";
    private static final String NEW_COMMIT_LINE_PREFIX = "commit";
    private static final DateTimeFormatter GIT_ISO_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss +SSSS");

    private String log;
    private static final String DATE_LINE_PREFIX = "Date:";

    public GitLogParser(String log) {
        this.log = (log == null) ? "" : log;
    }

    public List<Commit> extractCommits() {
        List<Commit> commits = new ArrayList<Commit>();
        String[] lines = log.split("\n");
        Author currentAuthor = null;
        DateTime date = null;

        for (String line : lines) {
            if (line.startsWith(NEW_COMMIT_LINE_PREFIX)) {
                if(currentAuthor != null && date != null) {
                    commits.add(new CommitImpl(currentAuthor, "Moo", date.toDate()));
                }
            } else if (line.startsWith(AUTHOR_LINE_PREFIX)) {
                currentAuthor = extractAuthor(line);
            } else if (line.startsWith(DATE_LINE_PREFIX)) {
                date = GIT_ISO_DATE_FORMAT.parseDateTime(line.substring(DATE_LINE_PREFIX.length()).trim());
            } else {

            }
        }

        if(currentAuthor != null) {
            commits.add(new CommitImpl(currentAuthor, "Moo", date.toDate()));
        }
        return commits;
    }

    private Author extractAuthor(String line) {
        String[] tokens = line.substring(AUTHOR_LINE_PREFIX.length()).trim().split(" ");
        if (tokens.length == 1 || tokens.length == 2) {
            return new AuthorImpl(tokens[0]);
        }
        if (tokens.length == 3) {
            return new AuthorImpl(tokens[0] + " " + tokens[1]);
        }

        return new AuthorImpl(Author.UNKNOWN_AUTHOR);
    }
}
