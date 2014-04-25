package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.QCManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapDataConverter class.
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataConverterTest {
    private AlertService alertService;
    private DiseaseService diseaseService;
    private HealthMapLocationConverter locationConverter;
    private HealthMapAlertConverter alertConverter;
    @SuppressWarnings("FieldCanBeLocal")
    private HealthMapLookupData healthMapLookupData;
    private HealthMapDataConverter healthMapDataConverter;
    private QCManager qcManager;

    private Provenance healthMapProvenance;

    @Before
    public void setUp() {
        alertService = mock(AlertService.class);
        diseaseService = mock(DiseaseService.class);
        locationConverter = mock(HealthMapLocationConverter.class);
        alertConverter = mock(HealthMapAlertConverter.class);
        healthMapLookupData = mock(HealthMapLookupData.class);
        qcManager = mock(QCManager.class);
        healthMapDataConverter = new HealthMapDataConverter(locationConverter, alertConverter,
                alertService, diseaseService, healthMapLookupData, qcManager);

        healthMapProvenance = new Provenance();
        when(healthMapLookupData.getHealthMapProvenance()).thenReturn(healthMapProvenance);
    }

    @Test
    public void convertNoLocations() {
        // Arrange
        List<HealthMapLocation> locations = new ArrayList<>();
        DateTime retrievalEndDate = DateTime.now();

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    @Test
    public void convertTwoSuccessfulLocationsEachWithTwoSuccessfulAlerts() {
        // Arrange
        // Create 2 locations each with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        final Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);
        mockAddPrecision(healthMapLocation1, location1);

        // healthMapLocation2 is successfully converted into location2
        final Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);
        mockAddPrecision(healthMapLocation2, location2);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);

        // healthMapAlert2 is successfully converted into diseaseOccurrence2
        DiseaseOccurrence diseaseOccurrence2 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert2, location1)).thenReturn(diseaseOccurrence2);

        // healthMapAlert3 is successfully converted into diseaseOccurrence3
        DiseaseOccurrence diseaseOccurrence3 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert3, location2)).thenReturn(diseaseOccurrence3);

        // healthMapAlert4 is successfully converted into diseaseOccurrence4
        DiseaseOccurrence diseaseOccurrence4 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert4, location2)).thenReturn(diseaseOccurrence4);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verify(diseaseService, times(4)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence1));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence2));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence3));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence4));
    }

    @Test
    public void convertFirstLocationFails() {
        // Arrange
        // Create 2 locations each with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is not successfully converted into location1
        when(locationConverter.convert(healthMapLocation1)).thenReturn(null);

        // healthMapLocation2 is successfully converted into location2
        final Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);
        mockAddPrecision(healthMapLocation2, location2);

        // healthMapAlert3 is successfully converted into diseaseOccurrence3
        DiseaseOccurrence diseaseOccurrence3 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert3, location2)).thenReturn(diseaseOccurrence3);

        // healthMapAlert4 is successfully converted into diseaseOccurrence4
        DiseaseOccurrence diseaseOccurrence4 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert4, location2)).thenReturn(diseaseOccurrence4);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verify(diseaseService, times(2)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence3));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence4));
    }

    @Test
    public void convertFirstLocationSucceedsSecondLocationFails() {
        // Arrange
        // Create 2 locations each with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        final Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);
        mockAddPrecision(healthMapLocation1, location1);

        // healthMapLocation2 is not successfully converted into location2
        when(locationConverter.convert(healthMapLocation2)).thenReturn(null);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);

        // healthMapAlert2 is successfully converted into diseaseOccurrence2
        DiseaseOccurrence diseaseOccurrence2 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert2, location1)).thenReturn(diseaseOccurrence2);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verify(diseaseService, times(2)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence1));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence2));
    }

    @Test
    public void convertNoAlerts() {
        // Arrange
        // Create 2 locations each with no alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        final Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapLocation2 is successfully converted into location2
        final Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
    }

    @Test
    public void convertFirstAlertFailsSecondAlertSucceeds() {
        // Arrange
        // Create a location with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        final Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);
        mockAddPrecision(healthMapLocation1, location1);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);

        // healthMapAlert2 is not successfully converted into diseaseOccurrence2
        when(alertConverter.convert(healthMapAlert2, location1)).thenReturn(null);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verify(diseaseService, times(1)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence1));
    }

    @Test
    public void addingLocationPrecisionFails() {
        // Arrange
        // Create a location with 1 alert
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1, but continuation fails (i.e. we do not call
        // mockAddPrecision)
        final Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
    }

    @Test
    public void qcStage1Fails() {
        // Arrange
        // Create a location with 1 alert
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1);
        int passedQCStage = 0;

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1, but it fails at QC stage 1
        final Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);
        mockAddPrecision(healthMapLocation1, location1);
        when(qcManager.performQC(location1)).thenReturn(passedQCStage);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertThat(location1.getPassedQCStage()).isEqualTo(passedQCStage);
        verify(diseaseService, times(1)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence1));
    }

    @Test
    public void qcStage1FirstLocationPassesSecondLocationFails() {
        // Arrange
        // Create 2 locations each with 1 alert
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert2));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1, and passes QC stage 1
        final Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);
        mockAddPrecision(healthMapLocation1, location1);
        when(qcManager.performQC(location1)).thenReturn(1);

        // healthMapLocation2 is successfully converted into location2, but fails QC stage 1
        final Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);
        mockAddPrecision(healthMapLocation2, location2);
        when(qcManager.performQC(location2)).thenReturn(0);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);

        // healthMapAlert2 is successfully converted into diseaseOccurrence2
        DiseaseOccurrence diseaseOccurrence2 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert2, location2)).thenReturn(diseaseOccurrence2);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertThat(location1.getPassedQCStage()).isEqualTo(1);
        assertThat(location2.getPassedQCStage()).isEqualTo(0);
        verify(diseaseService, times(2)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence1));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence2));
    }

    @Test
    public void convertLocationContinuationSucceedsBecauseLocationAlreadyExists() {
        // Arrange
        // Create 1 location with 1 alert
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1, which already exists (so no need to call
        // mockAddPrecision)
        final Location location1 = new Location(1);
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verify(diseaseService, times(1)).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(diseaseService).saveDiseaseOccurrence(same(diseaseOccurrence1));
    }


    private void verifyWriteLastRetrievalEndDate(DateTime retrievalEndDate) {
        assertThat(healthMapProvenance.getLastRetrievalEndDate()).isEqualTo(retrievalEndDate);
        verify(alertService, times(1)).saveProvenance(same(healthMapProvenance));
    }

    private void mockAddPrecision(HealthMapLocation healthMapLocation1, final Location location1) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                location1.setPrecision(LocationPrecision.COUNTRY);
                return null;
            }
        }).when(locationConverter).addPrecision(healthMapLocation1, location1);
    }
}