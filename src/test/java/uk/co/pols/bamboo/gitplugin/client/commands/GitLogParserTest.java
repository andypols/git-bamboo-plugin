package uk.co.pols.bamboo.gitplugin.client.commands;

import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitFile;
import junit.framework.TestCase;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.List;

public class GitLogParserTest extends TestCase {
    private static final int MOST_RECENT_COMMIT = 0;
    private static final int FIRST_COMMIT = 28;

    public void testReturnsAnEmptyListOfCommitsIfTheLogIsEmpty() {
        GitLogParser parser = new GitLogParser("");

        assertTrue(parser.extractCommits("lastRevisionDate").isEmpty());
    }

    public void testReturnsAnEmptyListOfCommitsIfTheLogIsNull() {
        GitLogParser parser = new GitLogParser(null);

        assertTrue(parser.extractCommits("lastRevisionDate").isEmpty());
    }

    public void testKnowsTheNumberOfCommitsInTheGitLog() {
        GitLogParser parser = new GitLogParser(sampleLog);

        assertEquals(29, parser.extractCommits("2009-03-13 01:24:44 +0000").size());
    }

    public void testKnowsTheAuthorOfEachCommit() {
        GitLogParser parser = new GitLogParser(sampleLog);
        List<Commit> commits = parser.extractCommits("2009-03-13 01:24:44 +0000");

        assertEquals("Andy Pols", commits.get(0).getAuthor().getName());
        assertEquals("Fred", commits.get(1).getAuthor().getName());
        assertEquals("<andy@pols.co.uk>", commits.get(2).getAuthor().getName());
    }

    public void testKnowsTheDateOfEachCommit() throws ParseException {
        GitLogParser parser = new GitLogParser(sampleLog);
        List<Commit> commits = parser.extractCommits("2009-03-13 01:24:44 +0000");

        assertEquals(new DateTime(2009, 3, 22, 11, 21, 21, 0).toDate(), commits.get(MOST_RECENT_COMMIT).getDate());
        assertEquals(new DateTime(2009, 3, 22, 1, 9, 25, 0).toDate(), commits.get(1).getDate());
        assertEquals(new DateTime(2009, 3, 13, 1, 26, 14, 0).toDate(), commits.get(FIRST_COMMIT).getDate());
    }

    public void testKnowsTheCommentOfEachCommit() throws ParseException {
        GitLogParser parser = new GitLogParser(sampleLog);
        List<Commit> commits = parser.extractCommits("2009-03-13 01:24:44 +0000");

        assertEquals("    adding some notes about what to do next\n", commits.get(MOST_RECENT_COMMIT).getComment());
        assertEquals("    Using parent's isWorkspaceEmpty to know when to initialise the repository and create a link to the remote repo url\n    \n    More removing of interfaces I don't care about\n", commits.get(1).getComment());
        assertEquals("    ignore the java build files\n", commits.get(FIRST_COMMIT).getComment());
    }

    public void testKnowsTheDateOfTheMostRecentCommit() {
        GitLogParser parser = new GitLogParser(sampleLog);
        parser.extractCommits("2009-03-13 01:24:44 +0000");

        assertEquals("2009-03-22 11:21:21 +0000", parser.getMostRecentCommitDate());
    }

    public void testIgnoresMergeLogLines() {
        GitLogParser parser = new GitLogParser("commit a6d16af596b2d122f4348ded85ca14a74b6adaae\n" +
                "Merge: 80d2bba... affad78...\n" +
                "Author: Bobby Brown <frank@zappa.us>\n" +
                "Date:   2009-03-22 11:21:21 +0000\n" +
                "\n" +
                "    Merge branch 'new-ui'\n" +
                "\n");

        List<Commit> commits = parser.extractCommits("2009-03-21 12:50:56 +0000");

        assertEquals(1, commits.size());
        assertEquals("    Merge branch 'new-ui'\n", commits.get(0).getComment());
    }

