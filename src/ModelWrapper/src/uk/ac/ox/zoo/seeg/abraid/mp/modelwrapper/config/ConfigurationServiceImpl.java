package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service class for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationServiceImpl.class);
    private static final String LOG_LOADING_CONFIGURATION_FILE =
            "Loading configuration file %s";
    private static final String LOG_UPDATING_AUTH_CONFIGURATION =
            "Updating auth configuration: %s %s";
    private static final String LOG_UPDATING_REPOSITORY_URL_CONFIGURATION =
            "Updating repository url configuration: %s";
    private static final String LOG_UPDATING_VERSION_CONFIGURATION =
            "Updating repository version configuration: %s";
    private static final String LOG_UPDATING_R_PATH =
            "Updating R path configuration: %s";
    private static final String LOG_UPDATING_RUN_DURATION =
            "Updating max run duration configuration: %s";
    private static final String LOG_UPDATING_COVARIATE_DIR =
            "Updating covariate dir configuration: %s";
    private static final String LOG_ADDING_FILES_TO_COVARIATE_CONFIG =
            "Adding %s files to the covariate config.";
    private static final String LOG_COVARIATE_DIR_CREATION_FAIL =
            "Cannot store covariate config. The directory does not exist and could not be created. At: %s";
    private static final String LOG_OLD_COVARIATE_CONFIG_REMOVAL_FAIL =
            "Removing old covariate config failed. At: %s";
    private static final String LOG_WRITING_COVARIATE_CONFIG_FAIL =
            "Writing new covariate config failed. At: %s";
    private static final String LOG_USING_DEFAULT_COVARIATE_CONFIG =
            "Custom covariate config file does not yet exist. Using default file: %s.";
    private static final String LOG_INVALID_COVARIATE_CONFIG =
            "Covariate config file on disk is not valid.";
    private static final String LOG_COULD_NOT_READ_COVARIATE_CONFIG =
            "Failed to read and parse covariate config file from disk.";

    private static final String DEFAULT_LINUX_CACHE_DIR = "/var/lib/abraid/modelwrapper";
    private static final String DEFAULT_WINDOWS_CACHE_DIR = System.getenv("LOCALAPPDATA") + "\\abraid\\modelwrapper";
    private static final String DEFAULT_COVARIATE_SUB_DIR = "covariates";
    private static final String DEFAULT_LINUX_R_PATH = "/usr/bin/R";
    private static final String DEFAULT_WINDOWS_R_PATH = System.getenv("R_HOME") + "\\bin\\x64\\R.exe";
    private static final String DEFAULT_RASTER_SUBDIR = "rasters";
    private static final String DEFAULT_ADMIN0_RASTER_NAME = "admin0qc.tif";
    private static final String DEFAULT_ADMIN1_RASTER_NAME = "admin1qc.tif";
    private static final String DEFAULT_ADMIN2_RASTER_NAME = "admin2qc.tif";
    private static final String DEFAULT_TROPICAL_RASTER_NAME = "admin_tropical.tif";
    private static final String DEFAULT_GLOBAL_RASTER_NAME = "admin_global.tif";
    private static final int DEFAULT_MAX_CPU = 64;
    private static final boolean DEFAULT_DRY_RUN_FLAG = false;
    private static final boolean DEFAULT_MODEL_VERBOSE_FLAG = false;

    private static final String USERNAME_KEY = "auth.username";
    private static final String PASSWORD_KEY = "auth.password_hash";
    private static final String CACHE_DIR_KEY = "cache.data.dir";
    private static final String MODEL_REPOSITORY_KEY = "model.repo.url";
    private static final String MODEL_VERSION_KEY = "model.repo.version";
    private static final String R_EXECUTABLE_KEY = "r.executable.path";
    private static final String R_MAX_DURATION_KEY = "r.max.duration";
    private static final String GLOBAL_RASTER_KEY = "raster.file.global";
    private static final String TROPICAL_RASTER_KEY = "raster.file.tropical";
    private static final String ADMIN0_RASTER_KEY = "raster.file.admin0";
    private static final String ADMIN1_RASTER_KEY = "raster.file.admin1";
    private static final String ADMIN2_RASTER_KEY = "raster.file.admin2";
    private static final String COVARIATE_DIRECTORY_KEY = "covariate.dir";
    private static final String MAX_CPU_KEY = "model.max.cpu";
    private static final String DRY_RUN_FLAG_KEY = "model.dry.run";
    private static final String MODEL_VERBOSE_FLAG_KEY = "model.verbose";
    private static final String MODEL_OUTPUT_HANDLER_ROOT_URL_KEY = "model.output.handler.root.url";

    private static final String COVARIATE_JSON_FILE = "abraid.json";

    private final FileConfiguration basicProperties;
    private final File defaultCovariateConfig;
    private final OSChecker osChecker;

    public ConfigurationServiceImpl(File basicProperties, File defaultCovariateConfig, OSChecker osChecker)
            throws ConfigurationException {
        LOGGER.info(String.format(LOG_LOADING_CONFIGURATION_FILE, basicProperties.toString()));
        this.basicProperties = new PropertiesConfiguration(basicProperties);
        this.basicProperties.setAutoSave(true);
        this.defaultCovariateConfig = defaultCovariateConfig;
        this.osChecker = osChecker;
    }

    /**
     * Updates the current modelwrapper authentication details.
     * @param username The new username.
     * @param passwordHash The bcrypt hash of the new password.
     */
    @Override
    public void setAuthenticationDetails(String username, String passwordHash) {
        LOGGER.info(String.format(LOG_UPDATING_AUTH_CONFIGURATION, username, passwordHash));
        basicProperties.setProperty(USERNAME_KEY, username);
        basicProperties.setProperty(PASSWORD_KEY, passwordHash);
    }

    /**
     * Gets the current modelwrapper authentication username.
     * @return The username
     */
    @Override
    public String getAuthenticationUsername() {
        return basicProperties.getString(USERNAME_KEY);
    }

    /**
     * Gets the current modelwrapper authentication password hash.
     * @return The password hash.
     */
    @Override
    public String getAuthenticationPasswordHash() {
        return basicProperties.getString(PASSWORD_KEY);
    }

    /**
     * Get the current remote repository url to use as a source for the model.
     * @return The repository url.
     */
    @Override
    public String getModelRepositoryUrl() {
        return basicProperties.getString(MODEL_REPOSITORY_KEY);
    }

    /**
     * Set the current remote repository url to use as a source for the model.
     * @param repositoryUrl The repository url.
     */
    @Override
    public void setModelRepositoryUrl(String repositoryUrl) {
        LOGGER.info(String.format(LOG_UPDATING_REPOSITORY_URL_CONFIGURATION, repositoryUrl));
        basicProperties.setProperty(MODEL_REPOSITORY_KEY, repositoryUrl);
    }

    /**
     * Get the current model version to use to run the model.
     * @return The model version.
     */
    @Override
    public String getModelRepositoryVersion() {
        return basicProperties.getString(MODEL_VERSION_KEY);
    }

    /**
     * Set the current model version to use to run the model.
     * @param version The model version.
     */
    @Override
    public void setModelRepositoryVersion(String version) {
        LOGGER.info(String.format(LOG_UPDATING_VERSION_CONFIGURATION, version));
        basicProperties.setProperty(MODEL_VERSION_KEY, version);
    }

    /**
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    @Override
    public String getCacheDirectory() {
        String defaultDir = osChecker.isWindows() ? DEFAULT_WINDOWS_CACHE_DIR : DEFAULT_LINUX_CACHE_DIR;
        return basicProperties.getString(CACHE_DIR_KEY, defaultDir);
    }

    /**
     * Gets the current path to the R executable binary.
     * @return The R path.
     * @throws ConfigurationException When a value for the R path is not set and R is not present in default locations.
     */
    @Override
    public String getRExecutablePath() throws ConfigurationException {
        if (basicProperties.containsKey(R_EXECUTABLE_KEY)) {
            return basicProperties.getString(R_EXECUTABLE_KEY);
        } else {
            return findDefaultR();
        }
    }

    /**
     * Sets the current path to the R executable binary.
     * @param path The R path.
     */
    @Override
    public void setRExecutablePath(String path) {
        LOGGER.info(String.format(LOG_UPDATING_R_PATH, path));
        basicProperties.setProperty(R_EXECUTABLE_KEY, path);
    }

    /**
     * Gets the current maximum model run duration.
     * @return The max duration.
     */
    @Override
    public int getMaxModelRunDuration() {
        return basicProperties.getInt(R_MAX_DURATION_KEY, Integer.MAX_VALUE);
    }

    /**
     * Sets the current maximum model run duration.
     * @param value The max duration.
     */
    @Override
    public void setMaxModelRunDuration(int value) {
        LOGGER.info(String.format(LOG_UPDATING_RUN_DURATION, value));
        basicProperties.setProperty(R_MAX_DURATION_KEY, value);
    }

    /**
     * Gets the path to the current global raster file.
     * @return The path to the global raster file.
     */
    @Override
    public String getGlobalRasterFile() {
        return getRasterFile(GLOBAL_RASTER_KEY, DEFAULT_GLOBAL_RASTER_NAME);
    }

    /**
     * Gets the path to the current tropical raster file.
     * @return The path to the tropical raster file.
     */
    @Override
    public String getTropicalRasterFile() {
        return getRasterFile(TROPICAL_RASTER_KEY, DEFAULT_TROPICAL_RASTER_NAME);
    }

    /**
     * Gets the path to the current admin 0 raster file.
     * @return The path to the admin 0 raster file.
     */
    @Override
    public String getAdmin0RasterFile() {
        return getRasterFile(ADMIN0_RASTER_KEY, DEFAULT_ADMIN0_RASTER_NAME);
    }

    /**
     * Gets the path to the current admin 1 raster file.
     * @return The path to the admin 1 raster file.
     */
    @Override
    public String getAdmin1RasterFile() {
        return getRasterFile(ADMIN1_RASTER_KEY, DEFAULT_ADMIN1_RASTER_NAME);
    }

    /**
     * Gets the path to the current admin 2 raster file.
     * @return The path to the admin 2 raster file.
     */
    @Override
    public String getAdmin2RasterFile() {
        return getRasterFile(ADMIN2_RASTER_KEY, DEFAULT_ADMIN2_RASTER_NAME);
    }

    private String getRasterFile(String propertyKey, String defaultName) {
        return getFile(propertyKey, DEFAULT_RASTER_SUBDIR, defaultName);
    }

    private String getFile(String propertyKey, String defaultSubdir, String defaultName) {
        if (basicProperties.containsKey(propertyKey)) {
            return basicProperties.getString(propertyKey);
        }
        Path filePath = Paths.get(getCacheDirectory(), defaultSubdir, defaultName);
        return filePath.toString();
    }

    /**
     * Gets the current maximum number of CPUs for the model to use.
     * @return The maximum number of CPUs.
     */
    @Override
    public int getMaxCPUs() {
        return basicProperties.getInt(MAX_CPU_KEY, DEFAULT_MAX_CPU);
    }

    /**
     * Gets the current value of the model verbose run flag.
     * @return The value of the model verbose run flag.
     */
    @Override
    public boolean getModelVerboseFlag() {
        return basicProperties.getBoolean(MODEL_VERBOSE_FLAG_KEY, DEFAULT_MODEL_VERBOSE_FLAG);
    }

    /**
     * Gets the current value of the dry run flag.
     * @return The value of the dry run flag.
     */
    @Override
    public boolean getDryRunFlag() {
        return basicProperties.getBoolean(DRY_RUN_FLAG_KEY, DEFAULT_DRY_RUN_FLAG);
    }

    /**
     * Gets the current directory for covariate files.
     * @return The directory for covariate files.
     */
    @Override
    public String getCovariateDirectory() {
        Path defaultDirPath = Paths.get(getCacheDirectory(), DEFAULT_COVARIATE_SUB_DIR);
        String defaultDir = defaultDirPath.toFile().getAbsolutePath();
        return basicProperties.getString(COVARIATE_DIRECTORY_KEY, defaultDir);
    }

    /**
     * Sets the current directory for covariate files.
     * @param path The directory for covariate files.
     */
    @Override
    public void setCovariateDirectory(String path) {
        LOGGER.info(String.format(LOG_UPDATING_COVARIATE_DIR, path));
        basicProperties.setProperty(COVARIATE_DIRECTORY_KEY, path);
    }

    /**
     * Gets the current covariate configuration.
     * @return The covariate configuration.
     * @throws java.io.IOException thrown if the configuration json file cannot be parsed correctly.
     */
    @Override
    public JsonCovariateConfiguration getCovariateConfiguration() throws IOException {
        return null;
    }

    /**
     * Sets the current covariate configuration.
     * @param config The covariate configuration.
     * @throws java.io.IOException thrown if the configuration json file cannot be written correctly.
     */
    @Override
    public void setCovariateConfiguration(JsonCovariateConfiguration config) throws IOException {
    }

    @Override
    public String getModelOutputHandlerRootUrl() {
        return basicProperties.getString(MODEL_OUTPUT_HANDLER_ROOT_URL_KEY);
    }

    private String findDefaultR() throws ConfigurationException {
        String rPath = osChecker.isWindows() ? DEFAULT_WINDOWS_R_PATH : DEFAULT_LINUX_R_PATH;
        File r = Paths.get(rPath).toFile();
        if (r.exists() && r.canExecute()) {
            return r.getAbsolutePath();
        } else {
            throw new ConfigurationException("Could not find R.");
        }
    }
}

