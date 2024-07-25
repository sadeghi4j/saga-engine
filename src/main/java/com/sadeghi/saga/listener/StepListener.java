package com.sadeghi.saga.listener;

import com.sadeghi.saga.model.StepExecutionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ali Sadeghi
 * Created at 6/22/24 - 6:13 PM
 */
public interface StepListener {
    void StepSucceeded(StepExecutionEvent stepExecutionEvent, Map<String, Object> contextVariable);
    void StepFailed(StepExecutionEvent stepExecutionEvent, Map<String, Object> contextVariable);

//    List<StepExecutionEvent> findInCompleteOperation();

}