    public void testIgoresTheCommit() {
        GitLogParser parser = new GitLogParser(sampleLog);

        assertEquals(29, parser.extractCommits("2009-03-13 01:24:44 +0000").size());
    }

    public void testKnowsWhichFilesHaveChangedInTheCommit() {
        GitLogParser parser = new GitLogParser(sampleLog);
        List<Commit> commits = parser.extractCommits("2009-03-13 01:24:44 +0000");

        List<CommitFile> updatedFiles = commits.get(MOST_RECENT_COMMIT).getFiles();
        assertEquals(1, updatedFiles.size());
        CommitFile commitFile = updatedFiles.get(0);
        assertEquals("src/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java", commitFile.getName());
        assertEquals("The commit hash identifies the version", "264438f6f9e7a3cb341eb8270a0e520e91f10db5", commitFile.getRevision());
    }

    public void testKnowsWhenACommitHasMultipleFileChanges() {
        GitLogParser parser = new GitLogParser(sampleLog);
        List<Commit> commits = parser.extractCommits("2009-03-13 01:24:44 +0000");

        List<CommitFile> updatedFiles = commits.get(5).getFiles();
        assertEquals(2, updatedFiles.size());
        CommitFile commitFile = updatedFiles.get(0);
        assertEquals("src/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java", commitFile.getName());
        assertEquals("The commit hash identifies the version", "016d94c83c3697773f64f51c0b2e5a652093b2c6", commitFile.getRevision());

        commitFile = updatedFiles.get(1);
        assertEquals("src/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java", commitFile.getName());
        assertEquals("The commit hash identifies the version", "016d94c83c3697773f64f51c0b2e5a652093b2c6", commitFile.getRevision());
    }

