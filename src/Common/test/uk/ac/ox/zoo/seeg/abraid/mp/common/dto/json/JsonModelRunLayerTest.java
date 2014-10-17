package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonModelRunLayer.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunLayerTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getName()).thenReturn("expectedName");
        when(modelRun.getRequestDate()).thenReturn(new DateTime(1413210069L * 1000));

        // Act
        JsonModelRunLayer result = new JsonModelRunLayer(modelRun);

        // Assert
        assertThat(result.getId()).isEqualTo("expectedName");
        assertThat(result.getDate()).isEqualTo("2014-10-13");
    }
}