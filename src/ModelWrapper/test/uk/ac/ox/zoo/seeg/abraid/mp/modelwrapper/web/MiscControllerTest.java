package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for MiscController.
 * Copyright (c) 2014 University of Oxford
 */
public class MiscControllerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void updateRExecutablePathRejectsInvalidArguments() throws Exception {
        // Arrange
        List<String> invalidValues = Arrays.asList(null, "");
        MiscController target = new MiscController(mock(ConfigurationService.class));

        for (String value : invalidValues) {
            // Act
            ResponseEntity result = target.updateRExecutablePath(value);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateRExecutablePathSavesValueIfDifferentToExistingValue() throws Exception {
        // Arrange
        String value = "value";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getRExecutablePath()).thenReturn("not" + value);

        // Act
        ResponseEntity result = target.updateRExecutablePath(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setRExecutablePath(value);
    }

    @Test
    public void updateRExecutablePathDoesNotSaveValueIfSameAsExistingValue() throws Exception {
        // Arrange
        String value = "value";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getRExecutablePath()).thenReturn(value);

        // Act
        ResponseEntity result = target.updateRExecutablePath(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(0)).setRExecutablePath(value);
    }

    @Test
    public void updateRExecutablePathAlwaysSavesValueIfInvalidExistingValue() throws Exception {
        // Arrange
        String value = "value";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getRExecutablePath()).thenThrow(new ConfigurationException());

        // Act
        ResponseEntity result = target.updateRExecutablePath(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setRExecutablePath(value);
    }

    @Test
    public void updateMaxRunDurationRejectsInvalidArguments() throws Exception {
        // Arrange
        List<Integer> invalidValues = Arrays.asList(0, 999, -2000, -10);
        MiscController target = new MiscController(mock(ConfigurationService.class));

        for (int value : invalidValues) {
            // Act
            ResponseEntity result = target.updateMaxRunDuration(value);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateMaxRunDurationSavesValueIfDifferentToExistingValue() throws Exception {
        // Arrange
        int value = 1001;
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getMaxModelRunDuration()).thenReturn(-value);

        // Act
        ResponseEntity result = target.updateMaxRunDuration(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setMaxModelRunDuration(value);
    }

    @Test
    public void updateMaxRunDurationDoesNotSaveValueIfSameAsExistingValue() throws Exception {
        // Arrange
        int value = 1001;
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getMaxModelRunDuration()).thenReturn(value);

        // Act
        ResponseEntity result = target.updateMaxRunDuration(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(0)).setMaxModelRunDuration(value);
    }

    @Test
    public void updateCovariateDirectoryRejectsInvalidArguments() throws Exception {
        // Arrange
        List<String> invalidValues = Arrays.asList("", null);
        MiscController target = new MiscController(mock(ConfigurationService.class));

        for (String value : invalidValues) {
            // Act
            ResponseEntity result = target.updateCovariateDirectory(value);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateCovariateDirectorySavesValueIfValid() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getCovariateDirectory()).thenReturn("nonsense");

        // Act
        String dir = testFolder.newFolder().getAbsolutePath();
        ResponseEntity result = target.updateCovariateDirectory(dir);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setCovariateDirectory(dir);
    }

    @Test
    public void updateCovariateDirectoryDoesNotSaveValueIfSameAsExistingValue() throws Exception {
        // Arrange
        String value = "FOO";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getCovariateDirectory()).thenReturn(value);

        // Act
        ResponseEntity result = target.updateCovariateDirectory(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(0)).setCovariateDirectory(value);
    }

    @Test
    public void updateCovariateDirectoryDoesNotSaveValueIfNotValidPath() throws Exception {
        // Arrange
        String value = "nonsense";
        ConfigurationService configurationService = mock(ConfigurationService.class);
        MiscController target = new MiscController(configurationService);
        when(configurationService.getCovariateDirectory()).thenReturn("old-" + value);

        // Act
        ResponseEntity result = target.updateCovariateDirectory(value);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(configurationService, times(0)).setCovariateDirectory(value);
    }
}