package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covarites;

import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates.CovariatesController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates.CovariatesControllerHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates.CovariatesControllerValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for CovariatesController.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariatesControllerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void showCovariatesPageReturnsCorrectModelData() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariatesControllerHelper.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariatesControllerHelper.getCovariateConfiguration().setDiseases(new ArrayList<JsonModelDisease>());
        Model model = mock(Model.class);

        // Act
        target.showCovariatesPage(model);

        // Assert
        verify(model).addAttribute("initialData", "{\"diseases\":[],\"files\":[]}");
    }

    @Test
    public void showCovariatesPageReturnsSortedDiseases() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariatesControllerHelper.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariatesControllerHelper.getCovariateConfiguration().setDiseases(new ArrayList<JsonModelDisease>());
        covariatesControllerHelper.getCovariateConfiguration().getDiseases().add(new JsonModelDisease(23, "aaa"));
        covariatesControllerHelper.getCovariateConfiguration().getDiseases().add(new JsonModelDisease(24, "zzz"));
        covariatesControllerHelper.getCovariateConfiguration().getDiseases().add(new JsonModelDisease(25, "ggg"));

        Model model = mock(Model.class);

        // Act
        target.showCovariatesPage(model);

        // Assert
        verify(model).addAttribute("initialData", "{\"diseases\":[{\"id\":23,\"name\":\"aaa\"},{\"id\":25,\"name\":\"ggg\"},{\"id\":24,\"name\":\"zzz\"}],\"files\":[]}");
    }

    @Test
    public void showCovariatesPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariatesControllerHelper.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariatesControllerHelper.getCovariateConfiguration().setDiseases(new ArrayList<JsonModelDisease>());
        Model model = mock(Model.class);

        // Act
        String result = target.showCovariatesPage(model);

        // Assert
        assertThat(result).isEqualTo("admin/covariates");
    }

    @Test
    public void showCovariatesPageThrowsForInvalidCovariateConfig() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenThrow(new IOException());
        Model model = mock(Model.class);

        // Act
        catchException(target).showCovariatesPage(model);

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessage("Existing covariate configuration is invalid.");
    }

    @Test
    public void updateCovariatesRejectsInvalidInputs() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesControllerValidator covariatesControllerValidator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, covariatesControllerValidator, new AbraidJsonObjectMapper());
        JsonCovariateConfiguration invalidConf = new JsonCovariateConfiguration();

        when(covariatesControllerValidator.validateCovariateConfiguration(any(JsonCovariateConfiguration.class))).thenReturn(Arrays.asList("FAIL1", "FAIL2"));
        for (JsonCovariateConfiguration conf : Arrays.asList(invalidConf, null)) {
            // Act
            ResponseEntity result = target.updateCovariates(conf);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
//
//    @Test
//    public void updateCovariatesThrowsIfConfigurationCanNotBeSaved() throws Exception {
//        // Arrange
//        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
//        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
//        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
//        when(conf.isValid()).thenReturn(true);
//        doThrow(new IOException()).when(covariatesControllerHelper).setCovariateConfiguration(conf);
//
//        // Act
//        catchException(target).updateCovariates(conf);
//
//        // Assert
//        assertThat(caughtException())
//                .isInstanceOf(IOException.class)
//                .hasMessage("Covariate configuration update failed.");
//    }
//
//    @Test
//    public void updateCovariatesSavedConfiguration() throws Exception {
//        // Arrange
//        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
//        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
//        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
//        when(conf.isValid()).thenReturn(true);
//
//        // Act
//        ResponseEntity result = target.updateCovariates(conf);
//
//        // Assert
//        verify(covariatesControllerHelper).setCovariateConfiguration(conf);
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//    }

    @Test
    public void addCovariateFileValidatesItsInputsCorrectly() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        when(expectedFile.getOriginalFilename()).thenReturn("file.ext");
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/file.ext");
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);


        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn(expectedPath);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString()))
            .thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<JsonFileUploadResponse> result = target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        verify(validator).validateCovariateUpload(
                expectedName, expectedSubdirectory, expectedFile, expectedPath
        );
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().getStatus()).isEqualTo("FAIL");
        assertThat(result.getBody().getMessages()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void addCovariateFileSavesTheFileCorrectly() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/" + expectedFileName);
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);

        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn(expectedPath);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString()))
                .thenReturn(new ArrayList<String>());

        // Act
        target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        verify(covariatesControllerHelper).saveNewCovariateFile(expectedName, expectedPath, expectedFile);
    }