    private String sampleLog = "commit 264438f6f9e7a3cb341eb8270a0e520e91f10db5\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-22 11:21:21 +0000\n" +
            "\n" +
            "    adding some notes about what to do next\n" +
            "\n" +
            "10\t5\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "\n" +
            "commit 4dba98e9c57027bae632dd645f3e794df0fe07b7\n" +
            "Author: Fred <andy@pols.co.uk>\n" +
            "Date:   2009-03-22 01:09:25 +0000\n" +
            "\n" +
            "    Using parent's isWorkspaceEmpty to know when to initialise the repository and create a link to the remote repo url\n" +
            "    \n" +
            "    More removing of interfaces I don't care about\n" +
            "\n" +
            "4\t33\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "\n" +
            "commit 72551abf6ff66d42639b04253ecaccf8a19cf971\n" +
            "Author: <andy@pols.co.uk>\n" +
            "Date:   2009-03-22 00:12:47 +0000\n" +
            "\n" +
            "    removed interfaces MutableQuietPeriodAwareRepository and RepositoryEventAware as not using any of the their methods\n" +
            "\n" +
            "3\t63\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "\n" +
            "commit acc4e814486e9daa054e5bc299657ef66f53728f\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 12:50:56 +0000\n" +
            "\n" +
            "    checks out the repository and pulls\n" +
            "    Now need to workout how to tell bamboo the change set\n" +
            "\n" +
            "111\t24\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "\n" +
            "commit b3396a3dd35253dec37c195f976b406800e8e7cf\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 12:47:50 +0000\n" +
            "\n" +
            "    remove unused imports\n" +
            "\n" +
            "0\t2\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit 016d94c83c3697773f64f51c0b2e5a652093b2c6\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 10:40:52 +0000\n" +
            "\n" +
            "    Don't know why i tested the name for equality as can't change - is a constant\n" +
            "\n" +
            "0\t1\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "1\t12\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit 45d05060fb738b602002d18b3d889236b372a6aa\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 10:38:31 +0000\n" +
            "\n" +
            "    Assume we don't need to do anything special for the onInitialBuild\n" +
            "    Implemented isRepositoryDifferent as a crude test of the url and name\n" +
            "\n" +
            "15\t9\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "70\t1\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit c1f8709d82c53242f91eaf0f2a147e186c7c5112\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 00:55:22 +0000\n" +
            "\n" +
            "    improved up test method description\n" +
            "\n" +
            "commit 770d457a3138243990f5a4952ef4886bf2d3bbda\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 00:55:03 +0000\n" +
            "\n" +
            "    testing reporting of multiple errors at same time\n" +
            "    Improved the intent of single error tests\n" +
            "\n" +
            "19\t6\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit c286386d518def4855e5ee117d69b40e2d002f45\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 00:43:05 +0000\n" +
            "\n" +
            "    refactored duplicate validation code\n" +
            "    saves the repository values in the build context\n" +
            "\n" +
            "20\t98\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "22\t0\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit a06eded11ed9a1f97d5a0000bf23586450e8df70\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 00:20:25 +0000\n" +
            "\n" +
            "    cleaning up the error messages\n" +
            "\n" +
            "1\t1\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "11\t1\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit 5dfdbb909f2b5881696398c5604eecda1ecf2e13\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-21 00:12:54 +0000\n" +
            "\n" +
            "    testing the validation (you would have thought bamboo would do this as it knows that they're mandatory)\n" +
            "\n" +
            "75\t102\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "0\t21\tsrc/main/resources/uk/co/pols/bamboo/gitplugin/gitRepositoryEdit.ftl\n" +
            "1\t1\tsrc/main/resources/uk/co/pols/bamboo/gitplugin/gitRepositoryView.ftl\n" +
            "21\t18\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit a4199e00f9b7fbcd367c5a05fd319bab1b4c1f90\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-20 01:23:29 +0000\n" +
            "\n" +
            "    only init the repository if it already exists\n" +
            "\n" +
            "19\t17\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "\n" +
            "commit a6a3bb121d46528664c660af69318caa6947d063\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-20 01:03:42 +0000\n" +
            "\n" +
            "    added branch to ui\n" +
            "\n" +
            "21\t0\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "5\t0\tsrc/main/resources/uk/co/pols/bamboo/gitplugin/gitRepositoryEdit.ftl\n" +
            "2\t1\tsrc/main/resources/uk/co/pols/bamboo/gitplugin/gitRepositoryView.ftl\n" +
            "\n" +
            "commit 555e351c3f9fad5bb9684767d087f7abc2311f53\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-20 00:33:05 +0000\n" +
            "\n" +
            "    spike of executing commands to pull contents from a git hub repo\n" +
            "\n" +
            "29\t7\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "\n" +
            "commit 6fafa9b2dc87d2a2404be02a07e292bfee88c331\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-16 20:13:51 +0000\n" +
            "\n" +
            "    testing password authentication option and links to GitHub docs\n" +
            "\n" +
            "9\t1\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit 46e1f2a9f638082722ff321653226c3a5b3ac465\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-16 20:12:25 +0000\n" +
            "\n" +
            "    Correct label for Authentication type\n" +
            "\n" +
            "1\t1\tsrc/main/resources/uk/co/pols/bamboo/gitplugin/gitRepositoryEdit.ftl\n" +
            "\n" +
            "commit d328c7bd45c89eb5e669bd5af8310838bb208b4c\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-15 23:43:55 +0000\n" +
            "\n" +
            "    Moved ftl templates into package directory to ensure unique\n" +
            "    Added descriptions to fields\n" +
            "\n" +
            "4\t4\tsrc/main/resources/atlassian-plugin.xml\n" +
            "0\t42\tsrc/main/resources/gitRepositoryEdit.ftl\n" +
            "0\t1\tsrc/main/resources/gitRepositoryView.ftl\n" +
            "25\t0\tsrc/main/resources/uk/co/pols/bamboo/gitplugin/gitRepositoryEdit.ftl\n" +
            "1\t0\tsrc/main/resources/uk/co/pols/bamboo/gitplugin/gitRepositoryView.ftl\n" +
            "\n" +
            "commit 17a328bbc8eaf925a09d1cc3bb2250d58954abea\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-15 00:05:25 +0000\n" +
            "\n" +
            "    cutting out more cruft\n" +
            "\n" +
            "4\t21\tbuild.xml\n" +
            "\n" +
            "commit 71a136346dff5f0b334306e7ce5df5ecce216472\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-15 00:01:33 +0000\n" +
            "\n" +
            "    removed more junk and added a jar task\n" +
            "\n" +
            "8\t12\tbuild.xml\n" +
            "\n" +
            "commit 1ddbd96d20b9242e79ca8d03fd66e2aba4e07146\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-14 23:55:16 +0000\n" +
            "\n" +
            "    removed test compilation as currently run these in intellij (and I'll do it diff when I add tests to the build farm)\n" +
            "\n" +
            "3\t34\tbuild.xml\n" +
            "\n" +
            "commit 3084d48d55f1e1961369009b275397857af2cc4f\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-14 23:50:27 +0000\n" +
            "\n" +
            "    cutting out stuff I don't need\n" +
            "\n" +
            "9\t16\tbuild.xml\n" +
            "\n" +
            "commit acb18ed0ecc4d1fb345466556ebde7c03d1a9db8\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-14 23:40:43 +0000\n" +
            "\n" +
            "    intellij generated ant build file\n" +
            "\n" +
            "121\t0\tbuild.xml\n" +
            "\n" +
            "commit cab001eaa02b033a2e3c10a1e2126f3cf45b7375\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-14 13:24:00 +0000\n" +
            "\n" +
            "    cleaning up cut&paste from svn and adding some ssh options to the UI\n" +
            "\n" +
            "5\t1260\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "24\t4\tsrc/main/resources/gitRepositoryEdit.ftl\n" +
            "\n" +
            "commit 44487f10d241a82241f4323a4bfebffa4ee8493e\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-14 13:07:45 +0000\n" +
            "\n" +
            "    hacking around to see what works with the ui.  How have a working auth type...\n" +
            "\n" +
            "1393\t13\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "19\t2\tsrc/main/resources/gitRepositoryEdit.ftl\n" +
            "18\t1\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit 09226a700963ed457fcb3c103f58d8752e40abdf\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-14 01:10:17 +0000\n" +
            "\n" +
            "    Started fleshing out a basic UI for the Git repository\n" +
            "\n" +
            "96\t15\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "5\t1\tsrc/main/resources/gitRepositoryEdit.ftl\n" +
            "1\t1\tsrc/main/resources/gitRepositoryView.ftl\n" +
            "11\t0\tsrc/test/java/uk.co.pols.bamboo.gitplugin/GitRepositoryTest.java\n" +
            "\n" +
            "commit ae12e2abbf84dba3367f54913f0ff02074424e44\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-13 01:29:10 +0000\n" +
            "\n" +
            "    tweak ignore\n" +
            "\n" +
            "1\t0\t.gitignore\n" +
            "\n" +
            "commit 60f6a6cabe727b14897b4d98bca91ce646a07d3d\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-13 01:27:52 +0000\n" +
            "\n" +
            "    Initial plugin - just Adds Git to the repository dropdown... does not actually do anything just yet!\n" +
            "\n" +
            "65\t0\tsrc/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java\n" +
            "14\t0\tsrc/main/resources/atlassian-plugin.xml\n" +
            "1\t0\tsrc/main/resources/gitRepositoryEdit.ftl\n" +
            "1\t0\tsrc/main/resources/gitRepositoryView.ftl\n" +
            "\n" +
            "commit 0ba53eb47ee4c79612fdf55f072952d8ef80b957\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-13 01:26:14 +0000\n" +
            "\n" +
            "    ignore the java build files\n" +
            "\n" +
            "2\t0\t.gitignore\n" +
            "\n" +
            "commit 1b014a81da79573aa960e7f0b22493da9fdb9310\n" +
            "Author: Andy Pols <andy@pols.co.uk>\n" +
            "Date:   2009-03-13 01:24:44 +0000\n" +
            "\n" +
            "    first commit\n" +
            "\n" +
            "3\t0\tREADME.textile";
}
