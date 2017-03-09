# xenon-play
Playing with xenon framework for microservices

- creates a micro service for addressbook that stores name, cell and address
- after running, go to '/core/ui/addressbook/' url to see the fake UI
- Includes Stateless service that returns a fake count based on url parameter,
example of stateless service as a factory

- Includes an example of Bank Account Service to explain 
how to route a request using operation chain
- How to write tests for a xenon service using `BasicReusableHostTestCase` + junit
- How to fire operations on a host
