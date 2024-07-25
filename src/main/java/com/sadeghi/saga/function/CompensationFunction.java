package com.sadeghi.saga.function;

import java.util.Map;

/**
 * @author Ali Sadeghi
 * Created at 6/22/24 - 6:07 PM
 */
@FunctionalInterface
public interface CompensationFunction {
    void execute(Map<String, Object> contextVariable);
}