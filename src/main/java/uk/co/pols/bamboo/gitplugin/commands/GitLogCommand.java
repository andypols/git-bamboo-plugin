package uk.co.pols.bamboo.gitplugin.commands;

import com.atlassian.bamboo.commit.Commit;

import java.util.List;
import java.io.IOException;

public interface GitLogCommand {
    List<Commit> extractCommits() throws IOException;

    String getLastRevisionChecked();
}
