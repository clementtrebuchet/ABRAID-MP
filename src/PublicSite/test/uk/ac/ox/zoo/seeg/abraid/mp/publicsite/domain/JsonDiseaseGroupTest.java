package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtent;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroupType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the JsonDiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseGroupTest {
    @Test
    public void constructorSetsCorrectValues() {
        // Arrange
        DiseaseGroup diseaseGroup = new DiseaseGroup(10);
        diseaseGroup.setParentGroup(new DiseaseGroup(20, "Parent name"));
        diseaseGroup.setName("Name");
        diseaseGroup.setGroupType(DiseaseGroupType.SINGLE);
        diseaseGroup.setPublicName("Public name");
        diseaseGroup.setShortName("Short name");
        diseaseGroup.setAbbreviation("Abbr");
        diseaseGroup.setGlobal(true);
        diseaseGroup.setValidatorDiseaseGroup(new ValidatorDiseaseGroup(30, "Validator name"));
        diseaseGroup.setWeighting(0.1);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        diseaseGroup.setMinNewLocationsTrigger(400);
        diseaseGroup.setMinEnvironmentalSuitability(0.3);
        diseaseGroup.setMinDistanceFromDiseaseExtent(2.0);
        diseaseGroup.setMinDataVolume(100);
        diseaseGroup.setMinDistinctCountries(5);
        diseaseGroup.setHighFrequencyThreshold(10);
        diseaseGroup.setMinHighFrequencyCountries(20);
        diseaseGroup.setOccursInAfrica(true);
        diseaseGroup.setDiseaseExtentParameters(new DiseaseExtent(diseaseGroup, 0.6, 2, 1, 60, 1, 2));

        // Act
        JsonDiseaseGroup jsonDiseaseGroup = new JsonDiseaseGroup(diseaseGroup);

        // Assert
        assertThat(jsonDiseaseGroup.getName()).isEqualTo("Name");
        assertThat(jsonDiseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.SINGLE.toString());
        assertThat(jsonDiseaseGroup.getPublicName()).isEqualTo("Public name");
        assertThat(jsonDiseaseGroup.getShortName()).isEqualTo("Short name");
        assertThat(jsonDiseaseGroup.getAbbreviation()).isEqualTo("Abbr");
        assertThat(jsonDiseaseGroup.getIsGlobal()).isTrue();
        assertThat(jsonDiseaseGroup.getWeighting()).isEqualTo(0.1);
        assertThat(jsonDiseaseGroup.isAutomaticModelRuns()).isEqualTo(true);
        assertThat(jsonDiseaseGroup.getMinNewLocations()).isEqualTo(400);
        assertThat(jsonDiseaseGroup.getMinEnvironmentalSuitability()).isEqualTo(0.3);
        assertThat(jsonDiseaseGroup.getMinDistanceFromDiseaseExtent()).isEqualTo(2.0);
        assertThat(jsonDiseaseGroup.getMinDataVolume()).isEqualTo(100);
        assertThat(jsonDiseaseGroup.getMinDistinctCountries()).isEqualTo(5);
        assertThat(jsonDiseaseGroup.getOccursInAfrica()).isTrue();

        JsonParentDiseaseGroup parentDiseaseGroup = jsonDiseaseGroup.getParentDiseaseGroup();
        assertThat(parentDiseaseGroup).isNotNull();
        assertThat(parentDiseaseGroup.getId()).isEqualTo(20);
        assertThat(parentDiseaseGroup.getName()).isEqualTo("Parent name");

        JsonValidatorDiseaseGroup validatorDiseaseGroup = jsonDiseaseGroup.getValidatorDiseaseGroup();
        assertThat(validatorDiseaseGroup).isNotNull();
        assertThat(validatorDiseaseGroup.getId()).isEqualTo(30);
        assertThat(validatorDiseaseGroup.getName()).isEqualTo("Validator name");

        JsonDiseaseExtent diseaseExtentParameters = jsonDiseaseGroup.getDiseaseExtentParameters();
        assertThat(diseaseExtentParameters).isNotNull();
        assertThat(diseaseExtentParameters.getMinValidationWeighting()).isEqualTo(0.6);
        assertThat(diseaseExtentParameters.getMinOccurrencesForPresence()).isEqualTo(2);
        assertThat(diseaseExtentParameters.getMinOccurrencesForPossiblePresence()).isEqualTo(1);
        assertThat(diseaseExtentParameters.getLowerOccurrenceScore()).isEqualTo(1);
        assertThat(diseaseExtentParameters.getHigherOccurrenceScore()).isEqualTo(2);
    }
}
