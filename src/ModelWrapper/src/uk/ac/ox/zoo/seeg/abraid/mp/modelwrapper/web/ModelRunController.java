package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

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
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;

/**
 * Controller for the ModelWrapper model run triggers.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ModelRunController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(ModelRunController.class);
    private static final String LOG_STARTING_NEW_BACKGROUND_MODEL_RUN = "Starting new background model run";
    private static final String LOG_EXCEPTION_STARTING_MODEL_RUN = "Exception starting model run.";

    private final RunConfigurationFactory runConfigurationFactory;
    private final ModelRunner modelRunner;

    @Autowired
    public ModelRunController(RunConfigurationFactory runConfigurationFactory, ModelRunner modelRunner) {
        this.runConfigurationFactory = runConfigurationFactory;
        this.modelRunner = modelRunner;
    }

    /**
     * Triggers a new model run with the given occurrences.
     * @param runData The run data to model.
     * @return 204 for success, 400 for invalid parameters or 500 if server cannot start model run.
     */
    @RequestMapping(value = "/model/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> startRun(@RequestBody JsonModelRun runData) {
        if (runData == null || !runData.isValid() || !runData.getDisease().isValid()) {
            return new ResponseEntity<String>("Run data must be provided and be valid", HttpStatus.BAD_REQUEST);
        }

        try {
            LOGGER.info(LOG_STARTING_NEW_BACKGROUND_MODEL_RUN);

            RunConfiguration runConfiguration = runConfigurationFactory.createDefaultConfiguration(
                    runData.getDisease().getId(),
                    runData.getDisease().isGlobal(),
                    runData.getDisease().getName(),
                    runData.getDisease().getAbbreviation());

            // Ignore result for now
            modelRunner.runModel(runConfiguration, runData.getOccurrences(), runData.getExtents());
        } catch (Exception e) {
            LOGGER.error(LOG_EXCEPTION_STARTING_MODEL_RUN, e);
            return new ResponseEntity<String>("Could not start model run.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
}
