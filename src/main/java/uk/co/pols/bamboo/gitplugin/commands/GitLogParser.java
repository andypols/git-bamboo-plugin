package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitImpl;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.commit.CommitFileImpl;
import com.atlassian.bamboo.author.AuthorImpl;
import com.atlassian.bamboo.author.Author;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class GitLogParser {
    private static final String AUTHOR_LINE_PREFIX = "Author:";
    private static final String NEW_COMMIT_LINE_PREFIX = "commit";
    private static final DateTimeFormatter GIT_ISO_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss +SSSS");
    private static final String DATE_LINE_PREFIX = "Date:";

    private String log;
    private String mostRecentCommitDate = null;

    public GitLogParser(String log) {
        this.log = (log == null) ? "" : log;
    }

    public List<Commit> extractCommits() {
        List<Commit> commits = new ArrayList<Commit>();
        Author currentAuthor = null;
        DateTime date = null;
        StringBuffer comment = new StringBuffer();
        List<CommitFile> commitFile = new ArrayList<CommitFile>();

        for (String line : log.split("\n")) {
            if (line.startsWith(NEW_COMMIT_LINE_PREFIX)) {
                if (currentAuthor != null && date != null) {
                    commits.add(new CommitImpl(currentAuthor, comment.toString(), date.toDate()));
                }
                comment = new StringBuffer();
                commitFile = new ArrayList<CommitFile>();
            } else if (line.startsWith(AUTHOR_LINE_PREFIX)) {
                currentAuthor = extractAuthor(line);
            } else if (line.startsWith(DATE_LINE_PREFIX)) {
                String commitDate = line.substring(DATE_LINE_PREFIX.length()).trim();
                if (mostRecentCommitDate == null) {
                    mostRecentCommitDate = commitDate;
                }
                date = GIT_ISO_DATE_FORMAT.parseDateTime(commitDate);
            } else if (line.startsWith("Merge")){
                // ignore
            } else {
                if (line.length() > 0 && Character.isDigit(line.toCharArray()[0])) {
                    // Could be a commit message or fileDetails (FileDetails always starts with an int)
                    StringTokenizer st = new StringTokenizer(line);
                    try {
                        int linesAdded = Integer.parseInt(st.nextToken());
                        int linesDeleted = Integer.parseInt(st.nextToken());
                        String filename = st.nextToken();

                        commitFile.add(new CommitFileImpl(filename));
                    } catch (Exception e) {
                    }
                } else {
                    if (line.length() > 0 && !"\n".equals(line)) {
                        comment.append(line).append("\n");
                    }
                }
            }
        }

        if (currentAuthor != null) {
            commits.add(new CommitImpl(currentAuthor, comment.toString(), date.toDate()));
        }
        return commits;
    }

    public String getMostRecentCommitDate() {
        return mostRecentCommitDate;
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