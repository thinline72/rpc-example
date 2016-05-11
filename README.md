# Simple RPC application

This is a simple RPC application which uses SpringBoot, Spring Remote for RPC, Jetty HTTP/2 for 
connectivity and Protostuff for Serialization. It consists of 3 modules. 

## RPC Module

This module has common classes for both Client and Server modules and uses [Protostuff Runtime Serializer](https://github.com/thinline72/rpc-example/blob/master/rpc/src/main/java/com/ish/rpc/ProtostuffSerializationServiceImpl.java)
as [SerializationService](https://github.com/thinline72/rpc-example/blob/master/rpc/src/main/java/com/ish/rpc/SerializationService.java) implementation.

## Server Module

This module provides [JettyHttp2ServerConfig](https://github.com/thinline72/rpc-example/blob/master/server/src/main/java/com/ish/server/JettyHttp2ServerConfig.java) with custom HTTP/2 server connector and provides implementation 
of [HttpInvokerServiceExporter](https://github.com/thinline72/rpc-example/blob/master/server/src/main/java/com/ish/server/Http2InvokerServiceExporter.java) for using predefined SerializationService.

## Client Module

This module has custom [Http2InvokerRequestExecutor](https://github.com/thinline72/rpc-example/blob/master/client/src/main/java/com/ish/client/Http2InvokerRequestExecutor.java) implementation of AbstractHttpInvokerRequestExecutor with Jetty HTTP/2 Client. 
As it uses prior knowledge about HTTP/2 on server-side you could try it without ALPN. Also it shows how to combine 
Spring DI container with Guice through [SpringModule](https://github.com/thinline72/rpc-example/blob/master/client/src/main/java/com/ish/client/SpringModule.java)