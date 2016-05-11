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


import com.ish.rpc.Account;
import com.ish.rpc.AccountService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountServiceImpl implements AccountService {

    private static final Map<String, List<Account>> accountMap = new ConcurrentHashMap<>();

    @Override
    public void insertAccount(Account account) {
        if (accountMap.containsKey(account.getName())) {
            accountMap.get(account.getName()).add(account);
        } else {
            accountMap.put(account.getName(), new ArrayList<>(Collections.singleton(account)));
        }
    }

    @Override
    public List<Account> getAccounts(String name) {
        return accountMap.get(name);
    }

}
