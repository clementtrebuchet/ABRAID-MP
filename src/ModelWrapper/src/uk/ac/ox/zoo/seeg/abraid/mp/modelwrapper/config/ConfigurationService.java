package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config;

import org.apache.commons.configuration.ConfigurationException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;

import java.io.IOException;

/**
 * Service interface for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public interface ConfigurationService {
    /**
     * Updates the current modelwrapper authentication details.
     * @param username The new username.
     * @param passwordHash The bcrypt hash of the new password.
     */
    void setAuthenticationDetails(String username, String passwordHash);

    /**
     * Gets the current modelwrapper authentication username.
     * @return The username.
     */
    String getAuthenticationUsername();

    /**
     * Gets the current modelwrapper authentication password hash.
     * @return The password hash.
     */
    String getAuthenticationPasswordHash();

    /**
     * Get the current remote repository url to use as a source for the model.
     * @return The repository url.
     */
    String getModelRepositoryUrl();

    /**
     * Set the current remote repository url to use as a source for the model.
     * @param repositoryUrl The repository url.
     */
    void setModelRepositoryUrl(String repositoryUrl);

    /**
     * Get the current model version to use to run the model.
     * @return The model version.
     */
    String getModelRepositoryVersion();

    /**
     * Set the current model version to use to run the model.
     * @param version The model version.
     */
    void setModelRepositoryVersion(String version);

    /**
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    String getCacheDirectory();

    /**
     * Gets the current path to the R executable binary.
     * @return The R path.
     * @throws ConfigurationException When a value for the R path is not set and R is not present in default locations.
     */
    String getRExecutablePath() throws ConfigurationException;

    /**
     * Sets the current path to the R executable binary.
     * @param path The R path.
     */
    void setRExecutablePath(String path);

    /**
     * Gets the current maximum model run duration.
     * @return The max duration.
     */
    int getMaxModelRunDuration();

    /**
     * Sets the current maximum model run duration.
     * @param value The max duration.
     */
    void setMaxModelRunDuration(int value);

    /**
     * Gets the path to the current global raster file.
     * @return The path to the global raster file.
     */
    String getGlobalRasterFile();

    /**
     * Gets the path to the current tropical raster file.
     * @return The path to the tropical raster file.
     */
    String getTropicalRasterFile();

    /**
     * Gets the path to the current admin 1 raster file.
     * @return The path to the admin 1 raster file.
     */
    String getAdmin1RasterFile();

    /**
     * Gets the path to the current admin 2 raster file.
     * @return The path to the admin 2 raster file.
     */
    String getAdmin2RasterFile();

    /**
     * Gets the current maximum number of CPUs for the model to use.
     * @return The maximum number of CPUs.
     */
    int getMaxCPUs();

    /**
     * Gets the current value of the model verbose flag.
     * @return The value of the model verbose flag.
     */
    boolean getModelVerboseFlag();

    /**
     * Gets the current value of the dry run flag.
     * @return The value of the dry run flag.
     */
    boolean getDryRunFlag();

    /**
     * Gets the current directory for covariate files.
     * @return The directory for covariate files.
     */
    String getCovariateDirectory();

    /**
     * Sets the current directory for covariate files.
     * @param path The directory for covariate files.
     */
    void setCovariateDirectory(String path);

    /**
     * Gets the current covariate configuration.
     * @return The covariate configuration.
     * @throws java.io.IOException thrown if the configuration json file cannot be parsed correctly.
     */
    JsonCovariateConfiguration getCovariateConfiguration() throws IOException;

    /**
     * Sets the current covariate configuration.
     * @param config The covariate configuration.
     * @throws java.io.IOException thrown if the configuration json file cannot be written correctly.
     */
    void setCovariateConfiguration(JsonCovariateConfiguration config) throws IOException;

    /**
     * Gets the root URL for the Model Output Handler web service.
     * @return The root URL for the Model Output Handler web service.
     */
    String getModelOutputHandlerRootUrl();
}