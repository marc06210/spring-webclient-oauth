# Getting Started

**UPDATED TO SPRINGBOOT 3**

## Docker

Run **docker-compose up --build** to build and run the full system. As the project compilation
is done during the creation of the images, the build process can take some time.

The system is autonomous and made of:
* one authorization server
* three instances of resource server (all running the code from the same project with different profiles)
* one client application to consume services exposed by the resource server

To simply run the previously built images, run **docker-compose up**.

Once all started simply access **http://localhost:8080/demo1** or **http://localhost:8080/demo2**

## Code

### client-app

This is the projects that demonstrates the call to other backend systems using **WebClient**.
The calls are made in the **DemoController** class.

It consumes endpoints provided by the **resource-server** injecting the correct credentials.
The consumed URLs are so far hardcoded and match the service name of the docker-compose services.

The calls to the backend systems are done in parallel. So, it is not a good option to have a single
**WebClient** bean in which we inject the security context at runtime before the invocation. The idea
is to have one dedicated **WebClient** per targeted backend system. This
is done in the **WebClientConfiguration**.

The web server listens on port **8080**.

**/demo1** calls in parallel **/cars** and **/bikes** and aggregates the result into one single response.
As bikes are returned with no speed, the returned entries about bikes have speed zero.

**/demo2** calls in parallel **/cars** and **/bikes**. Each bike is enriched by a call to **/bike-speed**.
The call is defined in a reactive way. Then all data are aggregated to produce one single response.

You can inject a error request parameter in order to show two different error handling
strategies. For **/demo1?error=true** no strategy and the process is stopped on error, and
for **/demo2?error=true** in case of error then the process is not stopped

### Authorization server

Auth server that provides JWT for 5 different clients
* client = messaging-client{i}
* secret = secret{i}
The configuration is stored into a **h2** database.

Listens on port **9000**.

### resource-server

Server that exposes protected resources depending on profile

* profile: **car** => exposed endpoint **/cars** on port **8090** (a 2.5 second delay is injected before returning results)
* profile: **bikes** => exposed endpoint **/bikes** on port **8091**
* profile: **speed** => exposed endpoint **/bike-speed** on port **8092**



