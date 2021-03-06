package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonCrs.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonCrsTest {
    @Test
    public void constructorForGeoJsonCrsBindsParametersCorrectly() throws Exception {
        // Arrange
        String expectedType = "foo";
        Object expectedProperties = mock(Object.class);

        // Act
        GeoJsonCrs target = new GeoJsonCrs(expectedType, expectedProperties) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getType()).isSameAs(expectedType);
        assertThat(target.getProperties()).isSameAs(expectedProperties);
    }
}
