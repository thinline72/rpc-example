# Simple RPC app with Spring, Guice, Jetty HTTP/2 and Protostuff

This is a simple RPC application which uses SpringBoot, Spring Remote for RPC, Jetty HTTP/2 for 
connectivity and Protostuff for Serialization. It consists of 3 modules. 

## RPC Module

This module has common classes for both Client and Server modules and uses Protostuff Runtime Serializer 
as SerializationService implementation.

## Server Module

This module provides JettyServerCustomizer with custom HTTP/2 server connector and provides implementation 
of HttpInvokerServiceExporter for using predefined SerializationService.

## Client Module

This module has custom implementation of AbstractHttpInvokerRequestExecutor with Jetty HTTP/2 Client. 
As it uses prior knowledge about HTTP/2 on server-side you could try it without ALPN. Also it shows how to combine 
Spring DI container with Guice.