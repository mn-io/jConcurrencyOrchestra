# jConcurrencyOrchestra

[![CircleCI](https://circleci.com/gh/mn-io/jConcurrencyOrchestra.svg?style=svg)](https://circleci.com/gh/mn-io/jConcurrencyOrchestra)
[![](https://jitpack.io/v/mn-io/jConcurrencyOrchestra.svg)](https://jitpack.io/#mn-io/jConcurrencyOrchestra)

Allows concurrency testing to verify how your program behaves on (triggered) race conditions.

## Get started

Code is the best documentation and therefore start at the given unit tests in 
[OrchestratedInterruptServiceImplTest.java](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/OrchestratedInterruptServiceImplTest.java)

A more realistic use case can be seen in my [spring boot setup repository](https://github.com/mn-io/spring-booter/#Testing).
[UserServiceOrchestrationTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/services/user/UserServiceOrchestrationTest.java)
shows how InterruptService is used within spring environment.

## how it works and some hints

Code and tests including comments and output should be self explaining as much as possible. In case you have ideas where I can express myself better, __please contact me__.

### Output

Our first unit tests in [OrchestratedInterruptServiceImplTest](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/OrchestratedInterruptServiceImplTest.java)
logs following output:


First line shows in which running order the tasks will be executed. If you don't provide a task name like `/1` it will use a random one. Each task is acutally a thread, therefore Thread count increases.

`[main] INFO TaskSchedule - Running order: Thread-0-Task-/1, Thread-0-Task-/1, Thread-1-Task-/2`


Our dummy service starts working normally by first given task...

`[Thread-0-Task-/1] INFO MyService - Thread-0-Task-/1 => 1`


... until it is interrupted first time by `interruptService.interrupt()`.

`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Interruption called`


As seen in running order Task1 can continue from first interruption ...

`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption`

`[Thread-0-Task-/1] INFO MyService - Thread-0-Task-/1 => 2`


... until second interruption is called.

`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Interruption called`


Task2 can start working now ... 

`[Thread-1-Task-/2] INFO MyService - Thread-1-Task-/2 => 1`

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Interruption called`


... until interrupted and Task1 can continue from second interruption.
`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Continue from interruption`

`[Thread-0-Task-/1] INFO MyService - Thread-0-Task-/1 => 3`


As Task1  has finished, Task2 con continue ....

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption`

`[Thread-1-Task-/2] INFO MyService - Thread-1-Task-/2 => 2`

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Interruption called`


... which means each interruption is continues immediately. 

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Continue from interruption`

`[Thread-1-Task-/2] INFO MyService - Thread-1-Task-/2 => 3`
`


### Exceptions 

Exceptions can occur and shall not intercept our tests. They are logged and can be verified after completion.
`OrchestratedInterruptServiceImpl.start()` returns a boolean, which gives a quick hint whether everything was OK.