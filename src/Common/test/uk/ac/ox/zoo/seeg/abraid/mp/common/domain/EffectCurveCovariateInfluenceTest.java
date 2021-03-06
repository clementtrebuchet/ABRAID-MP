package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvEffectCurveCovariateInfluence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for EffectCurveCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class EffectCurveCovariateInfluenceTest {
    @Test
    public void constructorBindsFieldCorrectly() {
        // Arrange
        ModelRun runExpectation = mock(ModelRun.class);
        CovariateFile covariateExpectation = mock(CovariateFile.class);
        CsvEffectCurveCovariateInfluence dtoExpectation = new CsvEffectCurveCovariateInfluence();
        dtoExpectation.setMeanInfluence(3.0);
        dtoExpectation.setLowerQuantile(4.0);
        dtoExpectation.setUpperQuantile(5.0);
        dtoExpectation.setCovariateValue(6.0);

        // Act
        EffectCurveCovariateInfluence result = new EffectCurveCovariateInfluence(covariateExpectation, dtoExpectation, runExpectation);

        // Assert
        assertThat(result.getModelRun()).isEqualTo(runExpectation);
        assertThat(result.getCovariateFile()).isEqualTo(covariateExpectation);
        assertThat(result.getMeanInfluence()).isEqualTo(dtoExpectation.getMeanInfluence());
        assertThat(result.getLowerQuantile()).isEqualTo(dtoExpectation.getLowerQuantile());
        assertThat(result.getUpperQuantile()).isEqualTo(dtoExpectation.getUpperQuantile());
        assertThat(result.getCovariateValue()).isEqualTo(dtoExpectation.getCovariateValue());
        assertThat(result.getId()).isNull();
    }
}
