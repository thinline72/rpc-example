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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ish.rpc.Account;
import com.ish.rpc.AccountService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Http2BootClient {

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", Http2BootClient.class.getResource("/keystore").getPath());

        // Run SpringBoot app
        ConfigurableApplicationContext context = SpringApplication.run(Http2BootClient.class, args);

        // Create Guice Injector
        Injector injector = Guice.createInjector(new SpringModule(context));

        AccountService accountService = injector.getInstance(AccountService.class);

        insertAccounts(accountService);
        printAccountsInformation(accountService);
    }

    public static void insertAccounts(AccountService accountService) {
        Account emma = new Account("Emma Doe");
        emma.setEmail("emmadoe@example.org");
        emma.setPhone("123-45-67");

        Account john1 = new Account("John Doe");
        john1.setEmail("johndoe1@example.org");
        john1.setPhone("234-56-78");

        Account john2 = new Account("John Doe");
        john2.setEmail("johndoe2@example.org");
        john2.setPhone("345-67-89");

        accountService.insertAccount(emma);
        accountService.insertAccount(john1);
        accountService.insertAccount(john2);
    }

    public static void printAccountsInformation(AccountService accountService) {
        accountService.getAccounts("Emma Doe").forEach(System.out::println);
        accountService.getAccounts("John Doe").forEach(System.out::println);
    }

}
