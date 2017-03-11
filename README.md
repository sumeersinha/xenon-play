# xenon-play
Includes set of examples created while playing with xenon framework for microservices

### Address Book as a service
- creates a micro service for addressbook that stores name, cell and address
- after running, go to '/core/ui/addressbook/' url to see the fake UI
- Includes Stateless service that returns a fake count based on url parameter,
example of stateless service as a factory

### Bank Account Service
- Includes an example of Bank Account Service to explain 
how to route a request using operation chain
- How to write tests for a xenon service using `BasicReusableHostTestCase` + junit
- How to fire operations on a host

### Task Based Services as Finite State Machine
- Task Finite State Machine, using Xenon's `TaskState` and `TaskFsmTracker`
- For custom state machines, follow `FSM` and `FSMTracker`
- Includes a test to transition service from CREATED state to STARTED state
