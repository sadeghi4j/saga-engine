
Implements Saga Orchestration pattern

## Features
- No framework dependency (like Spring)
- Resume-able
- 
```java

void payment() {
    
    Saga saga = new Saga();

    saga.addStep(
      () -> accountService.withdraw(),
      () -> accountService.reverseWithdraw()
    );

    saga.addStep(
      () -> accountService.deposit(),
      () -> accountService.reverseDeposit()
    );

    saga.execute();
}
```