//
//    @Test
//    public void addCovariateFileUpdatesTheCovariateConfigurationCorrectly() throws Exception {
//        // Arrange
//        final MultipartFile expectedFile = mock(MultipartFile.class);
//        final String expectedFileName = "file.ext";
//        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
//        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
//        final String expectedName = "name";
//        final String expectedSubdirectory = "dir";
//        final String expectedCovariateDir = testFolder.newFolder().toString();
//        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);
//        final List<JsonCovariateFile> covariateFileList = new ArrayList<>();
//        when(expectedCovariateConf.getFiles()).thenReturn(covariateFileList);
//
//        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
//        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
//        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn(expectedPath);
//        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
//        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
//        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString()))
//                .thenReturn(new ArrayList<String>());
//
//        // Act
//        target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);
//
//        // Assert
//        assertThat(covariateFileList).hasSize(1);
//        assertThat(covariateFileList.get(0).getName()).isEqualTo(expectedName);
//        assertThat(covariateFileList.get(0).getPath()).isEqualTo(expectedSubdirectory + "/" + expectedFileName);
//        assertThat(covariateFileList.get(0).getHide()).isEqualTo(false);
//        assertThat(covariateFileList.get(0).getInfo()).isEqualTo(null);
//        assertThat(covariateFileList.get(0).getEnabled()).hasSize(0);
//    }
//
//    @Test
//    public void addCovariateFileCorrectlyNormalizesPaths() throws Exception {
//        // Arrange
//        final MultipartFile expectedFile = mock(MultipartFile.class);
//        final String expectedFileName = "file.ext";
//        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
//        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
//        final String expectedName = "name";
//        final String expectedSubdirectory = "/one\\two/dir";
//        final String expectedCovariateDir = testFolder.getRoot().toString();
//        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);
//        final List<JsonCovariateFile> covariateFileList = new ArrayList<>();
//        when(expectedCovariateConf.getFiles()).thenReturn(covariateFileList);
//
//        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
//        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
//        when(covariatesControllerHelper.getCovariateDirectory()).thenReturn(expectedCovariateDir);
//        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
//        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
//        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString(), anyString(), any(JsonCovariateConfiguration.class)))
//                .thenReturn(new ArrayList<String>());
//
//        // Act
//        target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);
//
//        // Assert
//        assertThat(covariateFileList).hasSize(1);
//        assertThat(covariateFileList.get(0).getPath()).isEqualTo("one/two/dir/file.ext");
//    }

    //    @Test
//    public void isValidForCorrectInputs() {
//        // Arrange
//        String path = "path";
//        String name = "name";
//        String info = "info";
//        boolean hide = true;
//        List<Integer> enabled = new ArrayList<>();
//
//        // Act
//        JsonCovariateFile result = new JsonCovariateFile(path, name, info, hide, enabled);
//
//        // Assert
//        assertThat(result.isValid()).isTrue();
//    }
//
//    @Test
//    public void isNotValidForMissingPath() {
//        // Arrange
//        String path = "";
//        String name = "name";
//        String info = "info";
//        boolean hide = true;
//        List<Integer> enabled = new ArrayList<>();
//
//        // Act
//        JsonCovariateFile result = new JsonCovariateFile(path, name, info, hide, enabled);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }
//
//    @Test
//    public void isNotValidForDuplicateEnabledId() {
//        // Arrange
//        String path = "path";
//        String name = "name";
//        String info = "info";
//        boolean hide = true;
//        List<Integer> enabled = Arrays.asList(1, 1);
//
//        // Act
//        JsonCovariateFile result = new JsonCovariateFile(path, name, info, hide, enabled);
//
//        // Assert
//        assertThat(result.isValid()).isFalse();
//    }

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
    @Test
    public void addCovariateFileReturnsAnAppropriateStatusForSuccess() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "dir";
        final List<JsonCovariateFile> covariateFileList = new ArrayList<>();

        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn("xyz");
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString()))
                .thenReturn(new ArrayList<String>());

        // Act
        ResponseEntity<JsonFileUploadResponse> result = target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getBody().getMessages()).hasSize(0);
    }
}
