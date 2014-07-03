package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

/**
 * Determines whether the model run should execute.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeper {
    private static final Logger LOGGER = Logger.getLogger(ModelRunManager.class);
    private static final String NO_MODEL_RUN_MIN_NEW_OCCURRENCES =
            "No min new occurrences threshold has been defined for this disease group";
    private static final String NEVER_BEEN_EXECUTED_BEFORE =
            "Model run has never been executed before for this disease group";
    private static final String WEEK_HAS_NOT_ELAPSED = "A week has not elapsed since last model run preparation on %s";
    private static final String WEEK_HAS_ELAPSED = "At least a week has elapsed since last model run preparation on %s";
    private static final String ENOUGH_NEW_OCCURRENCES = "Number of new occurrences has exceeded minimum required";
    private static final String NOT_ENOUGH_NEW_OCCURRENCES = "Number of new occurrences has not exceeded minimum value";

    private DiseaseService diseaseService;

    ModelRunGatekeeper(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Determines whether model run preparation tasks should be carried out.
     * @param diseaseGroupId The id of the disease group for which the model run is being prepared.
     * @return True if there is no lastModelRunPrepDate for disease, or more than a week has passed since last run, or
     * there have been more new occurrences since the last run than the minimum required for the disease group.
     * False if the minimum number of new occurrences value is not specified for the disease group.
     */
    public boolean dueToRun(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup.getModelRunMinNewOccurrences() == null) {
            LOGGER.info(NO_MODEL_RUN_MIN_NEW_OCCURRENCES);
            return false;
        } else {
            return weekHasElapsed(diseaseGroup) || enoughNewOccurrences(diseaseGroup);
        }
    }

    private boolean weekHasElapsed(DiseaseGroup diseaseGroup) {
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        if (lastModelRunPrepDate == null) {
            LOGGER.info(NEVER_BEEN_EXECUTED_BEFORE);
            return true;
        } else {
            LocalDate today = LocalDate.now();
            LocalDate comparisonDate = lastModelRunPrepDate.toLocalDate().plusWeeks(1);
            final boolean weekHasElapsed = !comparisonDate.isAfter(today);
            LOGGER.info(String.format(weekHasElapsed ? WEEK_HAS_ELAPSED : WEEK_HAS_NOT_ELAPSED, lastModelRunPrepDate));
            return weekHasElapsed;
        }
    }

    private boolean enoughNewOccurrences(DiseaseGroup diseaseGroup) {
        long count = diseaseService.getNewOccurrencesCountByDiseaseGroup(diseaseGroup.getId());
        int min = diseaseGroup.getModelRunMinNewOccurrences();
        final boolean hasEnoughNewOccurrences = count > min;
        LOGGER.info(hasEnoughNewOccurrences ? ENOUGH_NEW_OCCURRENCES : NOT_ENOUGH_NEW_OCCURRENCES);
        return hasEnoughNewOccurrences;
    }
}
