package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonCovariateConfiguration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateConfigurationTest {
    @Test
    public void bindsFieldsCorrectly() {
        // Arrange
        List<JsonModelDisease> expectedDiseases = new ArrayList<>();
        List<JsonCovariateFile> expectedFiles = new ArrayList<>();

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.getDiseases()).isEqualTo(expectedDiseases);
        assertThat(result.getFiles()).isEqualTo(expectedFiles);
    }

//    @Test
//    public void isValidForCorrectInputs() {
//        // Arrange
//        List<JsonModelDisease> expectedDiseases = new ArrayList<>();
//        List<JsonCovariateFile> expectedFiles = new ArrayList<>();
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isTrue();
//    }
//
//    @Test
//    public void isNotValidForMissingDiseases() {
//        // Arrange
//        List<JsonModelDisease> expectedDiseases = null;
//        List<JsonCovariateFile> expectedFiles = new ArrayList<>();
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForMissingFiles() {
//        // Arrange
//        List<JsonModelDisease> expectedDiseases = new ArrayList<>();
//        List<JsonCovariateFile> expectedFiles = null;
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForInvalidDiseases() {
//        // Arrange
//        JsonModelDisease mockDisease = mock(JsonModelDisease.class);
//        when(mockDisease.isValid()).thenReturn(false);
//        List<JsonModelDisease> expectedDiseases = Arrays.asList(mockDisease);
//        List<JsonCovariateFile> expectedFiles = new ArrayList<>();
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForInvalidFiles() {
//        // Arrange
//        List<JsonModelDisease> expectedDiseases = new ArrayList<>();
//        JsonCovariateFile mockFile = mock(JsonCovariateFile.class);
//        when(mockFile.isValid()).thenReturn(false);
//        List<JsonCovariateFile> expectedFiles = Arrays.asList(mockFile);
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForDuplicateDiseases() {
//        // Arrange
//        JsonModelDisease mockDisease = mock(JsonModelDisease.class);
//        when(mockDisease.getId()).thenReturn(1);
//        List<JsonModelDisease> expectedDiseases = Arrays.asList(mockDisease, mockDisease);
//        List<JsonCovariateFile> expectedFiles = new ArrayList<>();
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForDuplicateFiles() {
//        // Arrange
//        List<JsonModelDisease> expectedDiseases = new ArrayList<>();
//        JsonCovariateFile mockFile = mock(JsonCovariateFile.class);
//        when(mockFile.getPath()).thenReturn("foo");
//        List<JsonCovariateFile> expectedFiles = Arrays.asList(mockFile, mockFile);
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForBrokenDiseaseIdReferences() {
//        // Arrange
//        List<JsonModelDisease> expectedDiseases = new ArrayList<>();
//        JsonCovariateFile mockFile = mock(JsonCovariateFile.class);
//        when(mockFile.getEnabled()).thenReturn(Arrays.asList(1));
//        List<JsonCovariateFile> expectedFiles = Arrays.asList(mockFile);
//
//        // Act
//        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
}
