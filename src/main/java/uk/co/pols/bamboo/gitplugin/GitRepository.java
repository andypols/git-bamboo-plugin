package uk.co.pols.bamboo.gitplugin;

import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.repository.*;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.BuildChanges;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.BuildChangesImpl;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Provides GIT and GITHUB support for the Bamboo Build Server
 * <p/>
 * TODO Let user define the location of the git exe
 */
public class GitRepository extends AbstractRepository implements SelectableAuthenticationRepository {
    private static final Log log = LogFactory.getLog(GitRepository.class);

    public static final String NAME = "Git";
    public static final String KEY = "git";

    private static final String REPO_PREFIX = "repository.git.";
    public static final String GIT_REPO_URL = REPO_PREFIX + "repositoryUrl";
    public static final String GIT_BRANCH = REPO_PREFIX + "branch";

    private String repositoryUrl;
    private String branch;

    private static final String GIT_HOME = "/opt/local/bin";
    private static final String GIT_EXE = GIT_HOME + "/git";

    public String getName() {
        return NAME;
    }

    public String getUrl() {
        return "http://github.com/guides/home";
    }

    /* -------- Make the properties available to the UI -------- */

    public String getHost() {
        return UNKNOWN_HOST;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = StringUtils.trim(repositoryUrl);
    }

    public List<NameValuePair> getAuthenticationTypes() {
        throw new UnsupportedOperationException("TO DO");
    }

    public ErrorCollection validate(BuildConfiguration buildConfiguration) {
        ErrorCollection errorCollection = super.validate(buildConfiguration);

        validateMandatoryField(buildConfiguration, errorCollection, GIT_REPO_URL, "Please specify where the repository is located");
        validateMandatoryField(buildConfiguration, errorCollection, GIT_BRANCH, "Please specify which branch you want to build");

        return errorCollection;
    }

    public void prepareConfigObject(BuildConfiguration buildConfiguration) {
    }

    public HierarchicalConfiguration toConfiguration() {
        HierarchicalConfiguration configuration = super.toConfiguration();
        configuration.setProperty(GIT_REPO_URL, getRepositoryUrl());
        configuration.setProperty(GIT_BRANCH, getBranch());
        return configuration;
    }

    public void populateFromConfig(HierarchicalConfiguration config) {
        super.populateFromConfig(config);

        setRepositoryUrl(config.getString(GIT_REPO_URL));
        setBranch(config.getString(GIT_BRANCH));
    }

    public void onInitialBuild(BuildContext buildContext) {
    }

    public String retrieveSourceCode(String planKey, String vcsRevisionKey) throws RepositoryException {
        try {
            pullFromRepository(getSourceCodeDirectory(planKey), repositoryUrl);
        } catch (IOException e) {
            throw new RepositoryException("Failed to retrieveSourceCode", e);
        }
        return detectCommitsForUrl(repositoryUrl, vcsRevisionKey, new ArrayList<Commit>(), planKey);
    }

    public synchronized BuildChanges collectChangesSinceLastBuild(String planKey, String lastVcsRevisionKey) throws RepositoryException {
        try {
            String repositoryUrl = getRepositoryUrl();
            pullFromRepository(getSourceCodeDirectory(planKey), repositoryUrl);

            final List<Commit> commits = new ArrayList<Commit>();
            String lastRevisionChecked = detectCommitsForUrl(repositoryUrl, lastVcsRevisionKey, commits, planKey);
            return new BuildChangesImpl(String.valueOf(lastRevisionChecked), commits);
        } catch (IOException e) {
            throw new RepositoryException("Failed to collectChangesSinceLastBuild", e);
        }
    }

    private void validateMandatoryField(BuildConfiguration buildConfiguration, ErrorCollection errorCollection, String fieldKey, String errorMessage) {
        if (StringUtils.isEmpty(buildConfiguration.getString(fieldKey))) {
            errorCollection.addError(fieldKey, errorMessage);
        }
    }

    public boolean isRepositoryDifferent(Repository repository) {
        if (repository != null && repository instanceof GitRepository) {
            GitRepository gitRepository = (GitRepository) repository;
            return !new EqualsBuilder()
                    .append(getRepositoryUrl(), gitRepository.getRepositoryUrl())
                    .isEquals();
        }
        return true;
    }

    private String detectCommitsForUrl(String repositoryUrl, final String lastRevisionChecked, final List<Commit> commits, String planKey) {
        log.info("detectCommitsForUrl: /" + lastRevisionChecked + "/");
//        GitLog gitLog = new GitLog();
//        GitLogOptions opt = new GitLogOptions();
//        if (lastRevisionChecked != null)
//        {
//            opt.setOptLimitCommitAfter(true, lastRevisionChecked);
//        }
//        List<GitLogResponse.Commit> gitCommits = gitLog.log(getCheckoutDirectory(planKey), opt, Ref.createBranchRef("origin/master"));
//        if (gitCommits.size() > 1)
//        {
//            gitCommits.remove(gitCommits.size()-1);
//            log.error("commits found:"+gitCommits.size());
//            String startRevision = gitCommits.get(gitCommits.size() - 1).getDateString();
//            String latestRevisionOnServer = gitCommits.get(0).getDateString();
//            log.info("Collecting changes for '" + planKey + "' on path '" + repositoryUrl + "' from version " + startRevision + " to " + latestRevisionOnServer);
//
//            for (GitLogResponse.Commit logEntry : gitCommits)
//            {
//                CommitImpl commit = new CommitImpl();
//                String authorName = logEntry.getAuthor();
//
//                // it is possible to have commits with empty committer. BAM-2945
//                if (StringUtils.isBlank(authorName))
//                {
//                    log.info("Author name is empty for " + commit.toString());
//                    authorName = Author.UNKNOWN_AUTHOR;
//                }
//                commit.setAuthor(new AuthorImpl(authorName));
//                commit.setDate(new Date(logEntry.getDateString()));
//                commit.setComment(logEntry.getMessage());
//                List<CommitFile> files = new ArrayList();
//
//                if (logEntry.getFiles() != null) {
//                    for (GitLogResponse.CommitFile file : logEntry.getFiles())
//                    {
//                        CommitFileImpl commitFile = new CommitFileImpl();
//                        commitFile.setName(file.getName());
//                        commitFile.setRevision(logEntry.getSha());
//                        files.add(commitFile);
//                    }
//                }
//                commit.setFiles(files);
//
//                commits.add(commit);
//            }
//            return latestRevisionOnServer;
//        }
        log.info("******  returning last revision:" + lastRevisionChecked);
        return lastRevisionChecked;
    }

    private void pullFromRepository(File sourceDir, String repositoryUrl) throws IOException {
        Execute execute = new Execute(new PumpStreamHandler(System.out));
        execute.setWorkingDirectory(sourceDir);

        if (isWorkspaceEmpty(sourceDir)) {
            sourceDir.mkdirs();
            execute.setCommandline(new String[]{GIT_EXE, "init"});
            execute.execute();

            execute.setCommandline(new String[]{GIT_EXE, "remote", "add", "origin", repositoryUrl});
            execute.execute();
        }

        execute.setCommandline(new String[]{GIT_EXE, "pull", "origin", "master"});
        execute.execute();
    }
}