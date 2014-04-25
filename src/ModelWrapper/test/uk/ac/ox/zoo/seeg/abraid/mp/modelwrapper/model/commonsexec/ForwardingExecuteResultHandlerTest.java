package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.ExecuteException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the ForwardingExecuteResultHandler class.
 * Copyright (c) 2014 University of Oxford
 */
public class ForwardingExecuteResultHandlerTest {
    @Test
    public void onProcessCompleteShouldForwardExitCode() throws Exception {
        // Arrange
        ProcessHandler mockProcessHandler = mock(ProcessHandler.class);
        ForwardingExecuteResultHandler target = new ForwardingExecuteResultHandler(mockProcessHandler);
        int expectedExitCode = 4321;

        // Act
        target.onProcessComplete(expectedExitCode);

        // Assert
        verify(mockProcessHandler).onProcessComplete(expectedExitCode);
    }

    @Test
    public void onProcessFailedShouldWrapCause() throws Exception {
        // Arrange
        ProcessHandler mockProcessHandler = mock(ProcessHandler.class);
        ForwardingExecuteResultHandler target = new ForwardingExecuteResultHandler(mockProcessHandler);
        ExecuteException expectedCause = new ExecuteException("foo", -123);

        // Act
        target.onProcessFailed(expectedCause);

        // Assert
        ArgumentCaptor<ProcessException> captor = ArgumentCaptor.forClass(ProcessException.class);
        verify(mockProcessHandler).onProcessFailed(captor.capture());
        Throwable cause = captor.getValue().getCause();
        assertThat(cause).isEqualTo(expectedCause);
    }
}