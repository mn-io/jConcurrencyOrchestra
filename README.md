# jConcurrencyOrchestra

[![CircleCI](https://circleci.com/gh/mn-io/jConcurrencyOrchestra.svg?style=svg)](https://circleci.com/gh/mn-io/jConcurrencyOrchestra)
[![](https://jitpack.io/v/mn-io/jConcurrencyOrchestra.svg)](https://jitpack.io/#mn-io/jConcurrencyOrchestra)

Allows concurrency testing to verify how your program behaves on (triggered) race conditions.


## Get started

Code is the best documentation and therefore start at the given unit tests in 
[OrchestratedInterruptServiceImplTest.java](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/OrchestratedInterruptServiceImplTest.java)

A more realistic use case can be seen in my [spring boot setup repository](https://github.com/mn-io/spring-booter/#Concurrency).
[UserServiceConcurrencyTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/services/user/UserServiceConcurrencyTest.java)
shows how InterruptService is used within spring environment.


## Understanding

Code and tests including comments and output is desired to be self explaining. __Please contact me__ or __create a pull request__ in case anything is not clear or can be improved.

### Demo Service

First understand what our demo service in [MyService.java](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/MyService.java)
is desired to do.

1. Prints and appends to results: **CurrentThreadName**-Task-**TaskName** => 1
2. Interrupts with description: 1 -> 2
3. Prints and appends to results: **CurrentThreadName**-Task-**TaskName** => 2
4. Interrupts with description: 2 -> 3
5. Prints and appends to results: **CurrentThreadName**-Task-**TaskName** => 3

**NB**: Each task is actually a thread. Therefore each task belongs to a single thread. Log will print the thread name as well, which verifies correct behavior.


### What is expected

Our first unit test (`happyPathJava7`) in 
[OrchestratedInterruptServiceImplTest](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/OrchestratedInterruptServiceImplTest.java)
creates two tasks, which run the same service in parallel. 


The member `result` in 
[MyService.java](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/MyService.java) 
is shared between different threads. Normally we could not guarantee a certain order of results collected. 
But as we interrupt after each step and let only one thread work at a time, we can control what happens.


Our simple demo test case has its running order: Task1, Task1, Task2.


### Test Output

The mentioned unit tests logs following output, which explains best what is going on.


#### Before run

First line shows in which running order the tasks will be executed. 
If you don't provide a task name like `/1` it will use a random one.
Each task is actually a thread, therefore thread count increases, here starting with 0.

`[main] INFO TaskSchedule - Running order: Thread-0-Task-/1, Thread-0-Task-/1, Thread-1-Task-/2`


### Task 1 starts first time

Our demo service starts working normally by first given task...

`[Thread-0-Task-/1] INFO MyService - Thread-0-Task-/1 => 1`


... until it is interrupted first time by `interruptService.interrupt(""1->2")`.
A given description like `'1->2'`in code and output can help to understand what is the current state

`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Interruption '1->2' called`


#### Task 1 starts second time

As seen in running order Task1 can continue from first interruption ...

`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption '1->2'`

`[Thread-0-Task-/1] INFO MyService - Thread-0-Task-/1 => 2`


... until second interruption is called.

`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Interruption '2->3' called`


#### Task 2 starts first time

Task2 can start working now ... 

`[Thread-1-Task-/2] INFO MyService - Thread-1-Task-/2 => 1`

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Interruption 1->2' called`


#### Tasks finishes by initial running order

... until interrupted and Task1 can continue from second interruption.

`[Thread-0-Task-/1] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Continue from '2->3' interruption`

`[Thread-0-Task-/1] INFO MyService - Thread-0-Task-/1 => 3`


As Task1  has finished, Task2 con continue ....

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 1. time for thread - Continue from 1->2' interruption`

`[Thread-1-Task-/2] INFO MyService - Thread-1-Task-/2 => 2`

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Interruption '2->3' called`


... which means each interruption is continues immediately. 

`[Thread-1-Task-/2] INFO OrchestratedInterruptServiceImpl - 2. time for thread - Continue from interruption '2->3'`

`[Thread-1-Task-/2] INFO MyService - Thread-1-Task-/2 => 3`


### Exceptions 

Exceptions can occur and shall not interfere our tests. They are logged and can be verified after completion.
`OrchestratedInterruptServiceImpl.start()` returns a boolean, which gives a quick hint whether everything was OK.