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

package com.ish.server;

import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.stereotype.Component;

import static org.eclipse.jetty.util.resource.Resource.newClassPathResource;

/**
 * Jetty HTTP/2 Server customization.
 */
@Component
public class JettyHttp2ServerConfig implements EmbeddedServletContainerCustomizer {

    private final ServerProperties serverProperties;

    @Autowired
    public JettyHttp2ServerConfig(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        JettyEmbeddedServletContainerFactory factory = (JettyEmbeddedServletContainerFactory) container;

        factory.addServerCustomizers(new JettyHttp2ServerCustomize());
    }

    private class JettyHttp2ServerCustomize implements JettyServerCustomizer {

        @Override
        public void customize(Server server) {
            // HTTPS Configuration
            HttpConfiguration httpsConfig = new HttpConfiguration();
            httpsConfig.setSecureScheme("https");
            httpsConfig.setSecurePort(8443);
            httpsConfig.addCustomizer(new SecureRequestCustomizer());

            // SSL Context Factory for HTTPS and HTTP/2
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStoreResource(newClassPathResource("keystore"));
            sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
            sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
            sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);

            // SSL Connection Factory
            SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, "h2");

            // HTTP/2 Connector
            ServerConnector http2Connector = new ServerConnector(server, ssl, new HTTP2ServerConnectionFactory(httpsConfig));
            http2Connector.setPort(8443);
            server.addConnector(http2Connector);
        }

    }

}
