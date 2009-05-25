package uk.co.pols.bamboo.gitplugin.client.commands;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitImpl;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.commit.CommitFileImpl;
import com.atlassian.bamboo.author.AuthorImpl;
import com.atlassian.bamboo.author.Author;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;

public class GitLogParser {
    private static final String AUTHOR_LINE_PREFIX = "Author:";
    private static final String NEW_COMMIT_LINE_PREFIX = "commit";
    private static final DateTimeFormatter GIT_ISO_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
    private static final String DATE_LINE_PREFIX = "Date:";
    private static final String MERGE_LINE_PREFIX = "Merge";

    private String log;
    private String mostRecentCommitDate = null;

    public GitLogParser(String log) {
        this.log = (log == null) ? "" : log;
    }

    public List<Commit> extractCommits(String notOnlastRevisionChecked) {
        List<Commit> commits = new ArrayList<Commit>();
        GitCommitLogEntry commitLogEntry = new GitCommitLogEntry("Unknown");

        for (String line : log.split("\n")) {
            if (line.startsWith(NEW_COMMIT_LINE_PREFIX)) {
                if (commitLogEntry.isValidCommit(notOnlastRevisionChecked)) {
                    commits.add(commitLogEntry.toBambooCommit());
                }
                commitLogEntry = new GitCommitLogEntry(line.substring(NEW_COMMIT_LINE_PREFIX.length() + 1));
            } else if (line.startsWith(AUTHOR_LINE_PREFIX)) {
                commitLogEntry.addAuthor(line);
            } else if (line.startsWith(DATE_LINE_PREFIX)) {
                commitLogEntry.addDate(line);
            } else if (line.length() > 0 && Character.isDigit(line.toCharArray()[0])) {
                commitLogEntry.addFileName(line);
            } else if (line.startsWith(MERGE_LINE_PREFIX)) {
                // ignore
            } else if (line.length() > 0 && !"\n".equals(line)) {
                commitLogEntry.addCommentLine(line);
            }
        }

        if (commitLogEntry.isValidCommit(notOnlastRevisionChecked)) {
            commits.add(commitLogEntry.toBambooCommit());
        }
        return commits;
    }

    public String getMostRecentCommitDate() {
        return mostRecentCommitDate;
    }

    public class GitCommitLogEntry {
        private StringBuffer comment = new StringBuffer();
        private List<CommitFile> commitFiles = new ArrayList<CommitFile>();
        private String commitId;
        private Author author = null;
        private DateTime date = null;

        public GitCommitLogEntry(String commitId) {
            this.commitId = commitId;
        }

        public void addFileName(String line) {
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            try {
                skipLinesAdded(stringTokenizer);
                skipLinesDeleted(stringTokenizer);
                commitFiles.add(fileWithinCurrentCommit(stringTokenizer.nextToken(), commitId));
            } catch (Exception e) {
                // can't parse, so lets add it to the comment, so we don't lose it
                comment.append(line).append("\n");
            }
        }

        public void addCommentLine(String line) {
            comment.append(line).append("\n");
        }

        public void addAuthor(String line) {
            author = extractAuthor(line);
        }

        public void addDate(String line) {
            String commitDate = line.substring(DATE_LINE_PREFIX.length()).trim();
            if (mostRecentCommitDate == null) {
                mostRecentCommitDate = commitDate;
            }
            date = GIT_ISO_DATE_FORMAT.parseDateTime(commitDate);
        }

        public boolean isValidCommit(String dateOfLastRecordCommit) {
            return author != null && date != null && !sameDateStampAsThePreviouslyProcessedCommit(dateOfLastRecordCommit);
        }

        private boolean sameDateStampAsThePreviouslyProcessedCommit(String dateOfLastRecordCommit) {
            if (dateOfLastRecordCommit == null) {
                return false;
            }

            DateTime dateTime = GIT_ISO_DATE_FORMAT.parseDateTime(dateOfLastRecordCommit);
            return dateTime.equals(date);
        }

        public Commit toBambooCommit() {
            CommitImpl commit = new CommitImpl(author, comment.toString(), date.toDate());
            commit.setFiles(commitFiles);
            return commit;
        }

        private int skipLinesDeleted(StringTokenizer stringTokenizer) {
            return Integer.parseInt(stringTokenizer.nextToken());
        }

        private int skipLinesAdded(StringTokenizer st) {
            return Integer.parseInt(st.nextToken());
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

        private CommitFileImpl fileWithinCurrentCommit(String filename, String commitId) {
            CommitFileImpl file = new CommitFileImpl(filename);
            file.setRevision(commitId);
            return file;
        }
    }
}