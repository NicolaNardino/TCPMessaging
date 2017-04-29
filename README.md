## Welcome to TCPMessaging

It shows how a core Java 8 TCP-multithreaded server can be set-up to process client requests. 
Furthermore, it's got a JEE layer based on a Webservice which exposes a MongoDB backend plugged into the TCServer to store client messages.
The specific use case is about messaging in the context of a simulated chat session.


### Roadmap

There's still much to come:
1. Plug into a server task the web method which extracts the client messages in order to replay the whole chat session.
2. A UI, which will show the messaging live.
