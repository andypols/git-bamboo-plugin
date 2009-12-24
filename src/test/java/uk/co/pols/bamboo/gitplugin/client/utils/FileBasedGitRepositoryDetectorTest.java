package uk.co.pols.bamboo.gitplugin.client.utils;

import junit.framework.TestCase;

import java.io.File;
import java.util.Random;

public class FileBasedGitRepositoryDetectorTest extends TestCase {
    private FileBasedGitRepositoryDetector detector = new FileBasedGitRepositoryDetector();
    private File repositoryDirectory = createTempDir();

    public void testARepositoryDirectoryMustExist() {
        assertFalse(detector.containsValidRepo(new File("unknown-repository")));
    }

    public void testARepositoryDirectoryMustContainAGitSubdirecory() {
        assertFalse(detector.containsValidRepo(repositoryDirectory));
    }

    public void testADirectoryWithAGitSubdirectoryIsGoldern() {
        File gitWorkingDirectory = new File(repositoryDirectory, ".git");
        gitWorkingDirectory.mkdirs();

        assertTrue(detector.containsValidRepo(repositoryDirectory));
    }

    public File createTempDir() {
        final String baseTempPath = System.getProperty("java.io.tmpdir");

        Random rand = new Random();
        int randomInt = 1 + rand.nextInt();

        File tempDir = new File(baseTempPath + File.separator + "tempDir" + randomInt);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        tempDir.deleteOnExit();

        return tempDir;
    }
}
