package com.sadeghi.saga.function;

import java.util.Map;

/**
 * @author Ali Sadeghi
 * Created at 6/22/24 - 6:06 PM
 */
@FunctionalInterface
public interface StepFunction {
    void execute(Map<String, Object> contextVariable) throws Exception;
}