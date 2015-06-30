package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelOutputHandlerWebService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunnerAsyncWrapper;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelStatusReporter;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelStatusReporterImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Controller for the ModelWrapper model run triggers.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ModelRunController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(ModelRunController.class);
    private static final String LOG_QUEUING_NEW_BACKGROUND_MODEL_RUN =
            "Queuing new background model run for disease group %d (model run name %s)";
    private static final String LOG_EXCEPTION_STARTING_MODEL_RUN = "Exception starting model run.";
    private static final String LOG_EXCEPTION_READING_MODEL_RUN_DATA = "Exception reading model run data.";

    private final RunConfigurationFactory runConfigurationFactory;
    private final ModelRunnerAsyncWrapper modelRunnerAsyncWrapper;
    private final ModelOutputHandlerWebService modelOutputHandlerWebService;
    private final AbraidJsonObjectMapper objectMapper;

    @Autowired
    public ModelRunController(
            RunConfigurationFactory runConfigurationFactory,
            ModelRunnerAsyncWrapper modelRunnerAsyncWrapper,
            ModelOutputHandlerWebService modelOutputHandlerWebService,
            AbraidJsonObjectMapper objectMapper) {
        this.runConfigurationFactory = runConfigurationFactory;
        this.modelRunnerAsyncWrapper = modelRunnerAsyncWrapper;
        this.modelOutputHandlerWebService = modelOutputHandlerWebService;
        this.objectMapper = objectMapper;
    }

    /**
     * Triggers a new model run with the given occurrences.
     * @param data The run data to model (metadata & covariates).
     * @return 204 for success, 400 for invalid parameters or 500 if server cannot start model run.
     */
    @RequestMapping(value = "/api/model/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<JsonModelRunResponse> startRun(@RequestBody byte[] data) {
        if (data == null) {
            return createErrorResponse("Run data must be provided and be valid.", HttpStatus.BAD_REQUEST);
        }

        JsonModelRun runData = null;
        File tempDataDir = null;
        try {
            tempDataDir = saveRequestBodyToTemporaryFile(data);
            runData = readMetadata(tempDataDir);
        } catch (ZipException|JsonParseException|JsonMappingException e) {
            return createErrorResponse("Run data must be provided and be valid.", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            LOGGER.error(LOG_EXCEPTION_READING_MODEL_RUN_DATA, e);
            return createErrorResponse("Could not read model run data. See server logs for more details.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (runData == null || !runData.isValid() || !runData.getDisease().isValid()) {
            return createErrorResponse("Run data must be provided and be valid.", HttpStatus.BAD_REQUEST);
        }

        String runName;
        try {
            runName = submitModelRun(runData, tempDataDir);
        } catch (Exception e) {
            LOGGER.error(LOG_EXCEPTION_STARTING_MODEL_RUN, e);
            return createErrorResponse("Could not start model run. See server logs for more details.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return createSuccessResponse(runName);
    }

    private String submitModelRun(JsonModelRun runData, File tempDataDir) throws ConfigurationException, IOException {
        RunConfiguration runConfiguration = runConfigurationFactory.createDefaultConfiguration(
                tempDataDir,
                runData.getDisease().isGlobal(),
                runData.getDisease().getAbbreviation());

        ModelStatusReporter modelStatusReporter = new ModelStatusReporterImpl(
                runConfiguration.getRunName(),
                runConfiguration.getWorkingDirectoryPath(),
                runConfiguration.getExecutionConfig().getDryRunFlag(),
                modelOutputHandlerWebService,
                objectMapper);

        // Ignore result for now
        LOGGER.info(String.format(LOG_QUEUING_NEW_BACKGROUND_MODEL_RUN, runData.getDisease().getId(),
                runConfiguration.getRunName()));

        modelRunnerAsyncWrapper.startModel(
                runConfiguration, runData.getOccurrences(), runData.getExtentWeightings(), modelStatusReporter);

        return runConfiguration.getRunName();
    }

    private JsonModelRun readMetadata(File tempDir) throws IOException {
        File file = Paths.get(tempDir.toString(), "metadata.json").toFile();
        return objectMapper.readValue(file, JsonModelRun.class);
    }

    private File saveRequestBodyToTemporaryFile(byte[] modelRunZip) throws IOException, ZipException {
        File tempDataDir = null;
        File zipFile = null;
        try {
            zipFile = Files.createTempFile("run", ".zip").toFile();
            FileUtils.writeByteArrayToFile(zipFile, modelRunZip);
            ZipFile zip = new ZipFile(zipFile);
            tempDataDir = Files.createTempDirectory("run").toFile();
            zip.extractAll(tempDataDir.toString());
            return tempDataDir;
        } catch (Exception e) {
            if (tempDataDir != null && tempDataDir.exists()) {
                FileUtils.deleteDirectory(tempDataDir);
            }
            throw e;
        } finally {
            if (zipFile != null && zipFile.exists()) {
                Files.delete(zipFile.toPath());
            }
        }
    }

    private ResponseEntity<JsonModelRunResponse> createSuccessResponse(String modelRunName) {
        return new ResponseEntity<>(new JsonModelRunResponse(modelRunName, null), HttpStatus.OK);
    }

    private ResponseEntity<JsonModelRunResponse> createErrorResponse(String errorText, HttpStatus status) {
        return new ResponseEntity<>(new JsonModelRunResponse(null, errorText), status);
    }
}
