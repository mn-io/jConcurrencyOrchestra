# jConcurrencyOrchestra

[![CircleCI](https://circleci.com/gh/mn-io/jConcurrencyOrchestra.svg?style=svg)](https://circleci.com/gh/mn-io/jConcurrencyOrchestra)

Allows concurrency testing to see how your program behaves on (triggered) race conditions.

## Get started

Code is the best documentation and therefore go here: 
[OrchestratedInterruptServiceImplTest](https://github.com/mn-io/jConcurrencyOrchestra/blob/master/src/test/java/net/mnio/jConcurrencyOrchestra/test/OrchestratedInterruptServiceImplTest.java)

A more realistic use case can be seen in my [spring boot setup repository](https://github.com/mn-io/spring-booter/#Testing).
[UserServiceOrchestrationTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/services/user/UserServiceOrchestrationTest.java)
shows how InterruptService is used within spring environment.

## how it works

TODO

- tag release and build from it
- warning: exception does not raise automatically!