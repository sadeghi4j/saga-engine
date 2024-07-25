package com.sadeghi.saga;

import com.sadeghi.saga.function.CompensationFunction;
import com.sadeghi.saga.function.StepFunction;
import com.sadeghi.saga.listener.StepListener;
import com.sadeghi.saga.model.StepExecutionEvent;
import com.sadeghi.saga.model.OperationDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Ali Sadeghi
 * Created at 7/4/24 - 7:29 PM
 */
class SagaTest {

    private Saga saga;
    private StepListener stepListener;

    @BeforeEach
    void setUp() {
        stepListener = mock(StepListener.class);
        saga = new Saga(stepListener);
    }

    @Test
    void testAddStep() {

        StepFunction stepFunction = (c) -> System.out.println("a");
        CompensationFunction compensationFunction = (c) -> System.out.println("a");
        saga.addStep(stepFunction, compensationFunction);

        assertEquals(1, saga.getSteps().size());
        assertEquals(1, saga.getCompensations().size());
    }

    @Test
    void testExecuteSuccessfulSteps() throws Exception {
        StepFunction step1 = mock(StepFunction.class);
        StepFunction step2 = mock(StepFunction.class);
        CompensationFunction compensation1 = mock(CompensationFunction.class);
        CompensationFunction compensation2 = mock(CompensationFunction.class);

        saga.addStep(step1, compensation1);
        saga.addStep(step2, compensation2);

        Map<String, Object> contextVariable = new HashMap<>();
        saga.execute(contextVariable);

        verify(step1, times(1)).execute(contextVariable);
        verify(step2, times(1)).execute(contextVariable);
        verify(saga.getStepListener(), times(2)).StepSucceeded(any(StepExecutionEvent.class), any());

        assertTrue(saga.isCompleted());
    }

    @Test
    void testExecuteWithExceptionAtStep1() throws Exception {

        StepFunction step1 = mock(StepFunction.class);
        StepFunction step2 = mock(StepFunction.class);
        CompensationFunction compensation1 = mock(CompensationFunction.class);
        CompensationFunction compensation2 = mock(CompensationFunction.class);

        saga.addStep(step1, compensation1);
        saga.addStep(step2, compensation2);

        Map<String, Object> contextVariable = new HashMap<>();

        doThrow(new RuntimeException("Step 1 failed")).when(step1).execute(contextVariable);

        saga.execute(contextVariable);

        verify(step1, times(1)).execute(contextVariable);
        verify(step2, never()).execute(contextVariable);

        verify(compensation1, times(1)).execute(contextVariable);
        verify(compensation2, never()).execute(contextVariable);

        // Step 1 failed
        verify(stepListener, times(1)).StepFailed(any(StepExecutionEvent.class), any());
        // 1 time at compensation
        verify(stepListener, times(1)).StepSucceeded(any(StepExecutionEvent.class), any());

    }

    @Test
    void testExecuteWithExceptionAtStep2() throws Exception {

        StepFunction step1 = mock(StepFunction.class);
        StepFunction step2 = mock(StepFunction.class);
        CompensationFunction compensation1 = mock(CompensationFunction.class);
        CompensationFunction compensation2 = mock(CompensationFunction.class);

        saga.addStep(step1, compensation1);
        saga.addStep(step2, compensation2);

        Map<String, Object> contextVariable = new HashMap<>();

        doThrow(new RuntimeException("Step 2 failed")).when(step2).execute(contextVariable);

        saga.execute(contextVariable);

        verify(step1, times(1)).execute(contextVariable);
        verify(step2, times(1)).execute(contextVariable);
        verify(compensation1, times(1)).execute(contextVariable);
        verify(compensation2, times(1)).execute(contextVariable);

        // 1 time at execution, 2 times at compensation
        verify(stepListener, times(3)).StepSucceeded(any(StepExecutionEvent.class), any());
        verify(stepListener, times(1)).StepFailed(any(StepExecutionEvent.class), any());

    }

    @Test
    void testWhenStep2Failed_ThenRunCustomCompensation() throws Exception {
        StepFunction step1 = mock(StepFunction.class);
        StepFunction step2 = mock(StepFunction.class);
        CompensationFunction compensation1 = mock(CompensationFunction.class);
        CompensationFunction compensation2 = mock(CompensationFunction.class);
        CompensationFunction customCompensation = mock(CompensationFunction.class);

        saga.addStep(step1, compensation1);
        saga.addStep(step2, compensation2);
        saga.setCustomCompensation(customCompensation);

        Map<String, Object> contextVariable = new HashMap<>();

        doThrow(new RuntimeException("Step 2 failed")).when(step2).execute(contextVariable);

        saga.execute(contextVariable);

        verify(step1, times(1)).execute(contextVariable);
        verify(step2, times(1)).execute(contextVariable);
        verify(customCompensation, times(1)).execute(contextVariable);
        verify(compensation1, never()).execute(contextVariable);
        verify(compensation2, never()).execute(contextVariable);
        // Execution of step 2
        verify(stepListener, times(1)).StepSucceeded(any(StepExecutionEvent.class), any());
        verify(stepListener, times(1)).StepFailed(any(StepExecutionEvent.class), any());
    }

    @Test
    void testResumeFromStep2() throws Exception {
        StepFunction step1 = mock(StepFunction.class);
        StepFunction step2 = mock(StepFunction.class);
        CompensationFunction compensation1 = mock(CompensationFunction.class);
        CompensationFunction compensation2 = mock(CompensationFunction.class);

        saga.addStep(step1, compensation1);
        saga.addStep(step2, compensation2);

        Map<String, Object> contextVariable = new HashMap<>();

        StepExecutionEvent stepExecutionEvent = new StepExecutionEvent(1, OperationDirection.MAIN_FLOW);

        saga.resume(stepExecutionEvent, contextVariable);

        verify(step1, never()).execute(contextVariable);
        verify(step2, times(1)).execute(contextVariable);

        verify(stepListener, never()).StepFailed(any(StepExecutionEvent.class), any());
        verify(stepListener, times(1)).StepSucceeded(any(StepExecutionEvent.class), any());
    }

}