package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ValidationParameterCacheService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the DistanceFromDiseaseExtentHelper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DistanceFromDiseaseExtentHelperTest {
    private NativeSQL nativeSQL;
    private DistanceFromDiseaseExtentHelper helper;
    private LocationService locationService;
    private ValidationParameterCacheService cacheService;

    @Before
    public void setUp() {
        nativeSQL = mock(NativeSQL.class);
        locationService = mock(LocationService.class);
        cacheService = mock(ValidationParameterCacheService.class);
        when(cacheService.getDistanceToExtentFromCache(anyInt(), anyInt())).thenReturn(null);
        helper = new DistanceFromDiseaseExtentHelper(nativeSQL, locationService, cacheService);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenLocationIsOutsideTheExtentReturnsDistanceOutside() {
        // Arrange
        int diseaseGroupId = 87;
        int locationId = 123;
        double expectedDistance = 25;
        boolean isGlobal = false;
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location, DiseaseExtentClass.UNCERTAIN, DiseaseExtentClass.POSSIBLE_ABSENCE, DiseaseExtentClass.ABSENCE);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(expectedDistance);
        when(nativeSQL.findDistanceInsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(321d);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
        verify(cacheService).saveDistanceToExtentCacheEntry(diseaseGroupId, locationId, actualDistance);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenLocationIsWithinTheExtentReturnsDistanceWithin() {
        // Arrange
        int diseaseGroupId = 87;
        int locationId = 123;
        double expectedDistance = 25;
        boolean isGlobal = false;
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location, DiseaseExtentClass.POSSIBLE_PRESENCE, DiseaseExtentClass.PRESENCE);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(123d);
        when(nativeSQL.findDistanceInsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(expectedDistance);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isEqualTo(-expectedDistance);
        verify(cacheService).saveDistanceToExtentCacheEntry(diseaseGroupId, locationId, actualDistance);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenCalculationOutsideReturnsNull() {
        int diseaseGroupId = 87;
        int locationId = 123;
        double expectedDistance = 25;
        boolean isGlobal = false;
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location, DiseaseExtentClass.UNCERTAIN, DiseaseExtentClass.POSSIBLE_ABSENCE, DiseaseExtentClass.ABSENCE);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(null);
        when(nativeSQL.findDistanceInsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(321d);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isNull();
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenCalculationInsideReturnsNull() {
        int diseaseGroupId = 87;
        int locationId = 123;
        boolean isGlobal = false;
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location, DiseaseExtentClass.POSSIBLE_PRESENCE, DiseaseExtentClass.PRESENCE);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(123d);
        when(nativeSQL.findDistanceInsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(null);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isNull();
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenSplitCountryCrossingBoarder() {
        int diseaseGroupId = 87;
        int locationId = 123;
        boolean isGlobal = false;
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location, DiseaseExtentClass.PRESENCE, DiseaseExtentClass.POSSIBLE_PRESENCE, DiseaseExtentClass.UNCERTAIN, DiseaseExtentClass.POSSIBLE_ABSENCE, DiseaseExtentClass.ABSENCE);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);
        when(nativeSQL.findDistanceInsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(123d);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(321d);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isEqualTo(0);
    }

    @Test
    public void findDistanceFromDiseaseExtentWhenNoExtent() {
        int diseaseGroupId = 87;
        int locationId = 123;
        boolean isGlobal = false;
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);
        when(nativeSQL.findDistanceInsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(123d);
        when(nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, isGlobal, locationId)).thenReturn(321d);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isNull();
    }

    public void findDistanceFromDiseaseExtentUsesCacheWhenOutside() {
        int diseaseGroupId = 87;
        int locationId = 123;
        double expectedDistance = 25;
        boolean isGlobal = false;
        when(cacheService.getDistanceToExtentFromCache(diseaseGroupId, locationId)).thenReturn(expectedDistance);
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location, DiseaseExtentClass.UNCERTAIN, DiseaseExtentClass.POSSIBLE_ABSENCE, DiseaseExtentClass.ABSENCE);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    public void findDistanceFromDiseaseExtentUsesCacheWhenInside() {
        // Arrange
        int diseaseGroupId = 87;
        int locationId = 123;
        double expectedDistance = -25;
        boolean isGlobal = false;
        when(cacheService.getDistanceToExtentFromCache(diseaseGroupId, locationId)).thenReturn(expectedDistance);
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        setupExtentClass(diseaseGroupId, isGlobal, location, DiseaseExtentClass.POSSIBLE_PRESENCE, DiseaseExtentClass.PRESENCE);

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, isGlobal, location);

        // Act
        Double actualDistance = helper.findDistanceFromDiseaseExtent(occurrence);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    private void setupExtentClass(int disease, boolean isGlobal, Location location, String... extentClassNames) {
        List<AdminUnitDiseaseExtentClass> classes = new ArrayList<>();
        for (String name : extentClassNames) {
            AdminUnitDiseaseExtentClass adminExtentClass = mock(AdminUnitDiseaseExtentClass.class);
            DiseaseExtentClass extentClass = mock(DiseaseExtentClass.class);
            when(adminExtentClass.getDiseaseExtentClass()).thenReturn(extentClass);
            when(extentClass.getName()).thenReturn(name);
            classes.add(adminExtentClass);
        }
        when(locationService.getAdminUnitDiseaseExtentClassesForLocation(disease, isGlobal, location)).thenReturn(classes);
    }

    private DiseaseOccurrence createDiseaseOccurrence(int diseaseGroupId, boolean isGlobal, Location location) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(isGlobal);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        return occurrence;
    }
}
