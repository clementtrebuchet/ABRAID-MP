package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.util.DigestUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Provides interaction with a GIT VCS repository containing the model code.
 * Copyright (c) 2014 University of Oxford
 */
public class GitSourceCodeManager implements SourceCodeManager {
    private static final String GIT_CONFIG_DIRECTORY = ".git";
    private static final String MASTER_BRANCH_NAME = "master";
    private static final int FILE_NAME_MAX_URL_LENGTH = 200;
    private static final String FILE_NAME_INVALID_CHARS = "\\W+";
    private static final String TAG_PREFIX = "refs/tags/";
    private static final String REPOSITORY_CACHE_SUBDIRECTORY = "repos";

    private ConfigurationService configurationService;

    public GitSourceCodeManager(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Copies a specific version of the source code into the workspace.
     * @param versionIdentifier The source code version to provision.
     * @param workspace The directory into which the source code should be provisioned.
     * @throws IllegalArgumentException Thrown if the specified version is not found in the repository.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    @Override
    public void provisionVersion(String versionIdentifier, File workspace)
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        synchronized (GitSourceCodeManager.class) {
            try {
                List<String> tags = getAvailableVersions();
                if (!tags.contains(versionIdentifier)) {
                    throw new IllegalArgumentException("No such version");
                }
                Git repository = openRepository();
                repository.checkout().setName(versionIdentifier).call();

                FileUtils.copyDirectory(
                        getRepositoryDirectory().toFile(),
                        workspace,
                        new NotFileFilter(new AndFileFilter(
                                new NameFileFilter(GIT_CONFIG_DIRECTORY),
                                DirectoryFileFilter.DIRECTORY)));

                repository.checkout().setName(MASTER_BRANCH_NAME).call();
            } catch (GitAPIException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    /**
     * Synchronise the local repository cache with the remote repository url.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    @Override
    public void updateRepository() throws IOException, UnsupportedOperationException {
        synchronized (GitSourceCodeManager.class) {
            try {
                if (!Files.exists(getRepositoryDirectory())) {
                    cloneRepository();
                } else {
                    openRepository().pull().call();
                }
            } catch (GitAPIException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    /**
     * Lists the package versions found in the source code repository.
     * @return The versions list.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    @Override
    public List<String> getAvailableVersions() throws IOException, UnsupportedOperationException {
        synchronized (GitSourceCodeManager.class) {
            try {
                List<String> versions = convert(openRepository().tagList().call(), new Converter<Ref, String>() {
                    public String convert(Ref ref) {
                        return ref.getName().substring(TAG_PREFIX.length());
                    }
                });

                Collections.reverse(versions);

                return versions;
            } catch (GitAPIException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    /**
     * Creates a unique, valid, folder name from the repository url, which will be used to store the cloned repository.
     * @return The folder path.
     */
    private Path getRepositoryDirectory() {
        String repositoryUrl = configurationService.getModelRepositoryUrl();
        String basePath = Paths.get(configurationService.getCacheDirectory(), REPOSITORY_CACHE_SUBDIRECTORY).toString();

        String hash = DigestUtils.md5DigestAsHex(repositoryUrl.getBytes(Charsets.US_ASCII));

        if (repositoryUrl.length() > FILE_NAME_MAX_URL_LENGTH) {
            repositoryUrl = repositoryUrl.substring(0, FILE_NAME_MAX_URL_LENGTH);
        }

        String folderName = String.format("%s - %s", repositoryUrl, hash).replaceAll(FILE_NAME_INVALID_CHARS, "_");
        return Paths.get(basePath, folderName);
    }

    /**
     * Opens the current repository and ensure master is the working branch.
     * @return The Git facade to interact with the repository.
     * @throws IOException
     * @throws GitAPIException
     */
    private Git openRepository() throws IOException, GitAPIException {
        Path repositoryDirectory = getRepositoryDirectory();
        Repository repository = (new FileRepositoryBuilder())
                .setGitDir(Paths.get(repositoryDirectory.toString(), GIT_CONFIG_DIRECTORY).toFile())
                .readEnvironment()
                .findGitDir()
                .build();
        Git git = new Git(repository);
        git.checkout().setName(MASTER_BRANCH_NAME).call();
        return git;
    }

    /**
     * Clones a new copy of the git repository.
     * @throws IOException
     * @throws GitAPIException
     */
    private void cloneRepository() throws IOException, GitAPIException {
        String repositoryUrl = configurationService.getModelRepositoryUrl();
        Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(getRepositoryDirectory().toFile())
                .call();
    }
}
