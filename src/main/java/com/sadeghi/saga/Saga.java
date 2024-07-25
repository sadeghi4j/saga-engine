package com.sadeghi.saga;

import com.sadeghi.saga.function.CompensationFunction;
import com.sadeghi.saga.function.StepFunction;
import com.sadeghi.saga.listener.DefaultStepListener;
import com.sadeghi.saga.listener.StepListener;
import com.sadeghi.saga.model.StepExecutionEvent;
import com.sadeghi.saga.model.OperationDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ali Sadeghi
 * Created at 6/22/24 - 10:56 AM
 */

public class Saga {

    private static final Logger log = LogManager.getLogger(Saga.class);

    private final List<StepFunction> steps;
    private final List<CompensationFunction> compensations;
    private final StepListener stepListener;
    private CompensationFunction customCompensation;
    private boolean completed;

    public Saga() {
        this(defaultEventPublisher());
    }

    public Saga(StepListener stepListener) {
        this.steps = new ArrayList<>();
        this.compensations = new ArrayList<>();
        this.stepListener = stepListener;
    }

    public void addStep(StepFunction stepFunction, CompensationFunction compensationFunction) {
        steps.add(stepFunction);
        compensations.add(compensationFunction);
    }

    public void execute(Map<String, Object> contextVariable) {
        execute(0, contextVariable);
    }

    public void execute(int startStepIndex, Map<String, Object> contextVariable) {
        for (int i = startStepIndex; i < steps.size(); i++) {
            try {
                steps.get(i).execute(contextVariable);
                stepListener.StepSucceeded(new StepExecutionEvent(i, OperationDirection.MAIN_FLOW), contextVariable);
            } catch (Exception e) {
                log.error("STEP {} failed. error message: {}", i, e.getMessage());
                stepListener.StepFailed(new StepExecutionEvent(i, OperationDirection.MAIN_FLOW), contextVariable);
                if (customCompensation != null) {
                    customCompensation.execute(contextVariable);
                } else {
                    compensate(i, contextVariable);
                }
                break;
            }
        }
        completed = true;
    }

    private void compensate(int failedStepIndex, Map<String, Object> contextVariable) {
        for (int i = failedStepIndex; i >= 0; i--) {
            try {
                compensations.get(i).execute(contextVariable);
                stepListener.StepSucceeded(new StepExecutionEvent(i, OperationDirection.COMPENSATION_FLOW), contextVariable);
            } catch (Exception e) {
                stepListener.StepFailed(new StepExecutionEvent(i, OperationDirection.COMPENSATION_FLOW), contextVariable);
            }
        }
    }

    public void resume(StepExecutionEvent stepExecutionEvent, Map<String, Object> contextVariable) {
        execute(stepExecutionEvent.getStep(), contextVariable);
    }

    private static StepListener defaultEventPublisher() {
        return new DefaultStepListener();
    }

    public void setCustomCompensation(CompensationFunction customCompensation) {
        this.customCompensation = customCompensation;
    }

    public List<StepFunction> getSteps() {
        return steps;
    }

    public List<CompensationFunction> getCompensations() {
        return compensations;
    }

    public StepListener getStepListener() {
        return stepListener;
    }

    public boolean isCompleted() {
        return completed;
    }
}
