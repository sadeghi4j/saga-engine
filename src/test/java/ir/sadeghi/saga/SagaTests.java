package ir.sadeghi.saga;

import com.sadeghi.saga.Saga;
import com.sadeghi.saga.function.StepFunction;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ali Sadeghi
 * Created at 7/4/24 - 7:15 PM
 */

public class SagaTests {

    @Test
    void testSuccessfulOperation(String id) {
        Saga saga = new Saga();
        Map<String, Object> contextVariable = new HashMap<>();

        saga.addStep(
                (c) -> operation1(id),
                (c) -> compensation1("123", 123)
        );

        saga.addStep(
                (c) -> operation2(id, 112),
                (c) -> compensation2("123", 123)
        );

        /*saga.addStep(
                () -> cmService.doTransaction(saga.getContext()),
                () -> cmService.reverse(saga.getContext())
        );

        saga.addStep(
                () -> accountService.withdraw(),
                () -> accountService.reverse()
        );*/

        saga.setCustomCompensation((c) -> {

        });
        saga.execute(contextVariable);

    }



    // Example step function
    public StepFunction operation1(String param) {
        return contextVariable -> {
            System.out.println("Executing: " + param);
            // Simulate failure on a specific step
            if ("Step 1".equals(param)) {
                throw new Exception("Intentional failure on Step 1");
            }
            contextVariable.put("1", "some value!!!");
        };
    }

    public StepFunction operation2(String param, int value) {
        return contextVariable -> {
            Object result1 = contextVariable.get("result1");
            System.out.println("Executing: " + param + " with value " + value + " and result1: " + result1);
            if ("Step 2".equals(param)) {
                contextVariable.put("result2", "Result from Step 2");
                throw new Exception("Intentional failure on Step 2");
            }
        };
    }

    public void compensation1(String param, int value) {
        System.out.println("Compensating: " + param + " with value " + value);
    }

    public void compensation2(String param, int value) {
        System.out.println("Compensating: " + param + " with value " + value);
    }

}
