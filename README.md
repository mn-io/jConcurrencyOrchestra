# jConcurrencyOrchestra

[![CircleCI](https://circleci.com/gh/mn-io/jConcurrencyOrchestra.svg?style=svg)](https://circleci.com/gh/mn-io/jConcurrencyOrchestra)
[![](https://jitpack.io/v/mn-io/jConcurrencyOrchestra.svg)](https://jitpack.io/#mn-io/jConcurrencyOrchestra)

Allows concurrency testing to see how your program behaves on (triggered) race conditions.

## Get started

Code is the best documentation and therefore go here: 
[OrchestratedInterruptServiceImplTest](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/OrchestratedInterruptServiceImplTest.java)

A more realistic use case can be seen in my [spring boot setup repository](https://github.com/mn-io/spring-booter/#Testing).
[UserServiceOrchestrationTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/services/user/UserServiceOrchestrationTest.java)
shows how InterruptService is used within spring environment.

## how it works and some hints

TODO

- See TaskSchedule Log output

23:05:41.669 [main] INFO net.mnio.jConcurrencyOrchestra.test.TaskSchedule - Running order: Thread-0-Task-/1, Thread-0-Task-/1, Thread-1-Task-/2
23:05:41.780 [Thread-0-Task-/1] INFO net.mnio.jConcurrencyOrchestra.test.MyService - Thread-0-Task-/1 => 1
23:05:41.781 [Thread-0-Task-/1] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Interruption called
23:05:41.880 [Thread-0-Task-/1] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Continue from interruption
23:05:41.880 [Thread-0-Task-/1] INFO net.mnio.jConcurrencyOrchestra.test.MyService - Thread-0-Task-/1 => 2
23:05:41.880 [Thread-0-Task-/1] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 2 - Interruption called
23:05:42.180 [Thread-1-Task-/2] INFO net.mnio.jConcurrencyOrchestra.test.MyService - Thread-1-Task-/2 => 1
23:05:42.180 [Thread-1-Task-/2] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Interruption called
23:05:42.281 [Thread-0-Task-/1] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 2 - Continue from interruption
23:05:42.281 [Thread-0-Task-/1] INFO net.mnio.jConcurrencyOrchestra.test.MyService - Thread-0-Task-/1 => 3
23:05:42.481 [Thread-1-Task-/2] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Continue from interruption
23:05:42.481 [Thread-1-Task-/2] INFO net.mnio.jConcurrencyOrchestra.test.MyService - Thread-1-Task-/2 => 2
23:05:42.481 [Thread-1-Task-/2] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 2 - Interruption called
23:05:42.681 [Thread-1-Task-/2] INFO net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 2 - Continue from interruption
23:05:42.681 [Thread-1-Task-/2] INFO net.mnio.jConcurrencyOrchestra.test.MyService - Thread-1-Task-/2 => 3


- tag release and build from it
- warning: exception does not raise automatically!