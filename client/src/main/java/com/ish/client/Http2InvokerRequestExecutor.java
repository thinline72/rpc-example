/*****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ****************************************************************/

package com.ish.client;

import com.ish.rpc.SerializationService;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.HTTP2ClientConnectionFactory;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.AbstractHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * This implementation of {@link AbstractHttpInvokerRequestExecutor} uses Jetty HTTP2 Client (high-level API) and
 * directly specifies HTTP/2 protocol. It works without ALPN.
 */
public class Http2InvokerRequestExecutor extends AbstractHttpInvokerRequestExecutor {

    @Autowired
    protected SerializationService serializationService;

    protected HttpClient httpClient;

    protected int connectTimeout = -1;
    protected int readTimeout = -1;

    public Http2InvokerRequestExecutor() throws Exception {
        httpClient = new HttpClient(getClientTransport(), new SslContextFactory());
        httpClient.start();
    }

    @Override
    protected RemoteInvocationResult doExecuteRequest(
            HttpInvokerClientConfiguration config, ByteArrayOutputStream baos)
            throws Exception {

        if (this.connectTimeout >= 0) {
            httpClient.setConnectTimeout(this.connectTimeout);
        }

        if (this.readTimeout >= 0) {
            httpClient.setIdleTimeout(this.readTimeout);
        }

        ContentResponse response = httpClient.newRequest(config.getServiceUrl())
                .method(HttpMethod.POST)
                .content(new BytesContentProvider(baos.toByteArray()))
                .send();

        if (response.getStatus() >= 300) {
            throw new IOException(
                    "Did not receive successful HTTP response: status code = " + response.getStatus() +
                            ", status message = [" + response.getReason() + "]");
        }

        return readRemoteInvocationResult(new ByteArrayInputStream(response.getContent()), config.getCodebaseUrl());
    }

    @Override
    protected RemoteInvocationResult readRemoteInvocationResult(InputStream is, String codebaseUrl) throws IOException, ClassNotFoundException {
        return serializationService.deserialize(is, RemoteInvocationResult.class);
    }

    @Override
    protected void writeRemoteInvocation(RemoteInvocation invocation, OutputStream os) throws IOException {
        serializationService.serialize(invocation, os);
    }

    protected HttpClientTransportOverHTTP2 getClientTransport() {
        return new HttpClientTransportOverHTTP2(new HTTP2Client()) {
            @Override
            public Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
                return new HTTP2ClientConnectionFactory().newConnection(endPoint, context);
            }
        };
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void close() throws Exception {
        httpClient.stop();
    }

    @PreDestroy
    public void onClose() throws Exception {
        close();
    }
}
