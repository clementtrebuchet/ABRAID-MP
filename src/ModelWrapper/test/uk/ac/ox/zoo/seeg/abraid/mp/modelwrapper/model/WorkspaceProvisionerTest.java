package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the WorkspaceProvisionerImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerTest {
    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void provisionWorkspaceShouldCreateDirectoryAtCorrectPath() throws Exception {
        // Arrange
        ScriptGenerator scriptGenerator = new FreemarkerScriptGenerator();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, mock(SourceCodeManager.class), mock(InputDataManager.class));
        File expectedBasePath = testFolder.getRoot();
        String expectedRunName = "bar";
        RunConfiguration config = new RunConfiguration(null, expectedBasePath, expectedRunName, 0, "");

        // Act
        File script = target.provisionWorkspace(config, null);
        File result = script.getParentFile();

        // Assert
        assertThat(result.getParentFile()).isEqualTo(expectedBasePath);
        assertThat(result.getName()).startsWith(expectedRunName);
        assertThat(result.getName()).matches(expectedRunName + "_" + UUID_REGEX);
        assertThat(result).exists();
        assertThat(result).isDirectory();
    }

    @Test
    public void provisionWorkspaceShouldGenerateRunScript() throws Exception {
        // Arrange
        ScriptGenerator scriptGenerator = mock(ScriptGenerator.class);
        File expectedScript = new File("foobar");
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, mock(SourceCodeManager.class), mock(InputDataManager.class));
        RunConfiguration config = new RunConfiguration(null, testFolder.getRoot(), "", 0, "");
        when(scriptGenerator.generateScript(eq(config), any(File.class), eq(false))).thenReturn(expectedScript);

        // Act
        File script = target.provisionWorkspace(config, null);

        // Assert
        verify(scriptGenerator, times(1)).generateScript(eq(config), any(File.class), eq(false));
        assertThat(script).isEqualTo(expectedScript);
    }

    @Test
    public void provisionWorkspaceProvisionsModelCode() throws Exception {
        // Arrange
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        ScriptGenerator scriptGenerator = mock(ScriptGenerator.class);
        InputDataManager inputDataManager = mock(InputDataManager.class);
        when(scriptGenerator.generateScript(any(RunConfiguration.class), any(File.class), anyBoolean())).then(returnsArgAt(1));
        String expectedVersion = "foobar";
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, sourceCodeManager, inputDataManager);
        RunConfiguration config = new RunConfiguration(null, testFolder.getRoot(), "", 0, expectedVersion);

        // Act
        File runDir = target.provisionWorkspace(config, null);

        // Assert
        ArgumentCaptor<String> versionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<File> directoryCaptor = ArgumentCaptor.forClass(File.class);
        verify(sourceCodeManager, times(1)).provisionVersion(versionCaptor.capture(), directoryCaptor.capture());
        assertThat(versionCaptor.getValue()).isEqualTo(expectedVersion);
        assertThat(directoryCaptor.getValue().getParentFile()).isEqualTo(runDir);
        assertThat(directoryCaptor.getValue().getName()).isEqualTo("model");
    }

    @Test
    public void provisionWorkspaceProvisionsInputData() throws Exception {
        // Arrange
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        ScriptGenerator scriptGenerator = mock(ScriptGenerator.class);
        InputDataManager inputDataManager = mock(InputDataManager.class);
        when(scriptGenerator.generateScript(any(RunConfiguration.class), any(File.class), anyBoolean())).then(returnsArgAt(1));
        GeoJsonDiseaseOccurrenceFeatureCollection expectedData = mock(GeoJsonDiseaseOccurrenceFeatureCollection.class);
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, sourceCodeManager, inputDataManager);
        RunConfiguration config = new RunConfiguration(null, testFolder.getRoot(), "", 0, "foobar");

        // Act
        File runDir = target.provisionWorkspace(config, expectedData);

        // Assert
        ArgumentCaptor<GeoJsonDiseaseOccurrenceFeatureCollection> dataCaptor = ArgumentCaptor.forClass(GeoJsonDiseaseOccurrenceFeatureCollection.class);
        ArgumentCaptor<File> directoryCaptor = ArgumentCaptor.forClass(File.class);
        verify(inputDataManager, times(1)).writeData(dataCaptor.capture(), directoryCaptor.capture());
        assertThat(dataCaptor.getValue()).isEqualTo(expectedData);
        assertThat(directoryCaptor.getValue().getParentFile()).isEqualTo(runDir);
        assertThat(directoryCaptor.getValue().getName()).isEqualTo("data");
    }

    @Test
    public void provisionWorkspaceShouldThrowIfWorkspaceDirectoryCanNotBeCreated() throws Exception {
        // Arrange
        File notAValidDirectory = testFolder.newFile();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(mock(ScriptGenerator.class), mock(SourceCodeManager.class), mock(InputDataManager.class));
        RunConfiguration conf = new RunConfiguration(null, notAValidDirectory, "", 0, "");

        // Act
        catchException(target).provisionWorkspace(conf, null);
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }
}