## Welcome to TCPMessaging

The project's core is a Java 8 TCP-multithreaded server, set-up to process client requests. 
Furthermore, it's got a JEE layer based on a Web services, which expose a MongoDB backend plugged into the TCServer to load and store client messages.
The specific use case is about messaging in the context of a simulated chat session.

### Development environment and tools
- Ubuntu 15.10.
- Eclipse Neon.
- JBoss Wildfly 10.1.
- MongoDB 3.2.

### Main features
- Multithreaded TCP server.
- Multithreaded TCP clients runner.
- A well-defined client/ server protocol.
- JAX-WS (SOAP) and JAX-RS (RESTful) web services to interact with MongoDB.
- Web service authentication layer by passing caller credentials in the web service request context http headers.
- Servlet context listener used to initialize a MongoDB cient and store it to the servlet context.
- Client and server SOAP handlers.
- Various side-services like the ones that store and load messages from/ to MongoDB.

### Roadmap

There's still much to come:
1. A UI, which will show the messaging live.
