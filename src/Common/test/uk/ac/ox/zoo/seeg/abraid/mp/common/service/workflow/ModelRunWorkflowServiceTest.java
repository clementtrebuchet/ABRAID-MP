package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseProcessType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

/**
 * Tests the ModelRunWorkflowServiceImpl class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunWorkflowServiceTest {
    private WeightingsCalculator weightingsCalculator;
    private ModelRunRequester modelRunRequester;
    private DiseaseOccurrenceReviewManager reviewManager;
    private DiseaseService diseaseService;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private ModelRunWorkflowServiceImpl modelRunWorkflowService;
    private AutomaticModelRunsEnabler automaticModelRunsEnabler;
    private MachineWeightingPredictor machineWeightingPredictor;
    private ModelRunOccurrencesSelector modelRunOccurrencesSelector;

    @Before
    public void setUp() {
        weightingsCalculator = mock(WeightingsCalculator.class);
        modelRunRequester = mock(ModelRunRequester.class);
        reviewManager = mock(DiseaseOccurrenceReviewManager.class);
        diseaseService = mock(DiseaseService.class);
        diseaseExtentGenerator = mock(DiseaseExtentGenerator.class);
        automaticModelRunsEnabler = mock(AutomaticModelRunsEnabler.class);
        machineWeightingPredictor = mock(MachineWeightingPredictor.class);
        modelRunOccurrencesSelector = mock(ModelRunOccurrencesSelector.class);
        modelRunWorkflowService = new ModelRunWorkflowServiceImpl(weightingsCalculator, modelRunRequester,
                reviewManager, diseaseService, modelRunOccurrencesSelector, diseaseExtentGenerator,
                automaticModelRunsEnabler, machineWeightingPredictor);
    }

    @Test
    public void prepareForAndRequestModelRunForAutomaticProcess() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        List<DiseaseOccurrence> occurrencesForTrainingPredictor = new ArrayList<>();

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(modelRunOccurrencesSelector.selectOccurrencesForModelRun(diseaseGroupId, false)).thenReturn(occurrences);
        when(diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId)).thenReturn(
                occurrencesForTrainingPredictor);

        // Act
        modelRunWorkflowService.prepareForAndRequestModelRun(diseaseGroupId, DiseaseProcessType.AUTOMATIC, null, null);

        // Assert
        //// No prep
        verify(weightingsCalculator, never()).updateDiseaseOccurrenceExpertWeightings(anyInt());
        verify(weightingsCalculator, never()).updateExpertsWeightings();
        verify(reviewManager, never()).updateDiseaseOccurrenceStatus(anyInt(), anyBoolean());
        verify(diseaseExtentGenerator, never()).generateDiseaseExtent(any(DiseaseGroup.class), any(DateTime.class), anyBoolean());
        verify(machineWeightingPredictor, never()).train(anyInt(), anyListOf(DiseaseOccurrence.class));
        //// Request run
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), isNull(DateTime.class), isNull(DateTime.class));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
    }

    @Test
    public void prepareForAndRequestModelRunForManualProcess() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        DateTime batchStartDate = new DateTime("2012-11-13T00:00:00.000");
        DateTime batchEndDate = new DateTime("2012-11-14T23:59:59.999");
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        List<DiseaseOccurrence> occurrencesForTrainingPredictor = new ArrayList<>();

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(modelRunOccurrencesSelector.selectOccurrencesForModelRun(diseaseGroupId, false)).thenReturn(occurrences);
        when(diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId)).thenReturn(
                occurrencesForTrainingPredictor);

        // Act
        modelRunWorkflowService.prepareForAndRequestModelRun(diseaseGroupId, DiseaseProcessType.MANUAL, batchStartDate, batchEndDate);

        // Assert
        // Prep
        verify(weightingsCalculator).updateDiseaseOccurrenceExpertWeightings(eq(diseaseGroupId));
        verify(weightingsCalculator).updateExpertsWeightings();
        verify(reviewManager).updateDiseaseOccurrenceStatus(eq(diseaseGroupId), eq(false));
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), isNull(DateTime.class), eq(false));
        verify(machineWeightingPredictor).train(eq(diseaseGroupId), same(occurrencesForTrainingPredictor));
        //// Request run
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), eq(batchStartDate), eq(batchEndDate));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
    }

    @Test
    public void prepareForAndRequestModelRunForGoldStandard() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        List<DiseaseOccurrence> occurrencesForTrainingPredictor = new ArrayList<>();

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(modelRunOccurrencesSelector.selectOccurrencesForModelRun(diseaseGroupId, true)).thenReturn(occurrences);
        when(diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId)).thenReturn(
                occurrencesForTrainingPredictor);

        // Act
        modelRunWorkflowService.prepareForAndRequestModelRun(diseaseGroupId, DiseaseProcessType.MANUAL_GOLD_STANDARD, null, null);

        // Assert
        // Prep
        verify(weightingsCalculator).updateDiseaseOccurrenceExpertWeightings(eq(diseaseGroupId));
        verify(weightingsCalculator).updateExpertsWeightings();
        verify(reviewManager).updateDiseaseOccurrenceStatus(eq(diseaseGroupId), eq(false));
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), isNull(DateTime.class), eq(true));
        verify(machineWeightingPredictor).train(eq(diseaseGroupId), same(occurrencesForTrainingPredictor));
        //// Request run
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), isNull(DateTime.class), isNull(DateTime.class));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
    }

    @Test
    public void enableAutomaticModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        modelRunWorkflowService.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(automaticModelRunsEnabler).enable(eq(diseaseGroupId));
    }

    @Test
    public void generateDiseaseExtentForManualProcess() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroupId, DiseaseProcessType.MANUAL);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), eq((DateTime) null), eq(false));
        verify(modelRunOccurrencesSelector, never()).selectOccurrencesForModelRun(anyInt(), anyBoolean());

    }

    @Test
    public void generateDiseaseExtentForAutomaticProcess() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithMultipleDates(minimumOccurrenceDate.plus(1), minimumOccurrenceDate, minimumOccurrenceDate.plusWeeks(1));
        when(modelRunOccurrencesSelector.selectOccurrencesForModelRun(diseaseGroupId, false)).thenReturn(occurrences);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroupId, DiseaseProcessType.AUTOMATIC);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate), eq(false));
    }

    @Test
    public void generateDiseaseExtentForGoldStandard() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroupId, DiseaseProcessType.MANUAL_GOLD_STANDARD);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), eq((DateTime) null), eq(true));
    }

    private List<DiseaseOccurrence> createListWithDate(DateTime minimumOccurrenceDate) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setOccurrenceDate(minimumOccurrenceDate);
        return Arrays.asList(occurrence);
    }

    private List<DiseaseOccurrence> createListWithMultipleDates(DateTime...  dates) {
        List<DiseaseOccurrence> diseaseOccurrences = new ArrayList<>();
        for (DateTime date : dates) {
            DiseaseOccurrence occurrence = new DiseaseOccurrence();
            occurrence.setOccurrenceDate(date);
            diseaseOccurrences.add(occurrence);
        }
        return diseaseOccurrences;
    }
}
