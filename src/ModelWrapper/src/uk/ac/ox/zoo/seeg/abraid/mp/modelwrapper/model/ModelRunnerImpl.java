package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Provides an entry point for model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunnerImpl implements ModelRunner {
    // The id/key to use for script file path substitution
    private static final String SCRIPT_FILE_ID = "script_file";

    // The arguments to pass to R
    private static final String[] R_OPTIONS = {"--no-save", "--slave", "-f", "${" + SCRIPT_FILE_ID + "}"};

    private ProcessRunnerFactory processRunnerFactory;
    private WorkspaceProvisioner workspaceProvisioner;

    public ModelRunnerImpl(ProcessRunnerFactory processRunnerFactory, WorkspaceProvisioner workspaceProvisioner) {
        this.processRunnerFactory = processRunnerFactory;
        this.workspaceProvisioner = workspaceProvisioner;
    }

    /**
     * Starts a new model run with the given configuration.
     * @param configuration The model run configuration.
     * @param occurrenceData The occurrence data to model with.
     * @return The process handler for the launched process.
     * @throws ProcessException Thrown in response to errors in the model.
     * @throws IOException Thrown if the workspace can not be correctly provisioned.
     */
    @Override
    public ModelProcessHandler runModel(RunConfiguration configuration,
                                        GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData)
            throws ProcessException, IOException {
        // Provision workspace
        File scriptFile = workspaceProvisioner.provisionWorkspace(configuration, occurrenceData);

        // Run model
        HashMap<String, File> fileArguments = new HashMap<>();
        fileArguments.put(SCRIPT_FILE_ID, scriptFile);
        ProcessRunner processRunner = processRunnerFactory.createProcessRunner(
                scriptFile.getParentFile(),
                configuration.getRPath(),
                R_OPTIONS,
                fileArguments,
                configuration.getMaxRuntime());

        ModelProcessHandler processHandler = new ModelProcessHandler();
        processRunner.run(processHandler);
        return processHandler;
    }
}