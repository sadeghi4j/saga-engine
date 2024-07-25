package com.sadeghi.saga.listener;

import com.sadeghi.saga.model.StepExecutionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author Ali Sadeghi
 * Created at 7/12/24 - 8:38 PM
 */
public class DefaultStepListener implements StepListener {

    private static final Logger log = LogManager.getLogger(DefaultStepListener.class);

    @Override
    public void StepSucceeded(StepExecutionEvent stepExecutionEvent, Map<String, Object> contextVariable) {
        log.info("Step Succeeded. stepExecutionEvent: {}", stepExecutionEvent);
    }

    @Override
    public void StepFailed(StepExecutionEvent stepExecutionEvent, Map<String, Object> contextVariable) {
        log.info("Step Failed. stepExecutionEvent: {}", stepExecutionEvent);
    }
}
