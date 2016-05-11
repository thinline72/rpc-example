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

import com.ish.rpc.AccountService;
import com.ish.rpc.ProtostuffSerializationServiceImpl;
import com.ish.rpc.SerializationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

@Configuration
public class ServerAppConfig {

    @Bean
    public AccountService accountService() {
        return new AccountServiceImpl();
    }

    @Bean
    public SerializationService serializationService() {
        return new ProtostuffSerializationServiceImpl();
    }

    @Bean(name = "/rpc/account")
    public HttpInvokerServiceExporter http2ServiceExporter() {
        Http2InvokerServiceExporter exporter = new Http2InvokerServiceExporter();
        exporter.setService(accountService());
        exporter.setServiceInterface(AccountService.class);
        return exporter;
    }

}
