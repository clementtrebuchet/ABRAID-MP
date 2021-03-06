package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.util.concurrent.Future;

/**
 * An interface to provide a means for triggering model runs where the workspace setup is done asynchronously.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunnerAsyncWrapper {
    /**
     * Asynchronously starts a new model run with the given configuration.
     * @param configuration The model run configuration.
     * @param modelStatusReporter The status reporter to call with the results of the model or if the setup fails.
     * @return The process handler for the launched process.
     */
    Future<ModelProcessHandler> startModel(RunConfiguration configuration, ModelStatusReporter modelStatusReporter);
}
