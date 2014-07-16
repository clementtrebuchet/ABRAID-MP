package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the DiseaseGroupForModelRunValidator class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupForModelRunValidatorTest {
    @Test
    public void diseaseGroupIsValid() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isNull();
    }

    @Test
    public void publicNameIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setPublicName(null);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the public name is missing");
    }

    @Test
    public void shortNameIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setShortName("    ");
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the short name is missing");
    }

    @Test
    public void abbreviationIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setAbbreviation("");
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the abbreviation is missing");
    }

    @Test
    public void globalOrTropicalIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setGlobal(null);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("global/tropical is missing");
    }

    @Test
    public void validatorDiseaseGroupIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setValidatorDiseaseGroup(null);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the Data Validator disease group is missing");
    }

    private DiseaseGroup createDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);
        diseaseGroup.setName("Test name");
        diseaseGroup.setPublicName("Test public name");
        diseaseGroup.setShortName("Test short name");
        diseaseGroup.setAbbreviation("Test abbreviation");
        diseaseGroup.setGlobal(false);
        diseaseGroup.setValidatorDiseaseGroup(new ValidatorDiseaseGroup());
        return diseaseGroup;
    }
}
