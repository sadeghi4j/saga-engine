package com.sadeghi.saga.model;

import java.util.Map;

/**
 * @author Ali Sadeghi
 * Created at 6/23/24 - 5:34 AM
 */
public class StepExecutionEvent {
    private final int step;

    private final OperationDirection operationDirection;

    public StepExecutionEvent(int step, OperationDirection operationDirection) {
        this.step = step;
        this.operationDirection = operationDirection;
    }

    public int getStep() {
        return step;
    }

    public OperationDirection getOperationDirection() {
        return operationDirection;
    }

}
