# BookStoreApp

we will build a simple Micro-Service framework. A Micro-Service framework consists of two main parts:
A Message-Bus and Micro-Services. Each Micro-Service is a thread that can exchange messages with other Micro-Services using a shared object referred to as the Message-Bus.

In such pattern, each micro-service is a thread which runs a loop. In each iteration of the loop, the thread tries to fetch a message from its queue and process it.

The flow of this app is : 
A customer connects to the store website and orders a book. If the book is available and the customer has enough credit, the order is confirmed - the customer pays for the book,
and then the book should be delivered to his address as soon as possible. 
each microservice take a part small part of the customer request.  
The assignment specification
https://www.cs.bgu.ac.il/~spl191/wiki.files/Assignment2-spl-191.pdf
