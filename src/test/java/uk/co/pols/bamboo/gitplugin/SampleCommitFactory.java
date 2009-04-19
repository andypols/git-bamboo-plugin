package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.commit.CommitImpl;
import com.atlassian.bamboo.commit.CommitFileImpl;

public class SampleCommitFactory {
    public static CommitImpl commitWithFile(String revision) {
        CommitImpl commit = new CommitImpl();
        commit.addFile(commitFile(revision));
        return commit;
    }

    public static CommitFileImpl commitFile(String revision) {
        CommitFileImpl commitFile = new CommitFileImpl("src/main/java/uk/co/pols/bamboo/gitplugin/GitRepository.java");
        commitFile.setRevision(revision);
        return commitFile;
    }
}
