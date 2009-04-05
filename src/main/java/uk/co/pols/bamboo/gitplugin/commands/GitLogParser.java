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
        String commitId = null;
        List<CommitFile> commitFile = new ArrayList<CommitFile>();

        for (String line : log.split("\n")) {
            if (line.startsWith(NEW_COMMIT_LINE_PREFIX)) {
                if (currentAuthor != null && date != null) {
                    commits.add(previousCommit(currentAuthor, comment, date, commitFile));
                }
                comment = new StringBuffer();
                commitId = line.substring(NEW_COMMIT_LINE_PREFIX.length() + 1);
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

                        CommitFileImpl file = new CommitFileImpl(filename);
                        file.setRevision(commitId);
                        commitFile.add(file);
                    } catch (Exception e) {
                        comment.append(line).append("\n");
                    }
                } else {
                    if (line.length() > 0 && !"\n".equals(line)) {
                        comment.append(line).append("\n");
                    }
                }
            }
        }

        if (currentAuthor != null) {
            commits.add(previousCommit(currentAuthor, comment, date, commitFile));
        }
        return commits;
    }

    private CommitImpl previousCommit(Author currentAuthor, StringBuffer comment, DateTime date, List<CommitFile> commitFile) {
        CommitImpl o = new CommitImpl(currentAuthor, comment.toString(), date.toDate());
        o.setFiles(commitFile);
        return o;
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


/*
public class GitLogParser {
    private static final String AUTHOR_LINE_PREFIX = "Author:";
    private static final String NEW_COMMIT_LINE_PREFIX = "commit";
    private static final DateTimeFormatter GIT_ISO_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss +SSSS");
    private static final String DATE_LINE_PREFIX = "Date:";
    private static final String MERGE_LINE_PREFIX = "Merge";

    private String log;
    private String mostRecentCommitDate = null;

    public GitLogParser(String log) {
        this.log = (log == null) ? "" : log;
    }

    public class GitCommitLogEntry {
        private StringBuffer comment = new StringBuffer();
        private List<CommitFile> commitFiles = new ArrayList<CommitFile>();
        private String commitId;
        private Author currentAuthor = null;
        DateTime date = null;

        public GitCommitLogEntry(String commitId) {
            this.commitId = commitId;
        }

        public List<CommitFile> getCommitFiles() {
            return commitFiles;
        }

        public void addFileName(String line) {
            StringTokenizer st = new StringTokenizer(line);
            try {
                int linesAdded = Integer.parseInt(st.nextToken());
                int linesDeleted = Integer.parseInt(st.nextToken());

                commitFiles.add(fileWithinCurrentCommit(st.nextToken(), commitId));
            } catch (Exception e) {
                // can't parse, so lets add it to the comment, so we don't lose it
                comment.append(line).append("\n");
            }
        }

        public StringBuffer getComment() {
            return comment;
        }

        public void addCommentLine(String line) {
            comment.append(line).append("\n");
        }

        public boolean isValidCommit() {
            return currentAuthor != null && date != null;
        }

        public Commit toBambooCommit() {
             return previousCommit(currentAuthor, comment, date, commitFiles);
        }

        public void addAuthor(String line) {
            currentAuthor = extractAuthor(line);
        }

        public void addDate(String line) {
            DateTime date;
            String commitDate = line.substring(DATE_LINE_PREFIX.length()).trim();
            if (mostRecentCommitDate == null) {
                mostRecentCommitDate = commitDate;
            }
            date = GIT_ISO_DATE_FORMAT.parseDateTime(commitDate);
        }
    }

    public List<Commit> extractCommits() {
        List<Commit> commits = new ArrayList<Commit>();
        GitCommitLogEntry commitLogEntry = new GitCommitLogEntry("Unknown");

        for (String line : log.split("\n")) {
            if (isStartOfANewCommit(line)) {
                if (commitLogEntry.isValidCommit()) {
                    commits.add(commitLogEntry.toBambooCommit());
                }
                commitLogEntry = new GitCommitLogEntry(line.substring(NEW_COMMIT_LINE_PREFIX.length() + 1));
            } else if (isAuthorOfCommit(line)) {
                commitLogEntry.addAuthor(line);
            } else if (isDateOfCommit(line)) {
                commitLogEntry.addDate(line);
            } else if (isMergeOfCommit(line)) {
                // ignore
            } else if (isFileDetail(line)) {
                commitLogEntry.addFileName(line);
            } else if (line.length() > 0 && !"\n".equals(line)) {
                commitLogEntry.addCommentLine(line);
            }
        }

        if (commitLogEntry.isValidCommit()) {
            commits.add(commitLogEntry.toBambooCommit());
        }
        return commits;
    }

    private boolean isMergeOfCommit(String line) {
        return line.startsWith(MERGE_LINE_PREFIX);
    }

    private boolean isDateOfCommit(String line) {
        return line.startsWith(DATE_LINE_PREFIX);
    }

    private boolean isAuthorOfCommit(String line) {
        return line.startsWith(AUTHOR_LINE_PREFIX);
    }

    private boolean isStartOfANewCommit(String line) {
        return line.startsWith(NEW_COMMIT_LINE_PREFIX);
    }

    private boolean isFileDetail(String line) {
        return line.length() > 0 && Character.isDigit(line.toCharArray()[0]);
    }

    private CommitImpl previousCommit(Author currentAuthor, StringBuffer comment, DateTime date, List<CommitFile> commitFiles) {
        CommitImpl commitInfo = new CommitImpl(currentAuthor, comment.toString(), date.toDate());
        commitInfo.setFiles(commitFiles);
        return commitInfo;
    }

    private CommitFileImpl fileWithinCurrentCommit(String filename, String commitId) {
        CommitFileImpl file = new CommitFileImpl(filename);
        file.setRevision(commitId);
        return file;
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
*/